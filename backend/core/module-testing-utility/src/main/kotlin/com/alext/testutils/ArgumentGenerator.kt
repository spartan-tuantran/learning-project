package com.alext.testutils

import java.io.File
import kotlin.random.Random
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.apache.commons.lang3.RandomStringUtils
import org.apache.commons.lang3.RandomUtils

object ArgumentGenerator {

  private val uppercaseLetters = ('A'..'Z')
  private val lowercaseLetters = ('a'..'z')
  private val digits = ('0'..'9')
  private val specialChars = listOf('!', '@', '#', '$', '%', '^', '&', '*')
  private val allowedChars = uppercaseLetters + lowercaseLetters + digits + specialChars
  private val imageExtensions = listOf("jpg", "jpeg", "png", "gif")
  private val videoExtensions = listOf("mp4", "avi", "mkv", "mov", "wmv")
  private val originations = listOf("vn", "com", "net", "org", "dev")

  fun randomEmail(prefix: String? = null): String {
    return "${prefix ?: randomText(5)}${randomText(5)}@gmail.com"
  }

  fun randomPassword(): String {
    var password: String
    while (true) {
      password = (1..8).map { allowedChars.random() }.joinToString("")
      if (password.any { it in uppercaseLetters } &&
        password.any { it in lowercaseLetters } &&
        password.any { it in digits } &&
        password.any { it in specialChars }
      ) {
        break
      }
    }
    return password
  }

  fun randomText(length: Int = 10): String {
    return RandomStringUtils.randomAlphanumeric(length)
  }

  fun randomNumber(min: Int = 0, max: Int = Int.MAX_VALUE): Int {
    return RandomUtils.nextInt(min, max)
  }

  fun randomURL(extension: String? = null): String {
    val extensions = listOf(*imageExtensions.toTypedArray(), *videoExtensions.toTypedArray())
    return "https://${randomText()}.${originations.random()}/${randomText()}.${extension ?: extensions.random()}"
  }

  fun randomSerialNumber(prefix: String = "STA", length: Int = 32): String {
    val randomNumber = randomNumber().toString()
    val timestamp = System.currentTimeMillis().toString()
    val desiredLength = length - prefix.length - randomNumber.length - timestamp.length
    if (desiredLength < 0) {
      return "$prefix$randomNumber$timestamp".substring(0, length - 1)
    }
    val zeros = "0".repeat(desiredLength)

    return "$prefix$zeros$randomNumber$timestamp"
  }

  fun randomUsername(): String {
    return randomText().trim().lowercase()
  }

  fun randomName(prefix: String? = null): String {
    return "${prefix ?: randomText(5)}${ randomText(5) }"
  }

  fun randomPhoneNumber(): String {
    val sb = StringBuilder()

    // Generate area code (3 digits)
    sb.append(Random.nextInt(8) + 2) // First digit should be between 2 and 9
    sb.append(Random.nextInt(10))
    sb.append(Random.nextInt(10))
    sb.append("-")

    // Generate exchange code (3 digits)
    sb.append(Random.nextInt(8) + 2) // First digit should be between 2 and 9
    sb.append(Random.nextInt(10))
    sb.append(Random.nextInt(10))
    sb.append("-")

    // Generate line code (4 digits)
    sb.append(Random.nextInt(10))
    sb.append(Random.nextInt(10))
    sb.append(Random.nextInt(10))
    sb.append(Random.nextInt(10))

    return sb.toString()
  }

  fun randomUKNumber(): String {
    // List of London area codes
    val areaCodes = listOf("0207", "0208", "0203", "0204")

    val areaCode = areaCodes.random()

    val localNumber = (1 until 8).joinToString("") { Random.nextInt(0, 10).toString() }

    val countryCode = "+44"

    return "$countryCode $areaCode $localNumber"
  }

  fun formData(name: String, value: String?): MultipartBody.Part? {
    value?.let {
      return MultipartBody.Part.createFormData(name, it)
    }
    return null
  }

  fun filePart(
    fileName: String? = null,
    extension: String
  ): MultipartBody.Part {
    // temporary file
    val tmpFile = tempFile("${fileName ?: randomText()}.$extension", ByteArray(100))

    // create request body
    val requestBody = tmpFile.asRequestBody(
      contentType = "multipart/form-data".toMediaTypeOrNull()
    )

    return MultipartBody.Part.createFormData(
      name = "file",
      filename = tmpFile.name,
      body = requestBody
    )
  }

  private fun tempFile(fileName: String, bytes: ByteArray): File {
    val resourcePath = javaClass.classLoader.getResource("")?.path
    val tmpFile = File(resourcePath, fileName)
    tmpFile.createNewFile()
    tmpFile.writeBytes(bytes)
    return tmpFile
  }
}
