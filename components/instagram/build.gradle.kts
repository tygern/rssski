val ktorVersion: String by extra
val jedisVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":components:functional-support"))

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("io.ktor:ktor-client-core:${ktorVersion}")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
    testImplementation("io.ktor:ktor-client-mock:${ktorVersion}")
}
