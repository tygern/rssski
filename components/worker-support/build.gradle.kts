import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val kotlinCoroutinesVersion: String by extra

dependencies {
    implementation(project(":components:functional-support"))

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinCoroutinesVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
}
