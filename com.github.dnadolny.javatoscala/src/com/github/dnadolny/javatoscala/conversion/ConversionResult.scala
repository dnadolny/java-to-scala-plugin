package com.github.dnadolny.javatoscala.conversion

sealed trait ConversionResult {
  def orElse(alternative: => ConversionResult): ConversionResult
  def toOption: Option[String]
}

case class ConversionSuccess(scalaSource: String) extends ConversionResult {
  def orElse(alternative: => ConversionResult) = this
  def toOption = Some(scalaSource)
}

case class ConversionFailure(javaSource: String, cause: Throwable) extends ConversionResult {
  def orElse(alternative: => ConversionResult) = alternative
  def toOption = None
}