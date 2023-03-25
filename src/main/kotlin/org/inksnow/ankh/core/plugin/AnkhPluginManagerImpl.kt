package org.inksnow.ankh.core.plugin

import com.google.inject.AbstractModule
import com.google.inject.Guice
import com.google.inject.name.Names
import org.bukkit.plugin.PluginDescriptionFile
import org.inksnow.ankh.core.api.AnkhCore
import org.inksnow.ankh.core.api.plugin.AnkhBukkitPlugin
import org.inksnow.ankh.core.api.plugin.AnkhPluginContainer
import org.inksnow.ankh.core.api.plugin.AnkhPluginManager
import org.inksnow.ankh.core.api.plugin.AnkhPluginYml
import org.inksnow.ankh.core.plugin.scanner.PluginClassScanner
import org.inksnow.ankh.loader.AnkhClassLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object AnkhPluginManagerImpl : AbstractModule(), AnkhPluginManager {
  private val logger = LoggerFactory.getLogger(AnkhCore.PLUGIN_ID)

  override fun register(
    pluginClass: Class<out AnkhBukkitPlugin>,
    file: File,
    classLoader: ClassLoader,
    descriptionFile: PluginDescriptionFile,
    pluginYml: AnkhPluginYml,
  ): AnkhPluginContainer {
    require(classLoader is AnkhClassLoader) { "ClassLoader require anhkClassLoader" }
    val container = AnkhPluginContainerImpl(pluginClass, classLoader, descriptionFile, pluginYml)

    val scannerInjector = Guice.createInjector({ binder ->
      binder.bind(Logger::class.java).toInstance(logger)
      binder.bind(Class::class.java).annotatedWith(Names.named("pluginClass")).toInstance(pluginClass)
      binder.bind(File::class.java).annotatedWith(Names.named("pluginFile")).toInstance(file)
      binder.bind(AnkhClassLoader::class.java).toInstance(classLoader)
      binder.bind(PluginDescriptionFile::class.java).toInstance(descriptionFile)
      binder.bind(AnkhPluginYml::class.java)
        .annotatedWith(Names.named("pluginYml")).toInstance(pluginYml)

      binder.bind(AnkhPluginContainer::class.java)
        .to(AnkhPluginContainerImpl::class.java)
      binder.bind(AnkhPluginContainerImpl::class.java).toInstance(container)
    }, this)

    scannerInjector.getInstance(PluginClassScanner::class.java).scan()

    return container
  }

  override fun configure() {

  }
}