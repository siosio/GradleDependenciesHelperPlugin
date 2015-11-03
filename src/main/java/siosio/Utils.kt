package siosio

import kotlin.text.Regex

object Utils {

  private val splitPattern = Regex(":")

  internal fun split(dependency: String) = splitPattern.split(dependency)

  internal fun trimQuote(text: String) = text.trim('"', '\'').trimEnd('"', '\'')
}
