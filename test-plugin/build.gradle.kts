plugins {
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
  id("net.minecrell.plugin-yml.bukkit") version ("0.5.1")
}

bukkit {
  name = "ankh-test-plugin"
  main = "org.inksnow.ankh.testplugin.TestBukkitPluginLoader"
  load = net.minecrell.pluginyml.bukkit.BukkitPluginDescription.PluginLoadOrder.STARTUP
  apiVersion = "1.16"
  depend = listOf("ankh-core")
  authors = listOf("InkerBot")
}

configurations {
  create("ankhShadow")
  create("ankhApi")
  create("ankhImpl")
}

dependencies {
  compileOnly(kotlin("stdlib"))
  compileOnly(project(":"))
  compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
}

tasks.jar {
  entryCompression = ZipEntryCompression.STORED

  manifest {
    attributes["Main-Class"] = "org.inksnow.ankh.testplugin.TestPluginMain"
  }

  from(configurations.getByName("ankhShadow").map {
    if (it.isFile) {
      zipTree(it)
    } else {
      it
    }
  })

  with(copySpec {
    from(configurations.getByName("ankhApi"))
    into("ankh-api")
  })

  with(copySpec {
    from(configurations.getByName("ankhImpl"))
    into("ankh-impl")
  })
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
}