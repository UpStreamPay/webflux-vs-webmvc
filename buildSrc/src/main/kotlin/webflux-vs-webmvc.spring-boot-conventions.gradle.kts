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
        jvmFlags = listOf("-XX:+PrintFlagsFinal", "-XX:+PrintCommandLineFlags", "-XX:+AlwaysActAsServerClassMachine")
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
