package com.alext.plugins.flyway.tasks.verify

import com.alext.plugins.flyway.FlywayPlugin
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

@CacheableTask
open class VerifySqlOrderTask : DefaultTask() {

  @get:Input
  lateinit var workingDir: String

  @TaskAction
  fun execute() {
    val fileTree = project.fileTree(workingDir)
    SqlOrderTest(fileTree)
  }

  companion object {
    private const val TASK_NAME = "verifySqlOrder"
    private const val TASK_DESCRIPTION = "Validate the order of sql migration files."

    fun configure(project: Project, workingDir: String) : VerifySqlOrderTask {
      val task = project.tasks.findByName(TASK_NAME)
      return if (task != null) {
        task as VerifySqlOrderTask
      } else {
        project.tasks.create(TASK_NAME, VerifySqlOrderTask::class.java).apply {
          this.workingDir = workingDir
          this.group = FlywayPlugin.PLUGIN_NAME
          this.description = TASK_DESCRIPTION
        }
      }
    }
  }
}
