plugins {
    id("webflux-vs-webmvc.java-conventions")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot:3.2.0-RC2")
    implementation("org.springframework.boot:spring-boot-starter-validation:3.2.0-RC2")
}
