plugins {
  id("com.alext.plugins.flyway")
  alias(libs.plugins.ksp)
}

dependencies {
  implementation(project(":core:module-database"))
  implementation(project(":core:module-jackson"))
  implementation(project(":core:module-utility"))
  implementation(project(":core:module-codegen"))
  implementation(project(":core:module-javatime"))
  ksp(project(":core:module-codegen"))

  implementation(libs.exposed.core)
  implementation(libs.exposed.dao)
  implementation(libs.exposed.jdbc)
  implementation(libs.exposed.java.time)

  implementation(mn.micronaut.inject.java)
  implementation(libs.utility.libphonenumber)
  implementation(libs.geospatial.jts)

  implementation(libs.database.jdbi.core)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.core)
  implementation(libs.database.postgresql)

  implementation(libs.apache.common.lang3)

  testImplementation(project(":core:module-io"))
  testImplementation(project(":core:module-crypto"))
  testImplementation(project(":core:module-rtree"))
  testImplementation(project(":core:module-testing-utility"))
}

kotlin {
  sourceSets.main {
    kotlin.srcDir("build/generated/ksp/main/kotlin")
  }
  sourceSets.test {
    kotlin.srcDir("build/generated/ksp/test/kotlin")
  }
}
