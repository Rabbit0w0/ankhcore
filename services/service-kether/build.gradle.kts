plugins {
  id("io.izzel.taboolib") version "1.56"
  id("org.jetbrains.kotlin.jvm")
}

group = "org.inksnow.ankh.kether"

taboolib {
  description {
    name = "ankh-kether"
    version = project.version.toString()
    bukkitApi("1.16")
    dependencies {
      name("ankh-core")
    }
    contributors {
      name("inkerbot")
    }
  }
  install("common")
  install("common-5")
  install("platform-bukkit")
  install("module-configuration")
  install("module-kether")
  install("module-chat")
  install("module-nms")
  install("module-nms-util")
  install("module-lang")

  version = "6.0.10-98"
}

dependencies {
  compileOnly(kotlin("stdlib"))
  compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
  compileOnly(project(":api"))
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
}