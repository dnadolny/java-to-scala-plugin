package com.github.dnadolny.javatoscala.conversion

import org.apache.commons.lang3.StringUtils

object ScalagenConverter extends Converter {
  private val ImportJavaConversions = "//remove if not needed\nimport scala.collection.JavaConversions._\n"
  private val ExtraNewlinesAfterPackage = "^package (.*?)\n\n\n"

  private def needsJavaConversions(scala: String) = List(".map", " <- ", ".find").exists(scala.contains)

  private def removeJavaConversionsImport(scala: String) = StringUtils.replace(scala, ImportJavaConversions, "").replaceAll(ExtraNewlinesAfterPackage, "package $1\n\n")

  def safeConvert(javaSource: String): ConversionResult = try {
    val scala = com.mysema.scalagen.Converter.getInstance().convert(javaSource)
    if (needsJavaConversions(scala)) ConversionSuccess(scala)
    else ConversionSuccess(removeJavaConversionsImport(scala))
  } catch {
    case e: Throwable => ConversionFailure(javaSource, e)
  }
}