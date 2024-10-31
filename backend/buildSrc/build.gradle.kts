plugins {
  `kotlin-dsl`
  `java-gradle-plugin`
}

gradlePlugin {
  plugins {
    create("flyway") {
      id = "com.alext.plugins.flyway"
      implementationClass = "com.alext.plugins.flyway.FlywayPlugin"
    }
  }
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

