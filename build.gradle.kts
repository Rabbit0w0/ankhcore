plugins {
  id("java-library")
  id("maven-publish")
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

allprojects {
  if (project.buildscript.sourceFile?.exists() != true) {
    project.tasks.forEach { it.enabled = false }
    return@allprojects
  }

  apply(plugin = "java-library")
  apply(plugin = "maven-publish")

  group = if (rootProject == project) {
    "org.inksnow.ankh"
  } else {
    "org.inksnow.ankh.core"
  }


  val buildNumber = System.getenv("BUILD_NUMBER")
  version = if (buildNumber == null) {
    "1.0-dev-SNAPSHOT"
  } else {
    "1.0-${System.getenv("BUILD_NUMBER")}-SNAPSHOT"
  }

  repositories {
    mavenCentral()
    maven("https://repo.inker.bot/repository/maven-snapshots/")
    maven("https://repo.codemc.io/repository/maven-public/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
  }

  java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
    withJavadocJar()
  }

  publishing {
    repositories {
      if (project.version.toString().endsWith("-SNAPSHOT")) {
        maven("https://repo.inker.bot/repository/maven-snapshots/"){
          credentials {
            username = System.getenv("NEXUS_USERNAME")
            password = System.getenv("NEXUS_PASSWORD")
          }
        }
      }else{
        maven("https://repo.inker.bot/repository/maven-releases/"){
          credentials {
            username = System.getenv("NEXUS_USERNAME")
            password = System.getenv("NEXUS_PASSWORD")
          }
        }
        maven("https://s0.blobs.inksnow.org/") {
          credentials {
            username = ""
            password = System.getenv("IREPO_PASSWORD")
          }
        }
      }
    }

    publications {
      create<MavenPublication>("mavenJar") {
        artifactId = project.path
          .removePrefix(":")
          .replace(':', '-')
          .ifEmpty { "core" }

        pom {
          name.set("AnkhCore${project.name}")
          description.set("A bukkit plugin loader named AnkhCore")
          url.set("https://github.com/InkerBot/AnkhCore")
          properties.set(mapOf())
          licenses {
            license {
              name.set("MIT")
              url.set("https://opensource.org/licenses/MIT")
              distribution.set("repo")
            }
          }
          developers {
            developer {
              id.set("inkerbot")
              name.set("InkerBot")
              email.set("im@inker.bot")
            }
          }
          scm {
            connection.set("scm:git:git://github.com/InkerBot/AnkhCore.git")
            developerConnection.set("scm:git:ssh://github.com/InkerBot/AnkhCore.git")
            url.set("https://github.com/InkerBot/AnkhCore")
          }
        }
        from(components["java"])
      }
    }
  }


  tasks.javadoc {
    options.encoding = "UTF-8"
    (options as CoreJavadocOptions).addStringOption("Xdoclint:none")
  }

  tasks.compileJava {
    options.encoding = "UTF-8"
  }
}

dependencies {
  // project base
  api(project(":api"))
  compileOnly(project(":loader"))
  compileOnly(project(":libs:shadow-spring-boot-loader", configuration = "shadow"))

  @Suppress("VulnerableLibrariesLocal") // We won't include it
  compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

  // kotlin
  api(kotlin("stdlib"))
  api(kotlin("reflect"))

  // base utils
  api("com.google.inject:guice:5.1.0")
  api("bot.inker.acj:runtime:1.3")
  api("bot.inker.aig:all:1.1-SNAPSHOT")
  api("org.ow2.asm:asm:9.4")
  api("org.ow2.asm:asm-tree:9.4")
  api("it.unimi.dsi:fastutil:8.5.12")

  // database
  api("org.hibernate.orm:hibernate-core:6.1.7.Final")
  api("org.hibernate.orm:hibernate-hikaricp:6.1.7.Final")
  api("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")

  // database drivers
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.0.3")
  runtimeOnly("org.postgresql:postgresql:42.3.8")
  @Suppress("VulnerableLibrariesLocal") // It's a fake cve
  runtimeOnly("com.h2database:h2:2.1.214")

  // script
  api("org.beanshell:bsh:3.0.0-SNAPSHOT")
  api("org.apache.groovy:groovy:4.0.10")

  // shadow depends
  api(project(":libs:shadow-paper-lib", configuration = "shadow"))
  api(project(":libs:shadow-nbtapi", configuration = "shadow"))

  // other plugins (api usage)
  compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")

  // For docs, use no-shadow version
  api("io.github.baked-libs:dough-api:1.2.0")

  // lombok
  compileOnly("org.projectlombok:lombok:1.18.26")
  annotationProcessor("org.projectlombok:lombok:1.18.26")

  // logger binding
  implementation("org.apache.logging.log4j:log4j-to-slf4j:2.20.0")
  api("org.slf4j:slf4j-api:2.0.6")
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
}