rootProject.name = "webflux-vs-webmvc"
pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version("0.4.0")
}
include("webmvc")
include("discount-api")
include("data-generator")
include("load-test")