dependencies {
  implementation(libs.kotlin.reflect)
  implementation(libs.geospatial.jts)
  implementation(libs.geospatial.spatial4j)
  implementation(libs.geospatial.jts)
  implementation(libs.database.postgis.jdbc)
  implementation(libs.jackson.databind)
  implementation(libs.utility.caffeine)

  testImplementation(project(":core:module-io"))
  testImplementation(project(":core:module-jackson"))
}
