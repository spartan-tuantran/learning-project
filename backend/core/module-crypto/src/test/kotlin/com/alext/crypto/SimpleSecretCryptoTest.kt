package com.alext.crypto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class SimpleSecretCryptoTest {

  private val crypto = SimpleSecretCrypto("123")

  @Test
  fun `encrypt and decrypt - happy path`() {
    val plainText = "chan@gmail.com"
    val cipherText = crypto.encrypt(plainText)
    println(cipherText.length)
    assertThat(crypto.decrypt(cipherText)).isEqualTo(plainText)
  }

  @Test
  fun `encrypt and decrypt - invalid`() {
    assertThat(crypto.decrypt("123")).isEqualTo(null)
    assertThat(crypto.decrypt("1:2:3")).isEqualTo(null)
  }
}
