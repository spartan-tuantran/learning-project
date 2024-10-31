package com.alext.utility

import com.google.i18n.phonenumbers.NumberParseException
import com.google.i18n.phonenumbers.PhoneNumberUtil

interface PhoneNumberFormatter {
  /**
   * Parses a string and returns it as a phone number in proto buffer format. The method is quite
   * lenient and looks for a number in the input text (raw input) and does not check whether the
   * string is definitely only a phone number. To do this, it ignores punctuation and white-space,
   * as well as any text before the number (e.g. a leading "Tel: ") and trims the non-number bits.
   * It will accept a number in any format (E164, national, international etc), assuming it can be
   * interpreted with the defaultRegion supplied. It also attempts to convert any alpha characters
   * into digits if it thinks this is a vanity number of the type "1800 MICROSOFT".
   *
   * @param phoneNumber number that we are attempting to parse. This can contain formatting such
   *     as +, ( and -, as well as a phone number extension. It can also be provided in RFC3966
   *     format. Default is "US"
   * @param countryCode region that we are expecting the number to be from. This is only used if
   *     the number being parsed is not written in international format. The country_code for the
   *     number in this case would be stored as that of the default region supplied. If the number
   *     is guaranteed to start with a '+' followed by the country calling code, then RegionCode.ZZ
   *     or null can be supplied.
   * @return null if the string is not considered to be a viable phone number (e.g.
   *     too few or too many digits) or if no default region was supplied and the number is not in
   *     international format (does not start with +)
   */
  fun format(phoneNumber: String, countryCode: String? = "US"): String?

  /**
   * Check if a phone number is valid
   *
   * @param phoneNumber number that we are attempting to parse. This can contain formatting such
   *     as +, ( and -, as well as a phone number extension. It can also be provided in RFC3966
   *     format. Default is "US"
   * @param countryCode region that we are expecting the number to be from. This is only used if
   *     the number being parsed is not written in international format. The country_code for the
   *     number in this case would be stored as that of the default region supplied. If the number
   *     is guaranteed to start with a '+' followed by the country calling code, then RegionCode.ZZ
   *     or null can be supplied.
   */
  fun check(phoneNumber: String, countryCode: String? = "US"): Boolean

  companion object {
    private const val US_COUNTRY_CODE = "US"

    /**
     * An implementation which uses E164 format.
     */
    val E164: PhoneNumberFormatter = object : PhoneNumberFormatter {

      override fun check(phoneNumber: String, countryCode: String?): Boolean {
        val util = PhoneNumberUtil.getInstance()
        return try {
          util.parse(phoneNumber, countryCode ?: US_COUNTRY_CODE)
          true
        } catch (e: NumberParseException) {
          false
        }
      }

      override fun format(phoneNumber: String, countryCode: String?): String? {
        val util = PhoneNumberUtil.getInstance()
        return try {
          util.format(
            util.parse(phoneNumber, countryCode ?: US_COUNTRY_CODE),
            PhoneNumberUtil.PhoneNumberFormat.E164
          )
        } catch (e: Exception) {
          null
        }
      }
    }
  }
}

fun String.asE164PhoneNumber(countryCode: String? = "US"): String? {
  return PhoneNumberFormatter.E164.format(this, countryCode)
}
