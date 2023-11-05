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
include("shared")
include("web-test")
include("webmvc")
include("webflux")
include("discount-api")
include("data-generator")
include("load-test")