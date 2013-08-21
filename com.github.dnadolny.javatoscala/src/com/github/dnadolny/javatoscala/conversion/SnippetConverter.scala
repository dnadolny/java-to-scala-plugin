package com.github.dnadolny.javatoscala.conversion

import com.github.dnadolny.javatoscala.text.CodeWrapper._

class SnippetConverter(converter: Converter) {
  def convertSnippet(unknownJavaSnippet: String, keepWrapper: Boolean = false): MultiConversionResult = {
    def convertUsingWrapper(wrap: String => String, unwrap: String => String) = {
      val wrappedJava = wrap(unknownJavaSnippet)
      converter.safeConvert(wrappedJava) match {
        case ConversionSuccess(fullScala) if !keepWrapper => ConversionSuccess(unwrap(fullScala))
        case ConversionSuccess(fullScala) => ConversionSuccess(fullScala.trim)
        case failure => failure
      }
    }

    val convertFullClass = converter.safeConvert(unknownJavaSnippet)
    lazy val convertContentsOfClass = convertUsingWrapper(wrapWithClass, removeClassWrapper)
    lazy val convertContentsOfMethod = {
      /*
       * Hacky work-around for a problem with scalagen/javaparser.
       * It allows the Java code "class Snippet { private void snippet() { static int x; }" },
       * (which is the snippet "static int x;" wrapped in a class & method) 
       * which is then translated to the Scala "class Snippet { def snippet() { var x: Int = null } }",
       * which means we lose the knowledge that it's static.
       * So, if the snippet might contain static fields/methods, don't even try wrapping it in a method.
       * This is not perfect because, for example, the code "new Object() { static int x; }" should be
       * wrapped in a method. However, I expect plain static fields/methods to be more common than wrapped static fields/methods.
       */
      if (unknownJavaSnippet.contains("static")) ConversionFailure(unknownJavaSnippet, new RuntimeException("Not wrapping in method")) 
      else convertUsingWrapper(wrapWithClassAndMethod, removeClassAndMethodWrapper)
    }
    
    /*
     * Try to put it in a method before wrapping with a class because:
     * "String str;" is valid Java in a class or a method, but:
     * in a class it gets converted to "var string: String = _"
     * in a method it gets converted to "var string: String = null"
     * If you were to put the generated Scala in a method, only the 2nd one compiles
     */
    convertFullClass.orElse(convertContentsOfMethod).orElse(convertContentsOfClass) match {
      case success: ConversionSuccess => success
      case _ => MultiConversionFailure(convertFullClass.asInstanceOf[ConversionFailure], convertContentsOfClass.asInstanceOf[ConversionFailure], convertContentsOfMethod.asInstanceOf[ConversionFailure]) //cast is safe because we're only here if all attempts are failures. I could re-write this without casts (nested match for each attempt) but I think this is better
    }
  }
}
