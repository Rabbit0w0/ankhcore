dependencies {
  api(project(":api"))
  compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT")
  api(project(":libs:shadow-spring-boot-loader", configuration = "shadow"))
}