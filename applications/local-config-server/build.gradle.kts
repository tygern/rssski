import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val ktorVersion: String by extra
val logbackVersion: String by extra

dependencies {
    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "ski.rss.localconfigserver.AppKt")
        }

        duplicatesStrategy = INCLUDE

        from({
            configurations.compileClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map {
                    zipTree(it)
                }
        })
    }
}
