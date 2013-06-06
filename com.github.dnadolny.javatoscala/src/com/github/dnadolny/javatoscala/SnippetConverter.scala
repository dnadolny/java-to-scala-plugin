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
    lazy val convertContentsOfMethod = {
      /*
       * Hacky work-around for a problem with scalagen/javaparser.
       * It allows the Java code "class Snippet { private void snippet() { static int x; }" },
       * which is then translated to the Scala "class Snippet { def snippet() { var x: Int = null } }",
       * which means we lose the knowledge that it's static.
       * So, if the snippet might contain static fields/methods, don't even try wrapping it in a method
       */
      if (unknownJavaSnippet.contains("static")) None 
      else convertUsingWrapper(wrapWithClassAndMethod, removeClassAndMethodWrapper)
    }
    
    /*
     * Try to put it in a method before wrapping with a class because:
     * "String str;" is valid Java in a class or a method, but:
     * in a class it gets converted to "var string: String = _"
     * in a method it gets converted to "var string: String = null"
     * If you were to put the generated Scala in a method, only the 2nd one compiles
     */
    convertFullClass.orElse(convertContentsOfMethod).orElse(convertContentsOfClass)
  }
}