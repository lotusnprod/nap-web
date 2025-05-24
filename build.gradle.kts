val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotlinxHtml_version: String by project
val jena_version: String by project
val serialization_version: String by project


plugins {
    kotlin("jvm") version "2.2.0-RC"
    id("io.ktor.plugin") version "3.1.3"
    id("com.github.ben-manes.versions") version "0.52.0"
    kotlin("plugin.serialization") version "2.2.0-RC"
}

kotlin {
    jvmToolchain(24)
}

group = "net.nprod.nap"
version = "0.0.2"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm")
    implementation("io.ktor:ktor-server-netty-jvm")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-client-cio")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-content-negotiation")

    implementation("org.apache.jena:apache-jena-libs:$jena_version")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtml_version")
    implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinxHtml_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    
    // Testing dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-content-negotiation")
    testImplementation("org.apache.jena:jena-fuseki-main:$jena_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
}

distributions {
    main {
        contents {
            // Add a directory to the distribution
            from("assets") {
                into("assets")
            }
        }
    }
}
