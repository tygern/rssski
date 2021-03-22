import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10" apply false
    kotlin("plugin.serialization") version "1.4.10" apply false
}

subprojects kotlinConfig@{
    if (isNotKotlinProject()) return@kotlinConfig

    extra.apply {
        set("ktorVersion", "1.4.2")

        set("kotlinCoroutinesVersion", "1.4.1")
        set("jedisVersion", "3.3.0")
        set("logbackVersion", "1.2.3")
    }

    group = "ski.rss"

    apply(plugin = "kotlin")

    repositories {
        jcenter()
    }

    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))

        "testImplementation"(kotlin("test-junit"))
        "testImplementation"("io.mockk:mockk:1.10.2")
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs += "-Xopt-in=kotlin.time.ExperimentalTime"
        }
    }
}

fun Project.isNotKotlinProject() = name == "applications" || name == "components"
