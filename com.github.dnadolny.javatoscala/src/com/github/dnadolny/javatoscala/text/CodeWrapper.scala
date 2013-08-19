package com.github.dnadolny.javatoscala.text

import org.apache.commons.lang3.StringUtils

object CodeWrapper {
  private val StaticWarningStart = "//***** Static fields/methods below, these should go in a companion object *****\n"
  private val StaticWarningEnd = "\n//***** End of static fields/methods *****"
    
  def wrapWithClass(snippet: String) = "class Snippet { " + snippet + " }"
  
  private def strOption(str: String) = Option(str).map(_.trim).filter(_ != "")

  def removeClassWrapper(fullScala: String) = {
    if (fullScala.contains("object Snippet {\n")) {
      val imports = StringUtils.substringBefore(fullScala, "object Snippet {\n")
      val rest = fullScala.substring(imports.length)
      val theObject = StringUtils.substringBefore(rest, "class Snippet {\n")
      val theClass = rest.substring(theObject.length)
      
      val importsToPrint = strOption(imports.replaceAllLiterally("import Snippet._\n", ""))
      val classToPrint = strOption(removeWrapper(theClass, "class Snippet {\n", "\n}"))
      val objectToPrint = strOption(removeWrapper(theObject, "object Snippet {\n", "\n}")).map(StaticWarningStart + _ + StaticWarningEnd)
      List(importsToPrint, classToPrint, objectToPrint).flatten.mkString("\n\n")
    } else {
      removeWrapper(fullScala, "class Snippet {\n", "\n}")
    }
  }
  
  def wrapWithClassAndMethod(snippet: String) = "class Snippet { public void snippet() { " + snippet + " } }"

  def removeClassAndMethodWrapper(fullScala: String) = {
    val withoutMethodWrapper = removeWrapper(fullScala, "  def snippet() {", "\n  }")
    removeWrapper(withoutMethodWrapper, "class Snippet {", "\n}")
  }

  def removeWrapper(code: String, wrapperStart: String, wrapperEnd: String) = {
    val before = StringUtils.substringBefore(code, wrapperStart)
    val rest = StringUtils.substringAfter(code, wrapperStart)
    val middle = StringUtils.substringBefore(rest, wrapperEnd)
    val after = StringUtils.substringAfter(rest, wrapperEnd)
    (before + middle.replaceAll("\n  ", "\n") + after).replaceAll("\n\n+", "\n\n").trim
  }
}
