import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val ktorVersion: String by extra

dependencies {
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("ch.qos.logback:logback-classic:1.2.3")

    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "ski.rss.AppKt")
        }

        duplicatesStrategy = INCLUDE

        from({
            configurations.compileClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })
    }
}
