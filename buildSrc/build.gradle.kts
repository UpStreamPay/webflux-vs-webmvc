repositories {
    gradlePluginPortal()
    maven { url = uri("https://repo.spring.io/milestone") }
}
plugins {
    `kotlin-dsl`
}
dependencies {
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.22.0")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.2.0-RC2")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.3")
    implementation("com.google.cloud.tools:jib-gradle-plugin:3.4.0")
    implementation("com.google.cloud.tools:jib-layer-filter-extension-gradle:0.3.0")

}


