import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.versions)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kover)
}

kotlin {
    jvmToolchain(libs.versions.jdk.get().toInt())
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
    // Keeps kotlin-reflect on the compiler's version. Without it the graph resolves
    // reflect 2.3.21 against stdlib 2.4.0, and Kotlin does not support that skew.
    implementation(platform(libs.kotlin.bom))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.statusPages)

    implementation(libs.jena.libs)

    // Resilience around the SPARQL endpoint: retry transient failures, open a
    // breaker when Fuseki is down instead of queueing requests against it.
    implementation(libs.resilience4j.circuitbreaker)
    implementation(libs.resilience4j.retry)

    implementation(libs.kotlinx.html)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.logback.classic)

    // Testing dependencies
    testImplementation(libs.kotlin.test.junit)
    testImplementation(libs.ktor.server.testHost)
    testImplementation(libs.ktor.client.contentNegotiation)
    testImplementation(libs.jena.fuseki.main)
    testImplementation(libs.kotlinx.coroutines.test)
}

// A 2016 shim for JDK 8, dragged in by ktor-server-netty. ALPN has been part of the
// JDK since 9, so this is unmaintained dead weight on the runtime classpath.
configurations.all {
    exclude(group = "org.eclipse.jetty.alpn", module = "alpn-api")
}

// Pin the whole transitive graph. `./gradlew resolveAndLockAll --write-locks` after any
// dependency change; the lockfile diff is then part of the review instead of a surprise.
dependencyLocking {
    lockAllConfigurations()
}

// Locking only reports on configurations something actually resolves, so a plain
// --write-locks run would silently leave most of the graph unlocked. This resolves
// every lockable configuration in one pass.
tasks.register("resolveAndLockAll") {
    doFirst {
        require(gradle.startParameter.isWriteDependencyLocks) {
            "Run with --write-locks: ./gradlew resolveAndLockAll --write-locks"
        }
    }
    doLast {
        configurations.filter { it.isCanBeResolved }.forEach { it.resolve() }
    }
}

tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

// Without a stability filter a run recommends netty 5.0.0.Alpha2, kotlin 2.4.20-Beta2
// and friends, which makes the report useless.
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    return !stableKeyword && !regex.matches(version)
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf { isNonStable(candidate.version) && !isNonStable(currentVersion) }
    checkForGradleUpdate = true
    outputFormatter = "json,plain"
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
