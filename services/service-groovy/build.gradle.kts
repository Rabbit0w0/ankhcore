group = "org.inksnow.ankh.groovy"

configurations {
  create("ankhShadow")
  create("ankhApi")
  create("ankhImpl").extendsFrom(getByName("runtimeClasspath"))
}

dependencies {
  implementation("org.apache.groovy:groovy:4.0.10")
  compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")
  compileOnly(project(":"))
}

tasks.jar {
  entryCompression = ZipEntryCompression.STORED
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
    from(configurations.getByName("ankhImpl").filter {
      !configurations.getByName("ankhShadow").contains(it) &&
          !configurations.getByName("ankhApi").contains(it)
    })
    into("ankh-impl")
  })
}

tasks.publish {
  enabled = false
}

tasks.javadocJar {
  enabled = false
}

tasks.sourcesJar {
  enabled = false
}