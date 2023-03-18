package bot.inker.ankh.loader.internal;

import bot.inker.ankh.core.api.plugin.*;
import bot.inker.ankh.loader.AnkhClassLoader;
import bot.inker.ankh.loader.libs.archive.JarFileArchive;
import bot.inker.ankh.loader.libs.jar.JarFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginAwareness;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.PluginClassLoader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.AbstractConstruct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.CodeSigner;
import java.util.*;
import java.util.function.Consumer;
import java.util.jar.JarEntry;

public final class AnkhBukkitPluginInternal implements AnkhBukkitPlugin.$internal$actions$ {
  private static final Yaml YAML = new Yaml(new SafeConstructor() {
    {
      yamlConstructors.put(null, new AbstractConstruct() {
        @Override
        public Object construct(final Node node) {
          if (!node.getTag().startsWith("!@")) {
            // Unknown tag - will fail
            return SafeConstructor.undefinedConstructor.construct(node);
          }
          // Unknown awareness - provide a graceful substitution
          return new PluginAwareness() {
            @Override
            public String toString() {
              return node.toString();
            }
          };
        }
      });
      for (final PluginAwareness.Flags flag : PluginAwareness.Flags.values()) {
        yamlConstructors.put(new Tag("!@" + flag.name()), new AbstractConstruct() {
          @Override
          public PluginAwareness.Flags construct(final Node node) {
            return flag;
          }
        });
      }
    }
  });

  static {
    try {
      AnkhBukkitPlugin.$internal$actions$.thisRef.set(new AnkhBukkitPluginInternal());
    } catch (Exception e) {
      throw uncheck(e);
    }
  }

  private final Field pluginClassLoaderFileField;
  private final Field pluginClassLoaderJarField;
  private final Field pluginClassLoaderDescriptionField;
  private final Map<String, AnkhClassLoader> classLoaderByName = Collections.synchronizedMap(new HashMap<>());

  private AnkhBukkitPluginInternal() throws Exception {
    this.pluginClassLoaderFileField = PluginClassLoader.class.getDeclaredField("file");
    this.pluginClassLoaderFileField.setAccessible(true);

    this.pluginClassLoaderJarField = PluginClassLoader.class.getDeclaredField("jar");
    this.pluginClassLoaderJarField.setAccessible(true);

    this.pluginClassLoaderDescriptionField = PluginClassLoader.class.getDeclaredField("description");
    this.pluginClassLoaderDescriptionField.setAccessible(true);
  }

  public static void ensureLoaded() {
    //
  }

  private static <T extends Throwable, R extends RuntimeException> R uncheck(Throwable e) throws T {
    throw (T) e;
  }

  @Override
  public AnkhPluginContainer initial(Class<? extends AnkhBukkitPlugin> mainClass) {
    try {
      return initialImpl(mainClass);
    } catch (Exception e) {
      Bukkit.getServer().shutdown();
      throw uncheck(e);
    }
  }

  private AnkhPluginContainer initialImpl(Class<? extends AnkhBukkitPlugin> mainClass) throws Exception {
    PluginClassLoader pluginClassLoader = (PluginClassLoader) mainClass.getClassLoader();
    File file = (File) pluginClassLoaderFileField.get(pluginClassLoader);

    DelegateJarFile delegateJarFile = new DelegateJarFile(file);
    JarFile selfJarFile = new JarFile(file);

    AnkhPluginYml pluginYml = readPluginYml(selfJarFile);
    PluginDescriptionFile pluginDescriptionFile = (PluginDescriptionFile) pluginClassLoaderDescriptionField.get(pluginClassLoader);
    boolean selfAsApi = pluginYml.isAnkhSelfAsApi();
    Map<String, ClasspathLoadType> ankhClasspath = pluginYml.getAnkhClasspath();

    List<URL> implUrlList = new ArrayList<>();
    if (selfAsApi) {
      delegateJarFile.add(selfJarFile);
    } else {
      implUrlList.add(new JarFileArchive(selfJarFile).getUrl());
    }
    scanJar(selfJarFile, delegateJarFile::add, jarFile -> {
      try {
        implUrlList.add(new JarFileArchive(jarFile).getUrl());
      } catch (MalformedURLException e) {
        throw uncheck(e);
      }
    });

    pluginClassLoaderJarField.set(pluginClassLoader, delegateJarFile);

    ClassLoader parentClassLoader = classLoaderByName.get("ankh-core");
    if (parentClassLoader == null) {
      parentClassLoader = pluginClassLoader;
    }
    AnkhClassLoader ankhClassLoader = new AnkhClassLoader(
      pluginYml,
      new JarFileArchive(file),
      implUrlList.toArray(new URL[0]),
      parentClassLoader
    );
    ankhClasspath.forEach(ankhClassLoader::registerLoadType);

    classLoaderByName.put(pluginDescriptionFile.getName(), ankhClassLoader);

    Consumer<String> dependConsumer = dependName -> {
      AnkhClassLoader dependClassLoader = classLoaderByName.get(dependName);
      if (dependClassLoader != null) {
        ankhClassLoader.registerDependClassLoader(dependClassLoader);
      }
    };
    pluginDescriptionFile.getDepend().forEach(dependConsumer);
    pluginDescriptionFile.getSoftDepend().forEach(dependConsumer);

    Class<?> anhkPluginManagerClass = Class.forName("bot.inker.ankh.core.plugin.AnkhPluginManagerImpl", true, ankhClassLoader);
    AnkhPluginManager pluginManager = (AnkhPluginManager) anhkPluginManagerClass.getField("INSTANCE").get(null);
    return pluginManager.register(mainClass, file, ankhClassLoader, pluginDescriptionFile, pluginYml);
  }

  // Also ensure all class required in AnhkClassLoader are laoded.
  private AnkhPluginYml readPluginYml(JarFile jarFile) throws IOException, InvalidDescriptionException {
    JarEntry pluginYmlEntry = jarFile.getJarEntry("plugin.yml");
    if (pluginYmlEntry == null || pluginYmlEntry.isDirectory()) {
      throw new IllegalArgumentException("'plugin.yml' in '" + jarFile.getName() + "' not found");
    }
    CodeSigner[] codeSigners = pluginYmlEntry.getCodeSigners();
    try (Reader reader = new InputStreamReader(jarFile.getInputStream(pluginYmlEntry), StandardCharsets.UTF_8)) {
      return new AnkhPluginYml(YAML.load(reader));
    }
  }

  private void scanJar(JarFile jarFile, Consumer<JarFile> apiConsumer, Consumer<JarFile> implConsumer) throws IOException {
    for (JarEntry entry : jarFile) {
      String entryName = entry.getName();
      if (!entryName.endsWith(".jar") && !entryName.endsWith(".zip")) {
        continue;
      }
      Consumer<JarFile> targetConsumer;
      if (entryName.startsWith("ankh-api/")) {
        targetConsumer = apiConsumer;
      } else if (entryName.startsWith("ankh-impl/")) {
        targetConsumer = implConsumer;
      } else {
        continue;
      }
      JarFile nestedJarFile = jarFile.getNestedJarFile(entry);
      targetConsumer.accept(nestedJarFile);
      scanJar(nestedJarFile, apiConsumer, implConsumer);
    }
  }
}
