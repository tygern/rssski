val ktorVersion: String by extra

dependencies {
    implementation(project(":components:instagram"))
    implementation(project(":components:rss"))

    implementation(project(":components:functional-support"))

    implementation("io.ktor:ktor-locations:$ktorVersion")
}
