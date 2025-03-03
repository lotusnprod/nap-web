val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotlinxHtmlVersion: String by project


plugins {
    kotlin("jvm") version "2.1.10"
    id("io.ktor.plugin") version "3.1.1"
}

kotlin {
    jvmToolchain(17)
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

    implementation("org.apache.jena:apache-jena-libs:5.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinxHtmlVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html:$kotlinxHtmlVersion")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests-jvm")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
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
