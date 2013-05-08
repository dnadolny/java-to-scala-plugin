package com.github.dnadolny.javatoscala.conversion

import org.apache.commons.lang3.StringUtils

object ScalagenConverter extends Converter {
  private val ImportJavaConversions = "//remove if not needed\nimport scala.collection.JavaConversions._\n"

  private def needsJavaConversions(scala: String) = List(".map", " <- ", ".find").exists(scala.contains)

  private def removeJavaConversionsImport(scala: String) = StringUtils.replace(scala, ImportJavaConversions, "")

  def safeConvert(javaSource: String): Option[String] = try {
    val scala = com.mysema.scalagen.Converter.getInstance().convert(javaSource)
    if (needsJavaConversions(scala)) Some(scala)
    else Some(removeJavaConversionsImport(scala))
  } catch {
    case e: Throwable => None
  }
}