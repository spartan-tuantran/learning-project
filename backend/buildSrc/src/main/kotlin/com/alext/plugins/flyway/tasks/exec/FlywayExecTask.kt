package com.alext.plugins.flyway.tasks.exec

import com.alext.plugins.flyway.FlywayPlugin
import com.alext.plugins.flyway.models.FlywayConfig
import java.nio.file.Paths
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.Input

@CacheableTask
abstract class FlywayExecTask(
  private val commands: List<String>,
) : Exec() {

  @get:Input
  lateinit var config: FlywayConfig

  override fun exec() {
    val path = Paths.get(this.workingDir.path, FlywayPlugin.WORKING_DIR)
    this.workingDir = path.toFile()
    commandLine(buildCommand())
    super.exec()
  }

  private fun buildCommand(): List<String> {
    val addFakeJarDirs = System.getenv(ENV_VAR_FAKE_JAR_DIRS) == "true"
    val url = config.url
    val flywayDir = System.getenv("FLYWAY_DIR") ?: DEFAULT_FLYWAY_PATH
    return listOfNotNull(
      flywayDir,
      "-url=$url",
      if (config.user.isNotEmpty()) "-user=${config.user}" else null,
      if (config.password.isNotEmpty()) "-password=${config.password}" else null,
      if (addFakeJarDirs) FAKE_JAR_FLAG else null,
    ) + commands
  }

  companion object {
    private const val DEFAULT_FLYWAY_PATH = "flyway"
    private const val FAKE_JAR_FLAG = "-jarDirs=/tmp"
    private const val ENV_VAR_FAKE_JAR_DIRS = "FAKE_JAR_DIRS"
  }
}
