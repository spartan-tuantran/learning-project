dependencies {
  implementation(project(":core:module-javatime"))
  implementation(libs.jackson.module.kotlin)
  implementation(libs.jackson.databind)
  implementation(libs.jackson.datatype.jsr310)
  implementation(libs.jackson.core)
  implementation(libs.geospatial.jts)
}
