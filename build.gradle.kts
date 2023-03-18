plugins {
  id("java-library")
  id("maven-publish")
  id("org.jetbrains.kotlin.jvm") version "1.8.10"
}

allprojects {
  apply(plugin = "java-library")
  apply(plugin = "maven-publish")

  group = "bot.inker.ankhcraft"
  version = "1.0.0-SNAPSHOT"

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
  }

  tasks.javadoc {
    options.encoding = "UTF-8"
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

  // database
  api("org.hibernate.orm:hibernate-core:6.1.7.Final")
  api("org.hibernate.orm:hibernate-hikaricp:6.1.7.Final")
  api("org.hibernate.common:hibernate-commons-annotations:6.0.6.Final")
  @Suppress("VulnerableLibrariesLocal") runtimeOnly("com.h2database:h2:2.1.214")
  runtimeOnly("org.mariadb.jdbc:mariadb-java-client:3.0.3")
  runtimeOnly("org.postgresql:postgresql:42.3.8")

  // script
  api("org.beanshell:bsh:3.0.0-SNAPSHOT")
  api("org.apache.groovy:groovy:4.0.9")

  // shadow depends
  api(project(":libs:shadow-paper-lib", configuration = "shadow"))
  api(project(":libs:shadow-nbtapi", configuration = "shadow"))

  // other plugins (api usage)
  compileOnly("me.filoghost.holographicdisplays:holographicdisplays-api:3.0.0")

  // For docs, use no-shadow version
  api("io.github.baked-libs:dough-api:1.2.0")
  api("org.slf4j:slf4j-api:2.0.6")

  // logger binding
  implementation("org.apache.logging.log4j:log4j-to-slf4j:2.20.0")
}

tasks.compileKotlin {
  kotlinOptions {
    jvmTarget = "11"
    freeCompilerArgs = listOf("-Xjvm-default=all")
  }
}