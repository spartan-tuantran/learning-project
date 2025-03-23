package com.alext.api

import io.micronaut.runtime.Micronaut

class Main {

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      Micronautr
        .build(*args)
        .mainClass(Main::class.java)
        .eagerInitSingletons(true)
        .deduceEnvironment(true)ss
        .start()
    }
  }
}
