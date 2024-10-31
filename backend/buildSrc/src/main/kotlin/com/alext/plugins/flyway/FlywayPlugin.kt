package com.alext.plugins.flyway

import com.alext.plugins.flyway.tasks.verify.VerifySqlOrderTask
import com.alext.plugins.flyway.tasks.exec.MigrateTask
import com.alext.plugins.flyway.tasks.exec.CleanMigrateTask
import com.alext.plugins.flyway.models.FlywayConfig
import java.io.File
import java.util.Properties
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.withType

class FlywayPlugin : Plugin<Project> {

  companion object {
    const val WORKING_DIR = "flyway"
    const val PLUGIN_NAME = "flyway"
  }

  override fun apply(target: Project) {
    target.afterEvaluate {
      val configFile = File(target.projectDir, "${WORKING_DIR}/flyway.conf")
      val properties = Properties()
      try {
        properties.load(configFile.inputStream())
      } catch (e: Exception) {
        throw GradleException("Please make sure that the flyway.conf file exists under: ${configFile.absolutePath} and loadable")
      }
      createTasks(target, properties)
    }
  }

  @Suppress("UNUSED_VARIABLE")
  private fun createTasks(
    project: Project,
    properties: Properties
  ) {
    val config = FlywayConfig.from(properties)
    val verifyOrderTask = VerifySqlOrderTask.configure(
      project = project,
      workingDir = WORKING_DIR
    )

    val migrate = MigrateTask.configure(
      project = project,
      config = config,
      verifyOrderTask
    )

    val cleanMigrate = CleanMigrateTask.configure(
      project = project,
      config = config,
      verifyOrderTask
    )

    project.tasks.withType(Test::class).configureEach {
      this.dependsOn(cleanMigrate)
    }
  }
}

