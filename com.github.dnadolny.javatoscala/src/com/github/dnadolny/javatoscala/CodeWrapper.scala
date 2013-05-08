package com.github.dnadolny.javatoscala

import org.apache.commons.lang3.StringUtils

object CodeWrapper {
  def wrapWithClass(snippet: String) = "class Snippet { " + snippet + " }"

  def removeClassWrapper(fullScala: String) = removeWrapper(fullScala, "class Snippet {\n", "\n}")

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