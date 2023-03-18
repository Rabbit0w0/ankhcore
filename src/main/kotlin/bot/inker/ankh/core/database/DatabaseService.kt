package bot.inker.ankh.core.database

import bot.inker.ankh.core.api.plugin.PluginLifeCycle
import bot.inker.ankh.core.api.plugin.annotations.SubscriptLifecycle
import bot.inker.ankh.core.common.config.AnkhConfig
import bot.inker.ankh.core.common.dsl.logger
import org.bukkit.event.EventPriority
import org.hibernate.SessionFactory
import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.boot.registry.classloading.internal.ClassLoaderServiceImpl
import org.hibernate.boot.registry.classloading.internal.TcclLookupPrecedence
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService
import org.hibernate.cfg.AvailableSettings
import org.hibernate.cfg.Configuration
import org.hibernate.hikaricp.internal.HikariCPConnectionProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseService @Inject private constructor(
  private val config: AnkhConfig,
) {
  private val logger by logger()
  private val entityClasses = LinkedHashSet<Class<*>>()
  lateinit var sessionFactory: SessionFactory
    private set

  fun registerEntity(clazz: Class<*>) {
    entityClasses.add(clazz)
  }

  @SubscriptLifecycle(PluginLifeCycle.ENABLE)
  private fun onEnable() {
    val database = config.database
    val configuration = Configuration()
    configuration.setProperty(
      AvailableSettings.CONNECTION_PROVIDER,
      HikariCPConnectionProvider::class.qualifiedName
    )
    configuration.setProperty(AvailableSettings.DRIVER, database.driver.driverClass)
    configuration.setProperty(AvailableSettings.DIALECT, database.driver.dialectClass)
    configuration.setProperty(AvailableSettings.URL, database.url)
    configuration.setProperty(AvailableSettings.USER, database.username)
    configuration.setProperty(AvailableSettings.PASS, database.password)
    configuration.setProperty(AvailableSettings.HBM2DDL_AUTO, "update")
    configuration.setProperty(AvailableSettings.AUTO_CLOSE_SESSION, "true")

    entityClasses.forEach(configuration::addAnnotatedClass)

    val serviceRegistry = StandardServiceRegistryBuilder()
      .applySettings(configuration.properties)
      .addService(
        ClassLoaderService::class.java,
        ClassLoaderServiceImpl(entityClasses.map { it.classLoader }.distinct(), TcclLookupPrecedence.NEVER)
      )
      .build()
    sessionFactory = configuration.buildSessionFactory(serviceRegistry)
  }

  @SubscriptLifecycle(PluginLifeCycle.DISABLE, priority = EventPriority.MONITOR)
  private fun onDisable() {
    sessionFactory.close()
  }
}