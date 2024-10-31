package com.alext.rtree.renderer

import java.awt.Graphics2D
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.catchThrowable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class RendererTest {

  @ParameterizedTest(name = "(width = {0}, height={1})")
  @CsvSource(
    "0   , 100",
    "100 , 0",
    "-1  , 100",
    "100 , 1",
    "0   , 0",
    "-1  , -1"
  )
  fun `weight and height must positive`() {
    assertThat(
      catchThrowable {
        object : Renderer(width = 0, height = 100) {
          override fun draw(graphics: Graphics2D) {
          }
        }
      }
    ).hasMessage("Both 'weight' and 'height' must be > 0.")
  }
}
