package com.github.dnadolny.javatoscala.convertcu

import org.apache.commons.lang3.StringUtils

object ScalaSourceModifier {
  def addComment(converted: String, comment: String, lineDelimiter: String): String = {
    if (converted.trim.endsWith("}")) {
      StringUtils.substringBeforeLast(converted, "}").replaceAllLiterally("\n", lineDelimiter) +
      comment + lineDelimiter + "}"
    } else {
      converted.trim.replaceAllLiterally("\n", lineDelimiter) + " {" + comment + lineDelimiter + "}"
    }
  }
}