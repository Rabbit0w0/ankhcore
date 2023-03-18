package bot.inker.ankh.loader;

import bot.inker.ankh.core.api.plugin.AnkhPluginYml;
import bot.inker.ankh.core.api.plugin.ClasspathLoadType;
import bot.inker.ankh.loader.libs.archive.Archive;
import bot.inker.ankh.loader.libs.jar.Handler;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class AnkhClassLoader extends URLClassLoader {

  private static final int BUFFER_SIZE = 4096;

  static {
    ClassLoader.registerAsParallelCapable();
  }

  private final AnkhPluginYml pluginYml;

  private final Archive rootArchive;

  private final Object packageLock = new Object();

  private volatile DefinePackageCallType definePackageCallType;

  private Map<String, ClasspathLoadType> loadTypeMap = new HashMap<>();
  private List<AnkhClassLoader> dependClassLoaders = new CopyOnWriteArrayList<>();

  /**
   * Create a new {@link AnkhClassLoader} instance.
   *
   * @param rootArchive the root archive or {@code null}
   * @param urls        the URLs from which to load classes and resources
   * @param parent      the parent class loader for delegation
   * @since 2.3.1
   */
  public AnkhClassLoader(AnkhPluginYml pluginYml, Archive rootArchive, URL[] urls, ClassLoader parent) {
    super(urls, parent);
    this.pluginYml = pluginYml;
    this.rootArchive = rootArchive;
  }

  public void registerLoadType(String prefix, ClasspathLoadType loadType) {
    Map<String, ClasspathLoadType> loadTypeMap = new HashMap<>(this.loadTypeMap);
    loadTypeMap.put(prefix, loadType);
    this.loadTypeMap = loadTypeMap;
  }

  public void registerDependClassLoader(AnkhClassLoader dependClassLoader) {
    dependClassLoaders.add(dependClassLoader);
  }

  @Override
  public URL findResource(String name) {
    Handler.setUseFastConnectionExceptions(true);
    try {
      return super.findResource(name);
    } finally {
      Handler.setUseFastConnectionExceptions(false);
    }
  }

  @Override
  public Enumeration<URL> findResources(String name) throws IOException {
    Handler.setUseFastConnectionExceptions(true);
    try {
      return new UseFastConnectionExceptionsEnumeration(super.findResources(name));
    } finally {
      Handler.setUseFastConnectionExceptions(false);
    }
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    Handler.setUseFastConnectionExceptions(true);
    try {
      try {
        definePackageIfNecessary(name);
      } catch (IllegalArgumentException ex) {
        // Tolerate race condition due to being parallel capable
        if (getPackage(name) == null) {
          // This should never happen as the IllegalArgumentException indicates
          // that the package has already been defined and, therefore,
          // getPackage(name) should not return null.
          throw new AssertionError("Package " + name + " has already been defined but it could not be found");
        }
      }
      synchronized (getClassLoadingLock(name)) {
        Class<?> clazz = findLoadedClass(name);

        if (name.startsWith("org.slf4j.")) {
          return AnkhLoggerLoader.instance().loadClass(name);
        }

        ClasspathLoadType loadType = ClasspathLoadType.SELF_FIRST;
        for (Map.Entry<String, ClasspathLoadType> stringLoadTypeEntry : loadTypeMap.entrySet()) {
          if (name.startsWith(stringLoadTypeEntry.getKey())) {
            loadType = stringLoadTypeEntry.getValue();
            break;
          }
        }

        if (loadType.p1 && clazz == null) {
          try {
            clazz = parentLoadClass(name);
          } catch (ClassNotFoundException e) {
            // ignore
          }
        }

        if (loadType.s2 && clazz == null) {
          try {
            clazz = findClass(name);
          } catch (ClassNotFoundException e) {
            // ignore
          }
        }

        if (loadType.p3 && clazz == null) {
          try {
            clazz = parentLoadClass(name);
          } catch (ClassNotFoundException e) {
            // ignore
          }
        }

        if (clazz == null) {
          throw new ClassNotFoundException(name);
        }

        if (resolve) {
          resolveClass(clazz);
        }
        return clazz;
      }
    } finally {
      Handler.setUseFastConnectionExceptions(false);
    }
  }

  private Class<?> parentLoadClass(String name) throws ClassNotFoundException {
    try {
      return getParent().loadClass(name);
    } catch (ClassNotFoundException e) {
      // ignore
    }
    for (AnkhClassLoader dependClassLoader : dependClassLoaders) {
      try {
        return dependClassLoader.loadClass(name);
      } catch (ClassNotFoundException e) {
        // ignore
      }
    }
    throw new ClassNotFoundException(name);
  }

  @Override
  public URL getResource(String name) {
    String filterName = name.replace('/', '.');
    ClasspathLoadType loadType = ClasspathLoadType.SELF_FIRST;
    for (Map.Entry<String, ClasspathLoadType> stringLoadTypeEntry : loadTypeMap.entrySet()) {
      if (filterName.startsWith(stringLoadTypeEntry.getKey())) {
        loadType = stringLoadTypeEntry.getValue();
        break;
      }
    }
    URL url = null;
    if (url == null && loadType.p1) {
      url = getParent().getResource(name);
    }
    if (url == null && loadType.s2) {
      url = findResource(name);
    }
    if (url == null && loadType.p3) {
      url = getParent().getResource(name);
    }
    return url;
  }

  /**
   * Define a package before a {@code findClass} call is made. This is necessary to
   * ensure that the appropriate manifest for nested JARs is associated with the
   * package.
   *
   * @param className the class name being found
   */
  private void definePackageIfNecessary(String className) {
    int lastDot = className.lastIndexOf('.');
    if (lastDot >= 0) {
      String packageName = className.substring(0, lastDot);
      if (getPackage(packageName) == null) {
        try {
          definePackage(className, packageName);
        } catch (IllegalArgumentException ex) {
          // Tolerate race condition due to being parallel capable
          if (getPackage(packageName) == null) {
            // This should never happen as the IllegalArgumentException
            // indicates that the package has already been defined and,
            // therefore, getPackage(name) should not have returned null.
            throw new AssertionError(
              "Package " + packageName + " has already been defined but it could not be found");
          }
        }
      }
    }
  }

  private void definePackage(String className, String packageName) {
    String packageEntryName = packageName.replace('.', '/') + "/";
    String classEntryName = className.replace('.', '/') + ".class";
    for (URL url : getURLs()) {
      try {
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
          JarFile jarFile = ((JarURLConnection) connection).getJarFile();
          if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null
            && jarFile.getManifest() != null) {
            definePackage(packageName, jarFile.getManifest(), url);
            return;
          }
        }
      } catch (IOException ex) {
        // Ignore
      }
    }
  }

  @Override
  protected Package definePackage(String name, Manifest man, URL url) throws IllegalArgumentException {
    synchronized (this.packageLock) {
      return doDefinePackage(DefinePackageCallType.MANIFEST, () -> super.definePackage(name, man, url));
    }
  }

  @Override
  protected Package definePackage(String name, String specTitle, String specVersion, String specVendor,
                                  String implTitle, String implVersion, String implVendor, URL sealBase) throws IllegalArgumentException {
    synchronized (this.packageLock) {
      if (this.definePackageCallType == null) {
        // We're not part of a call chain which means that the URLClassLoader
        // is trying to define a package for our exploded JAR. We use the
        // manifest version to ensure package attributes are set
        Manifest manifest = getManifest(this.rootArchive);
        if (manifest != null) {
          return definePackage(name, manifest, sealBase);
        }
      }
      return doDefinePackage(DefinePackageCallType.ATTRIBUTES, () -> super.definePackage(name, specTitle,
        specVersion, specVendor, implTitle, implVersion, implVendor, sealBase));
    }
  }

  private Manifest getManifest(Archive archive) {
    try {
      return (archive != null) ? archive.getManifest() : null;
    } catch (IOException ex) {
      return null;
    }
  }

  private <T> T doDefinePackage(DefinePackageCallType type, Supplier<T> call) {
    DefinePackageCallType existingType = this.definePackageCallType;
    try {
      this.definePackageCallType = type;
      return call.get();
    } finally {
      this.definePackageCallType = existingType;
    }
  }

  /**
   * Clear URL caches.
   */
  public void clearCache() {
    for (URL url : getURLs()) {
      try {
        URLConnection connection = url.openConnection();
        if (connection instanceof JarURLConnection) {
          clearCache(connection);
        }
      } catch (IOException ex) {
        // Ignore
      }
    }

  }

  private void clearCache(URLConnection connection) throws IOException {
    Object jarFile = ((JarURLConnection) connection).getJarFile();
    if (jarFile instanceof bot.inker.ankh.loader.libs.jar.JarFile) {
      ((bot.inker.ankh.loader.libs.jar.JarFile) jarFile).clearCache();
    }
  }

  public Class<?> define(String name, byte[] b, int off, int len) {
    return defineClass(name, b, off, len);
  }

  public String getPluginName() {
    return pluginYml.getName();
  }

  public AnkhPluginYml getPluginYml() {
    return pluginYml;
  }

  /**
   * The different types of call made to define a package. We track these for exploded
   * jars so that we can detect packages that should have manifest attributes applied.
   */
  private enum DefinePackageCallType {

    /**
     * A define package call from a resource that has a manifest.
     */
    MANIFEST,

    /**
     * A define package call with a direct set of attributes.
     */
    ATTRIBUTES

  }

  private static class UseFastConnectionExceptionsEnumeration implements Enumeration<URL> {

    private final Enumeration<URL> delegate;

    UseFastConnectionExceptionsEnumeration(Enumeration<URL> delegate) {
      this.delegate = delegate;
    }

    @Override
    public boolean hasMoreElements() {
      Handler.setUseFastConnectionExceptions(true);
      try {
        return this.delegate.hasMoreElements();
      } finally {
        Handler.setUseFastConnectionExceptions(false);
      }

    }

    @Override
    public URL nextElement() {
      Handler.setUseFastConnectionExceptions(true);
      try {
        return this.delegate.nextElement();
      } finally {
        Handler.setUseFastConnectionExceptions(false);
      }
    }

  }
}