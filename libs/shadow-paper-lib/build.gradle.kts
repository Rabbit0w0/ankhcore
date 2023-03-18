plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
  implementation("io.papermc:paperlib:1.0.7")
}

tasks.shadowJar {
  relocate("io.papermc.lib", "bot.inker.ankh.core.libs.paperlib")
}

tasks.assemble {
  dependsOn(tasks.shadowJar)
}