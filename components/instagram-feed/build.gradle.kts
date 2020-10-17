val ktorVersion: String by extra

dependencies {
    implementation(project(":components:instagram"))
    implementation(project(":components:rss"))

    implementation("io.ktor:ktor-locations:$ktorVersion")
}
