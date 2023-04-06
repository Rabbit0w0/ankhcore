configurations {
  create("ankhShadow")
  create("ankhApi")
  create("ankhImpl")
  create("ankhLogger")
}

dependencies {
  add("ankhShadow", project(":loader"))

  add("ankhImpl", project(":")) {
    exclude(group = "org.slf4j", module = "slf4j-api")
  }
  add("ankhLogger", project(":loader:logger"))
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
    from(configurations.getByName("ankhApi").filter {
      !configurations.getByName("ankhShadow").contains(it)
    })
    into("ankh-api")
  })

  with(copySpec {
    from(configurations.getByName("ankhImpl").filter {
      !configurations.getByName("ankhShadow").contains(it) &&
        !configurations.getByName("ankhApi").contains(it)
    })
    into("ankh-impl")
  })

  with(copySpec {
    from(configurations.getByName("ankhLogger"))
    into("ankh-logger")
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