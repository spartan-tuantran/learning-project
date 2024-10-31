plugins {
  alias(libs.plugins.ksp)
}

ksp {
  arg("autoserviceKsp.verify", "true")
  arg("autoserviceKsp.verbose", "true")
}

dependencies {
  implementation(project(":core:module-database"))
  implementation(project(":core:module-javatime"))

  implementation(libs.codegen.symbolprocessing)
  implementation(libs.codegen.kotlinpoet)
  implementation(libs.codegen.kotlinpoet.ksp)

  implementation(libs.database.postgis.jdbc)
  implementation(libs.database.postgresql)
  implementation(libs.database.hikaricp)

  implementation(libs.codegen.auto.service)
  implementation(libs.codegen.auto.service.annotation)

  implementation(libs.exposed.core)
  implementation(libs.exposed.jdbc)
  implementation(libs.exposed.dao)
  implementation(libs.exposed.java.time)

  ksp(libs.codegen.auto.service.ksp)

  testImplementation(libs.codegen.kotlin.compile.testing.ksp)
  testImplementation(libs.codegen.kotlin.compile.testing)
}
