val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kotlinxHtml_version: String by project
val jena_version: String by project
val serialization_version: String by project


plugins {
    kotlin("jvm") version "2.4.0"
    id("io.ktor.plugin") version "3.5.2"
    id("com.github.ben-manes.versions") version "0.54.0"
    kotlin("plugin.serialization") version "2.4.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

kotlin {
    jvmToolchain(26)
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

// stack/seed/ is the single source of truth for RDF fixtures: the local dev stack
// loads it through stack/fuseki/seed-entrypoint.sh, and the in-memory Fuseki test
// server reads the same files off the test classpath.
sourceSets["test"].resources.srcDir(layout.projectDirectory.dir("stack/seed"))

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
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    implementation("ch.qos.logback:logback-classic:$logback_version")
    
    // Testing dependencies
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-content-negotiation")
    testImplementation("org.apache.jena:jena-fuseki-main:$jena_version")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0")
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

kover {
    reports {
        total {
            html {
                onCheck = false
            }
            xml {
                onCheck = false
            }
        }
    }
}
