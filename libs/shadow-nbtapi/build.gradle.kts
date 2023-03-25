plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
  api("de.tr7zw:item-nbt-api:2.11.1")
}

tasks.shadowJar {
  relocate("de.tr7zw.changeme.nbtapi", "org.inksnow.ankh.core.libs.nbtapi")
}

tasks.assemble {
  dependsOn(tasks.shadowJar)
}