package com.alext.database.exposed.extension

import java.math.BigDecimal
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object MoneyFormat {
  const val CRYPTO_PRECISION = 100
  const val CRYPTO_SCALE = 20
  const val USD_PRECISION = 20
  const val USD_SCALE = 8
}

fun Table.crypto(name: String): Column<BigDecimal> {
  return decimal(name, MoneyFormat.CRYPTO_PRECISION, MoneyFormat.CRYPTO_SCALE)
}

fun Table.usd(name: String): Column<BigDecimal> {
  return decimal(name, MoneyFormat.USD_PRECISION, MoneyFormat.USD_SCALE)
}
