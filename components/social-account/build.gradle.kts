val jedisVersion: String by extra

dependencies {
    implementation(project(":components:redis-support"))
    implementation(project(":components:functional-support"))

    implementation("redis.clients:jedis:$jedisVersion")
}
