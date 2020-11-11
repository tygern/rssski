import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jedisVersion: String by extra
val ktorVersion: String by extra
val logbackVersion: String by extra
val okHttpVersion: String by extra

dependencies {
    implementation(project(":components:instagram"))
    implementation(project(":components:worker-support"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.1")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")

    testImplementation("io.ktor:ktor-server-jetty:$ktorVersion")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}

tasks {
    jar {
        manifest {
            attributes("Main-Class" to "ski.rss.instagramworker.AppKt")
        }

        from({
            configurations.compileClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })
    }
}
