val jedisVersion: String by extra
val ktorVersion: String by extra
val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":components:functional-support"))

    implementation("redis.clients:jedis:$jedisVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")

    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinCoroutinesVersion")
}
