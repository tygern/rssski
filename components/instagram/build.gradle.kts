val ktorVersion: String by extra

dependencies {
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.0")

    testImplementation("io.ktor:ktor-client-mock:${ktorVersion}")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.0-M1")
}
