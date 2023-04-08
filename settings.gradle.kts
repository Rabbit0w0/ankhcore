rootProject.name = "ankh-core"
include("api")
include("loader")
include("loader:plugin")
include("loader:logger")

include("test-plugin")
include("gradle-plugin")

include("libs:shadow-spring-boot-loader")
include("libs:shadow-paper-lib")
include("libs:shadow-nbtapi")

include("services:service-kether")
include("services:service-neigeitems")
