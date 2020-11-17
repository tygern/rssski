import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val jedisVersion: String by extra
val ktorVersion: String by extra
val logbackVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":components:instagram"))
    implementation(project(":components:twitter"))

    implementation(project(":components:functional-support"))
    implementation(project(":components:redis-support"))
    implementation(project(":components:worker-support"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")

    testImplementation("io.ktor:ktor-server-jetty:$ktorVersion")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}

tasks {
    jar {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        manifest {
            attributes("Main-Class" to "ski.rss.socialworker.AppKt")
        }

        from({
            configurations.compileClasspath.get()
                .filter { it.name.endsWith("jar") }
                .map { zipTree(it) }
        })
    }
}
