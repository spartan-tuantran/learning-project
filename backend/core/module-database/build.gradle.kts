dependencies {
  implementation(project(":core:module-jackson"))
  implementation(project(":core:module-logging"))

  implementation(libs.jackson.annotations)
  implementation(libs.jackson.core)
  implementation(libs.jackson.module.kotlin)
  implementation(libs.database.postgis.jdbc)
  implementation(libs.database.postgresql)
  implementation(libs.database.hikaricp)
  implementation(libs.geospatial.jts)
  implementation(libs.geospatial.spatial4j)

  implementation(libs.exposed.core)
  implementation(libs.exposed.dao)
  implementation(libs.exposed.jdbc)
  implementation(libs.exposed.java.time)
}
