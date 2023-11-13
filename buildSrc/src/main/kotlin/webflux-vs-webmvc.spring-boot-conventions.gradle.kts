plugins {
    id("webflux-vs-webmvc.java-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    id("com.google.cloud.tools.jib")
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

jib {
    from {
        image = "eclipse-temurin:21"
    }
    container {
        jvmFlags = listOf("-XX:+PrintCommandLineFlags", "-XX:+AlwaysActAsServerClassMachine", "-XX:+ExitOnOutOfMemoryError", "-XX:MaxRAMPercentage=40")
    }
    pluginExtensions {
        pluginExtension {
            implementation = "com.google.cloud.tools.jib.gradle.extension.layerfilter.JibLayerFilterExtension"
            configuration(Action<com.google.cloud.tools.jib.gradle.extension.layerfilter.Configuration> {
                filters {
                    filter {
                        glob = "**/spring-boot-devtools-*.jar"
                    }
                    filter {
                        glob = "**/spring-boot-docker-compose-*.jar"
                    }
                }
            })
        }
    }
}
