dependencies {
  implementation(project(":core:module-logging"))
  implementation(project(":core:module-jackson"))
  implementation(libs.redis.jedis)
  implementation(libs.redis.redisson)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.module.kotlin)

  testImplementation(project(":core:module-coroutine"))
  testImplementation(libs.apache.common.lang3)
  testImplementation(libs.logging.logbackClassic)
}
