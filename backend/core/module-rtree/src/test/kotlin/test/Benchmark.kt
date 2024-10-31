package test

data class BenchmarkResult<T>(
  val ns: Long,
  val data: T
)

inline fun <T> measure(crossinline call: () -> T): BenchmarkResult<T> {
  val begin = System.nanoTime()
  val result = call()
  val end = System.nanoTime()
  return BenchmarkResult(end - begin, result)
}
