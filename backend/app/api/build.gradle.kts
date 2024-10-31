import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

group = "com.alext"
version = "1.0-SNAPSHOT"

plugins {
  id("application")
  alias(libs.plugins.ksp)
  alias(libs.plugins.shadow)
  alias(libs.plugins.allopen)
}

application {
  mainClass.set("com.alext.api.Main")
}

allOpen {
  annotation("io.micronaut.aop.Around")
  annotation("io.micronaut.validation.Validated")
}

tasks {
  named<ShadowJar>("shadowJar") {
    archiveClassifier.set("all")
    mergeServiceFiles()
    manifest {
      attributes(mapOf("Main-Class" to "com.alext.api.Main"))
    }
    isZip64 = true
  }
}

dependencies {
  implementation(project(":app:module-postgresql"))
  implementation(project(":app:module-client"))
  implementation(project(":app:module-exception"))
  implementation(project(":core:module-jackson"))
  implementation(project(":core:module-logging"))
  implementation(project(":core:module-database"))
  implementation(project(":core:module-crypto"))
  implementation(project(":core:module-retrofit"))

  // Kotlin
  implementation(libs.kotlin.stdlib)
  implementation(libs.kotlin.reflect)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.kotlinx.coroutines.jdk8)
  implementation(libs.kotlinx.coroutines.jdk8)

  // Micronaut
  implementation(mn.micronaut.kotlin.runtime)
  ksp(mn.micronaut.inject.kotlin)
  implementation(mn.micronaut.http.server.netty)
  implementation(mn.micronaut.security.jwt)
  implementation(mn.micronaut.validation)
  ksp(mn.micronaut.validation.processor)
  ksp(mn.micronaut.http.validation)
  implementation(mn.micronaut.security.annotations)
  implementation(mn.micronaut.jackson.databind)
  ksp(mn.micronaut.openapi)
  compileOnly(mn.micronaut.openapi.annotations)
  implementation(mn.micronaut.reactor)
  runtimeOnly(mn.snakeyaml)

  implementation(libs.logging.logbackLogstash)
  implementation(libs.compiler.janino)
}
