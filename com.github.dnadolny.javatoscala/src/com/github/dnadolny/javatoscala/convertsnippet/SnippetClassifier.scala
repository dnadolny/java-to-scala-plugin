package com.github.dnadolny.javatoscala.convertsnippet

import org.apache.commons.lang3.StringUtils

object SnippetClassifier {
  def classify(unmodifiedJava: String): SnippetClassification = {
    val java = removeInitialComments(unmodifiedJava)
    
    lazy val isTopLevel = {
      val topLevelPrefix = List("package", "class", "interface", "enum", "public class", "public interface", "public enum", "abstract class", "abstract interface", "public abstract class", "public abstract interface", "abstract public class", "abstract public interface").map(_ + " ")
      val javaOnOneLine = java.replaceAllLiterally("\r\n", "\n").replaceAllLiterally("\n", " ").replaceAll(" +", " ")
      topLevelPrefix.exists(javaOnOneLine.startsWith)
    }
    
    lazy val isContentsOfClass = {
      val keywordsProbablyNotUsedInAMethod = List("static", "public", "abstract", "private", "void")
      keywordsProbablyNotUsedInAMethod.exists(java.contains)
    }
    
    if (isTopLevel) TopLevel
    else if (isContentsOfClass) ContentsOfClass
    else ContentsOfMethod
  }
  
  private def removeInitialComments(java: String): String = {
    var modifiedSource = java.trim
    def hasSingleLineComment = modifiedSource.startsWith("//") && modifiedSource.contains("\n")
    def hasMultiLineComment = modifiedSource.startsWith("/*") && modifiedSource.contains("*/")
    while (hasSingleLineComment || hasMultiLineComment) {
      if (hasSingleLineComment) {
        modifiedSource = StringUtils.substringAfter(modifiedSource, "\n").trim
      } else if (hasMultiLineComment) {
        modifiedSource = StringUtils.substringAfter(modifiedSource, "*/").trim
      }
    }
    modifiedSource
  }
}