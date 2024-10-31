package com.alext.io

import java.io.File
import java.io.InputStream

object Resources {

  fun file(path: String, relative: Boolean = false): File? {
    return if (relative) {
      this::class.java.getResource(path)?.let { File(it.toURI()) }
    } else {
      javaClass.classLoader.getResource(path.removePrefix("/"))?.let { File(it.toURI()) }
    }
  }

  fun stream(path: String, relative: Boolean = false): InputStream? {
    return if (relative) {
      this::class.java.getResourceAsStream(path)
    } else {
      javaClass.classLoader.getResourceAsStream(path.removePrefix("/"))
    }
  }
}
