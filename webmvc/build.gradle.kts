plugins {
    id("webflux-vs-webmvc.spring-boot-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation(project(":shared"))
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    testImplementation(testFixtures(project(":web-test")))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
//    testImplementation("org.testcontainers:postgresql:1.19.1")
//    testImplementation("org.springframework.boot:spring-boot-starter-test")
//    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
//    testImplementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
//    testImplementation("org.springframework.boot:spring-boot-starter-data-jdbc")
//    testImplementation("org.springframework.boot:spring-boot-starter-jdbc")
//    testImplementation("org.testcontainers:postgresql:1.19.1")
}
