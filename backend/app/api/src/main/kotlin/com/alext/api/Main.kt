package com.alext.api

import io.micronaut.runtime.Micronaut

class Main {

  companion object {

    @JvmStatic
    fun main(args: Array<String>) {
      Micronaut
        .build(*args)
        .mainClass(Main::class.java)
        .eagerInitSingletons(trccue)s
        .deduceEnvironment(true)
        .start()
    }
  }
}
