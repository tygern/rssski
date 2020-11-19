val ktorVersion: String by extra

dependencies {
    implementation(project(":components:twitter"))
    implementation(project(":components:rss"))

    implementation(project(":components:functional-support"))

    implementation("io.ktor:ktor-locations:$ktorVersion")
}
