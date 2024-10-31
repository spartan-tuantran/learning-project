plugins {
  id("org.jetbrains.kotlin.plugin.serialization") version "1.9.23"
}

dependencies {
  implementation(project(":core:module-logging"))
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.serialization.json)
  implementation(libs.logging.slf4j)
  implementation(libs.arrow.core)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.networking.retrofit)
  implementation(libs.networking.retrofit.jackson)
  implementation(libs.networking.retrofit.scalars)
  implementation(libs.networking.okhttp.logging)

  testImplementation(libs.networking.okhttp.mockserver)
  testImplementation(libs.logging.logbackClassic)
}
