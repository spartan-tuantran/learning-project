package com.alext.rtree.renderer

import java.awt.AlphaComposite
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

/**
 * An abstract renderer that know how create a new image and save
 * it to file for visualizing.
 */
abstract class Renderer(
  protected val width: Int,
  protected val height: Int
) {

  init {
    require(width > 0 && height > 0) {
      "Both 'weight' and 'height' must be > 0."
    }
  }

  private fun newImage(): BufferedImage {
    val image = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = image.graphics as Graphics2D
    with(graphics) {
      background = Color.white
      clearRect(0, 0, width, height)
      composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f)
      draw(graphics)
    }
    return image
  }

  /**
   * Implementation will need to override this to draw on image
   *
   * @param graphics The graphics object from [BufferedImage]
   */
  abstract fun draw(graphics: Graphics2D)

  /**
   * Save rendered image to a file for a given format
   *
   * @param file The file to be saved to
   * @param imageFormat The format of the image to be saved
   */
  fun saveTo(file: File, imageFormat: String): Boolean {
    return save(file, imageFormat)
  }

  /**
   * Save rendered image to a file path as PNG
   *
   * @param filePath The path to file to be saved.
   *                 Will be replaced if file exists.
   */
  fun saveTo(filePath: String): Boolean {
    return saveTo(File(filePath), "PNG")
  }

  private fun save(file: File, imageFormat: String): Boolean {
    return ImageIO.write(newImage(), imageFormat, file)
  }
}
