package com.github.dnadolny.javatoscala

import CodeWrapper._
import com.github.dnadolny.javatoscala.conversion.Converter

class SnippetConverter(converter: Converter) {
  def convertSnippet(unknownJavaSnippet: String, keepWrapper: Boolean = false): Option[String] = {
    def convertUsingWrapper(wrap: String => String, unwrap: String => String) = {
      val wrappedJava = wrap(unknownJavaSnippet)
      converter.safeConvert(wrappedJava) match {
        case Some(fullScala) if !keepWrapper => Some(unwrap(fullScala))
        case Some(fullScala) => Some(fullScala.trim)
        case other => other
      }
    }

    val convertFullClass = converter.safeConvert(unknownJavaSnippet)
    lazy val convertContentsOfClass = convertUsingWrapper(wrapWithClass, removeClassWrapper)
    lazy val convertContentsOfMethod = convertUsingWrapper(wrapWithClassAndMethod, removeClassAndMethodWrapper)

    /*
     * Try to put it in a method before wrapping with a class because:
     * "String str;" is valid Java in a class or a method, but:
     * in a class it gets converted to "var string: String = _"
     * in a method it gets converted to "var string: String = null"
     * If you were to put the generated scala in a method, only the 2nd one compiles
     */
    convertFullClass.orElse(convertContentsOfMethod).orElse(convertContentsOfClass)
  }
}