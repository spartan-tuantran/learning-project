import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
  repositories {
    mavenCentral()
  }
}

plugins {
  alias(libs.plugins.kotlin.jvm)
  alias(libs.plugins.ksp) apply false
}

repositories {
  mavenCentral()
}

subprojects {
  apply(plugin = "kotlin")
  apply(plugin = "java")

  tasks.getByName<Jar>("jar") {
    val parentName = this.project.parent?.name
    val thisName = this.project.name
    archiveFileName.set("${parentName}-${thisName}.jar")
  }

  java {
    toolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  kotlin {
    jvmToolchain {
      languageVersion.set(JavaLanguageVersion.of(17))
    }
  }

  repositories {
    mavenCentral()
    maven {
      url = uri("https://maven.google.com/")
    }
  }

  tasks.withType(KotlinCompile::class).configureEach {
    compilerOptions {
      jvmTarget.set(JvmTarget.JVM_17)
      allWarningsAsErrors = false
    }
  }

  tasks.withType<Test> {
    useJUnitPlatform()
  }

  dependencies {
    implementation(rootProject.libs.testing.mockk)
    implementation(rootProject.libs.testing.assertj)
    implementation(rootProject.libs.testing.jupiter)
    implementation(rootProject.libs.testing.strikt)
  }
}
