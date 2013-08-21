package com.github.dnadolny.javatoscala.conversion

sealed trait ConversionResult {
  def orElse(alternative: => ConversionResult): ConversionResult
  def toOption: Option[String]
}

case class ConversionSuccess(scalaSource: String) extends ConversionResult with MultiConversionResult {
  def orElse(alternative: => ConversionResult) = this
  def toOption = Some(scalaSource)
}

case class ConversionFailure(javaSource: String, cause: Throwable) extends ConversionResult {
  def orElse(alternative: => ConversionResult) = alternative
  def toOption = None
}

/*
 * This is a bit of a weird way of doing it, there's ConversionResult and MultiConversionResult,
 * and ConversionSuccess is a subclass of both.
 * I'm not sure how I feel about doing it like this yet.
 */
sealed trait MultiConversionResult {
  def toOption: Option[String]
}

case class MultiConversionFailure(convertFullClass: ConversionFailure, convertContentsOfClass: ConversionFailure, convertContentsOfMethod: ConversionFailure) extends MultiConversionResult {
  def toOption = None
}