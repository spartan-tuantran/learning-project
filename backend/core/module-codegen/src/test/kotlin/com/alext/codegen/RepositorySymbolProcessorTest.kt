@file:Suppress("SameParameterValue")

package com.alext.codegen

import com.alext.codegen.processor.RepositoryProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import java.io.BufferedReader
import java.io.File
import java.nio.file.Path
import org.assertj.core.api.Assertions.assertThat
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class RepositorySymbolProcessorTest {

  @TempDir
  lateinit var tempDir: Path

  @Test
  fun `entity is not a data class`() {
    val src = src(
      """
        package com.alext

        import com.alext.codegen.annotations.Repository
        import java.util.UUID

          @Repository
          class Hello(
            val id: UUID = UUID.randomUUID(),
            val two: Int = 123
          )
      """
    )
    val result = compile(src)
    assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
  }

  @Test
  fun `entity has type parameter`() {
    val src = src(
      """
        package com.alext

        import java.util.UUID
        import com.alext.codegen.annotations.Repository

          @Repository
          data class Hello<T>(
            val id: UUID = UUID.randomUUID(),
            val two: Int = 123
          )
      """
    )
    val result = compile(src)
    assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages).contains("@Repository must target data classes with no type parameters")
  }

  @Test
  fun `entity does not have id`() {
    val src = src(
      """
        package com.alext

        import com.alext.codegen.annotations.Repository

        @Repository
        data class Hello(
          val one: Int = 123
        )
      """
    )
    val result = compile(src)
    assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.COMPILATION_ERROR)
    assertThat(result.messages).contains("@Repository must contain `id` property as UUID")
  }

  @Test
  fun `generate repository extension from entity - happy path`() {
    val src = srcFromResource("UserEntity.kt")
    val result = compile(src)
    assertThat(result.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)

    val rawGeneratedSource = result.sourceFor("UserEntityRepositoryExtension.kt")
    println(rawGeneratedSource)

    rawGeneratedSource.apply {
      assertThat(this.contains("byId")).isTrue()
      assertThat(this.contains("byAge")).isTrue()
      assertThat(this.contains("byName")).isTrue()
      assertThat(this.contains("byIds")).isTrue()
      assertThat(this.contains("upsert")).isTrue()
      assertThat(this.contains("insert")).isTrue()
      assertThat(this.contains("convert")).isTrue()
      assertThat(this.contains("ResultSet")).isTrue()
      assertThat(this.contains("batchInsert")).isTrue()
      assertThat(this.contains("deleteById")).isTrue()
    }

    val compiledResult = compile(src(rawGeneratedSource))
    assertThat(compiledResult.exitCode).isEqualTo(KotlinCompilation.ExitCode.OK)
  }

  private fun compile(
    vararg source: SourceFile
  ): KotlinCompilation.Result {
    return KotlinCompilation()
      .apply {
        sources = source.toList()
        symbolProcessorProviders = listOf(RepositoryProcessorProvider())
        workingDir = tempDir.resolve("root").toFile()
        inheritClassPath = true
        verbose = false
      }
      .compile()
  }

  private fun KotlinCompilation.Result.sourceFor(fileName: String): String {
    val sources = kspGeneratedSources()
    return sources.find { it.name == fileName }
      ?.readText()
      ?: throw IllegalArgumentException("Could not find file $fileName in $sources")
  }

  private fun KotlinCompilation.Result.kspGeneratedSources(): List<File> {
    val kspDir = workingDir.resolve("ksp")
    val sourcesDir = kspDir.resolve("sources")
    val kotlinDir = sourcesDir.resolve("kotlin")
    val javaDir = sourcesDir.resolve("java")
    return kotlinDir.walk().toList() + javaDir.walk().toList()
  }

  private val KotlinCompilation.Result.workingDir: File
    get() = checkNotNull(outputDirectory.parentFile)

  private fun src(@Language("kotlin") contents: String): SourceFile {
    return SourceFile.kotlin("temp.kt", contents)
  }

  private fun srcFromResource(path: String): SourceFile {
    return SourceFile.kotlin("temp.kt", resourceText(path))
  }

  private fun resourceText(path: String): String =
    resourceBufferedReader(path).use { it.readText() }

  private fun resourceBufferedReader(path: String): BufferedReader =
    javaClass.classLoader.getResourceAsStream(path)!!.bufferedReader()
}
