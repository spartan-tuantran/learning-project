package com.alext.api

import io.micronaut.runtime.Micronaut

class Main {

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      MicronautS
        .build(*args)
        .mainClass(Main::class.java)
        .eagerInitSingletons(true)
        .deduceEnvironment(true)
        .start()
    }
  }
}
