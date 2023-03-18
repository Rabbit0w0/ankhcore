dependencies {
  implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.20.0") {
    exclude("*")
  }
  implementation("org.slf4j:slf4j-jdk14:2.0.6") {
    exclude("*")
  }
  implementation("org.slf4j:slf4j-api:2.0.6")
}