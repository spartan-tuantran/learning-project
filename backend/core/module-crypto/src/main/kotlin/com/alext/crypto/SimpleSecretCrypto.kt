package com.alext.crypto

import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import org.xerial.snappy.Snappy

class SimpleSecretCrypto(
  private val secret: String
) : Crypto {

  companion object {
    private const val ALGORITHM_PBKDF2 = "PBKDF2WithHmacSHA1"
    private const val ALGORITHM_AES = "AES"
    private const val AES_ALGORITHM = "AES/CBC/PKCS5PADDING"
    private const val ITERATION_COUNT = 10
    private const val KEY_LENGTH = 256
  }

  override fun encrypt(plainText: String): String {
    val saltSeed = secureRandom(256)
    val pbKeySpec = PBEKeySpec(secret.toCharArray(), saltSeed, ITERATION_COUNT, KEY_LENGTH)
    val secretKeyFactory: SecretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_PBKDF2)
    val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
    val keySpec = SecretKeySpec(keyBytes, ALGORITHM_AES)
    val ivSeed = secureRandom(16)
    val iv = IvParameterSpec(ivSeed)
    val cipher: Cipher = Cipher.getInstance(AES_ALGORITHM)
    cipher.init(Cipher.ENCRYPT_MODE, keySpec, iv)
    val encrypted = cipher.doFinal(plainText.toByteArray())
    return encode(saltSeed, ivSeed, encrypted)
  }

  override fun decrypt(cipherText: String): String? {
    return try {
      val (saltSeed, ivSeed, encrypted) = decode(cipherText)
      val pbKeySpec = PBEKeySpec(secret.toCharArray(), saltSeed, ITERATION_COUNT, KEY_LENGTH)
      val secretKeyFactory = SecretKeyFactory.getInstance(ALGORITHM_PBKDF2)
      val keyBytes = secretKeyFactory.generateSecret(pbKeySpec).encoded
      val keySpec = SecretKeySpec(keyBytes, ALGORITHM_AES)
      val cipher = Cipher.getInstance(AES_ALGORITHM)
      val ivSpec = IvParameterSpec(ivSeed)
      cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
      String(cipher.doFinal(encrypted))
    } catch (e: Exception) {
      null
    }
  }

  private fun secureRandom(length: Int): ByteArray {
    val random = SecureRandom()
    val result = ByteArray(length)
    random.nextBytes(result)
    return result
  }

  private fun encode(vararg parts: ByteArray): String {
    val encoder = Base64.getEncoder()
    val bytes = parts.joinToString(":") { encoder.encodeToString(it) }.toByteArray()
    val compressed = Snappy.compress(bytes)
    return encoder.encodeToString(compressed)
  }

  private fun decode(text: String): Triple<ByteArray, ByteArray, ByteArray> {
    val decoder = Base64.getDecoder()
    val uncompressed = Snappy.uncompress(decoder.decode(text))
    val bytes = String(uncompressed).split(":").map { decoder.decode(it) }
    return Triple(bytes[0], bytes[1], bytes[2])
  }
}
