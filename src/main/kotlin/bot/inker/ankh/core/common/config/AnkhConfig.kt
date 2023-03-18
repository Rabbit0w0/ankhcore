package bot.inker.ankh.core.common.config

import bot.inker.ankh.core.api.AnkhCoreLoader
import org.bukkit.configuration.ConfigurationSection
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnkhConfig @Inject private constructor(
  private val loaderPlugin: AnkhCoreLoader,
) {
  init {
    loaderPlugin.saveDefaultConfig()
  }

  private val configuration = loaderPlugin.config

  val database = DatabaseBean(configuration.getConfigurationSection("database")!!)

  val worldStorage = WorldStorageBean(configuration.getConfigurationSection("world-storage")!!)

  val tickRate = configuration.getInt("tick-rate")

  class DatabaseBean(configuration: ConfigurationSection) {
    val driver = configuration.getString("driver")!!.let(DriverType::valueOf)
    val url = configuration.getString("url")!!
    val username = configuration.getString("username")!!
    val password = configuration.getString("password")!!

    enum class DriverType(
      val driverClass: String,
      val dialectClass: String,
    ) {
      H2("org.h2.Driver", "org.hibernate.dialect.H2Dialect"),
      MARIADB("org.mariadb.jdbc.Driver", "org.hibernate.dialect.MariaDBDialect"),
      POSTGRESQL("org.postgresql.Driver", "org.hibernate.dialect.PostgreSQLDialect");
    }
  }

  class WorldStorageBean(configuration: ConfigurationSection) {
    val backend = configuration.getString("backend")!!
  }
}