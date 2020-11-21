import org.gradle.api.file.DuplicatesStrategy.INCLUDE

val ktorVersion: String by extra
val jedisVersion: String by extra
val logbackVersion: String by extra

dependencies {
    implementation(project(":components:instagram"))
    implementation(project(":components:rss"))
    implementation(project(":components:twitter"))

    implementation(project(":components:functional-support"))
    implementation(project(":components:redis-support"))

    implementation("io.ktor:ktor-server-jetty:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-serialization:$ktorVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("redis.clients:jedis:$jedisVersion")

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
                .map {
                    zipTree(it)
                }
        })
    }
}
