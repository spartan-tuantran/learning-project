pluginManagement {
  repositories {
    mavenCentral()
    maven("https://plugins.gradle.org/m2/")
    maven("https://maven.google.com")
  }
}

rootProject.name = "backend"

dependencyResolutionManagement {
  @Suppress("UnstableApiUsage")
  repositories {
    mavenCentral()
  }
  versionCatalogs {
    create("mn") {
      from("io.micronaut.platform:micronaut-platform:4.6.3")
    }
  }
}

include(
  "app:api",
  "app:module-postgresql",
  "app:module-exception",
  "app:module-client",
)

include(
  "core:module-database",
  "core:module-jackson",
  "core:module-logging",
  "core:module-javatime",
  "core:module-codegen",
  "core:module-coroutine",
  "core:module-crypto",
  "core:module-csv",
  "core:module-health",
  "core:module-io",
  "core:module-redis",
  "core:module-retrofit",
  "core:module-rtree",
  "core:module-testing-utility",
  "core:module-utility"
)
