plugins {
    id("java-test-fixtures")
    id("webflux-vs-webmvc.spring-boot-conventions")
}

dependencies {
    testFixturesImplementation("org.springframework:spring-web")
    testFixturesImplementation(project(":shared"))
    testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-test")
    testFixturesImplementation("org.springframework.boot:spring-boot-testcontainers")
    testFixturesImplementation("org.testcontainers:junit-jupiter")
    testFixturesImplementation("org.testcontainers:postgresql:1.19.1")
    testFixturesImplementation("org.testcontainers:r2dbc:1.19.1")
    testFixturesImplementation("com.squareup.okhttp3:mockwebserver:4.11.0")
}