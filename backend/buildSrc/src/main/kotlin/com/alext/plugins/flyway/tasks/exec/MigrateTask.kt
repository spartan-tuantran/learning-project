package com.alext.plugins.flyway.tasks.exec

import com.alext.plugins.flyway.FlywayPlugin
import com.alext.plugins.flyway.models.FlywayConfig
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.kotlin.dsl.create

open class MigrateTask : FlywayExecTask(listOf("migrate")) {

  companion object {
    private const val TASK_NAME = "migrate"

    fun configure(
      project: Project,
      config: FlywayConfig,
      vararg dependsOn: Task
    ) : MigrateTask {
      return project.tasks.create(TASK_NAME, MigrateTask::class).apply {
        this.description = "Run flyway migrate on ${config.dbName}"
        this.group = FlywayPlugin.PLUGIN_NAME
        this.config = config
        this.dependsOn(dependsOn)
      }
    }
  }
}
