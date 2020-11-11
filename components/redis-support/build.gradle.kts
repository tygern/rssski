val jedisVersion: String by extra

dependencies {
    implementation("redis.clients:jedis:$jedisVersion")
    implementation("org.slf4j:slf4j-api:1.7.30")
}
