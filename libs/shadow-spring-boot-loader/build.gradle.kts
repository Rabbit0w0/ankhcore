plugins {
  id("com.github.johnrengelman.shadow") version "7.1.2"
}

dependencies {
  implementation("org.springframework.boot:spring-boot-loader:2.7.9")
}

tasks.shadowJar {
  relocate("org.springframework.boot.loader", "bot.inker.ankh.loader.libs")
}

tasks.assemble {
  dependsOn(tasks.shadowJar)
}