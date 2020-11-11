val ktorVersion: String by extra
val jedisVersion: String by extra
val okHttpVersion: String by extra

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("com.squareup.okhttp3:okhttp:$okHttpVersion")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.0-M1")
    testImplementation("com.squareup.okhttp3:mockwebserver:$okHttpVersion")
}
