package test

data class MemoryUsage(
  val totalMb: Long = 0,
  val usedMb: Long = 0
) {

  companion object {

    private const val ONE_MB = 1024 * 1024

    /**
     * Collect runtime memory usage
     */
    fun runtime(): MemoryUsage {
      val runtime = Runtime.getRuntime()
      val free = runtime.freeMemory()
      val total = runtime.totalMemory()
      val used = total - free
      return MemoryUsage(
        totalMb = total / ONE_MB,
        usedMb = used / ONE_MB
      )
    }
  }

  override fun toString(): String {
    return "Memory usage: $usedMb  of $totalMb MB"
  }
}
