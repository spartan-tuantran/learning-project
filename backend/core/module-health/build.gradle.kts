dependencies {
  implementation(project(":core:module-logging"))
  implementation(project(":core:module-database"))
  implementation(project(":core:module-redis"))

  // Micronaut
  implementation(mn.micronaut.management)
  implementation(mn.micronaut.aop)
  implementation(mn.micronaut.inject.java)
  implementation(libs.redis.redisson)
  implementation(libs.exposed.core)
}
