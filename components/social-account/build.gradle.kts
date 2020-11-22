val jedisVersion: String by extra

dependencies {
    implementation("redis.clients:jedis:$jedisVersion")
}
