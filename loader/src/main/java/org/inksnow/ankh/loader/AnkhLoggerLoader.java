package org.inksnow.ankh.loader;

import org.bukkit.plugin.java.PluginClassLoader;
import org.inksnow.ankh.loader.libs.jar.JarFile;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.jar.JarEntry;

public class AnkhLoggerLoader extends URLClassLoader {
  private static final String SERVICES_PATH = "META-INF/services/";
  private static AnkhLoggerLoader INSTANCE;

  static {
    ClassLoader.registerAsParallelCapable();
  }

  public AnkhLoggerLoader(URL[] urls, ClassLoader parent) {
    super(urls, parent);
  }

  public static AnkhLoggerLoader instance() {
    return INSTANCE;
  }

  public static void initial(ClassLoader pluginClassLoader) {
    try {
      initialImpl(pluginClassLoader);
    } catch (Exception e) {
      throw uncheck(e);
    }
  }

  private static void initialImpl(ClassLoader pluginClassLoader) throws NoSuchFieldException, IllegalAccessException, IOException {
    Field pluginClassLoaderFileField = PluginClassLoader.class.getDeclaredField("file");
    pluginClassLoaderFileField.setAccessible(true);
    File pluginFile = (File) pluginClassLoaderFileField.get(pluginClassLoader);

    List<URL> urlList = new ArrayList<>();

    JarFile pluginJarFile = new JarFile(pluginFile);
    Enumeration<JarEntry> enumeration = pluginJarFile.entries();
    while (enumeration.hasMoreElements()) {
      JarEntry entry = enumeration.nextElement();
      if (entry.getName().startsWith("ankh-logger/") && entry.getName().endsWith(".jar") && !entry.isDirectory()) {
        urlList.add(pluginJarFile.getNestedJarFile(entry).getUrl());
      }
    }

    INSTANCE = new AnkhLoggerLoader(urlList.toArray(URL[]::new), pluginClassLoader);
  }

  private static <T extends Throwable, R extends RuntimeException> R uncheck(Throwable e) throws T {
    throw (T) e;
  }

  @Override
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    synchronized (getClassLoadingLock(name)) {
      Class<?> c = findLoadedClass(name);
      if (c == null) {
        try {
          c = findClass(name);
        } catch (ClassNotFoundException e) {
          //
        }
      }
      if (c == null) {
        try {
          c = getParent().loadClass(name);
        } catch (ClassNotFoundException e) {
          //
        }
      }
      if (c == null) {
        throw new ClassNotFoundException(name);
      }
      if (resolve) {
        resolveClass(c);
      }
      return c;
    }
  }

  @Override
  public URL getResource(String name) {
    URL url = null;
    if (url == null) {
      url = findResource(name);
    }
    if (url == null) {
      url = getParent().getResource(name);
    }
    return url;
  }

  @Override
  public Enumeration<URL> getResources(String name) throws IOException {
    return new CompoundEnumeration<>((Enumeration<URL>[]) new Enumeration<?>[]{
      findResources(name),
      getParent().getResources(name)
    });
  }

  @Override
  public URL findResource(String name) {
    if (name.startsWith(SERVICES_PATH)) {
      try {
        return findResources(name).nextElement();
      } catch (IOException e) {
        throw uncheck(e);
      }
    }
    return super.findResource(name);
  }

  @Override
  public Enumeration<URL> findResources(String name) throws IOException {
    if (name.startsWith(SERVICES_PATH)) {
      return super.findResources("META-INF/ankh-services/" + name.substring(SERVICES_PATH.length()));
    } else {
      return super.findResources(name);
    }
  }

  private static final class CompoundEnumeration<E> implements Enumeration<E> {
    private final Enumeration<E>[] enums;
    private int index;

    public CompoundEnumeration(Enumeration<E>[] enums) {
      this.enums = enums;
    }

    private boolean next() {
      while (index < enums.length) {
        if (enums[index] != null && enums[index].hasMoreElements()) {
          return true;
        }
        index++;
      }
      return false;
    }

    public boolean hasMoreElements() {
      return next();
    }

    public E nextElement() {
      if (!next()) {
        throw new NoSuchElementException();
      }
      return enums[index].nextElement();
    }
  }
}
