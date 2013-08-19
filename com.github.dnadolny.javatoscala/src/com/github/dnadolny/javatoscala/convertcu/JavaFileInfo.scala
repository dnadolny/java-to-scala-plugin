package com.github.dnadolny.javatoscala.convertcu

import com.github.dnadolny.javatoscala.convertcu.eclipse.jdt.RichCompilationUnit._
import org.eclipse.jdt.core.ICompilationUnit

case class JavaFileInfo(fileName: String, packageName: String, contents: String) {
  def contentsAsComment(lineDelimiter: String) = 
    lineDelimiter + "/*" + lineDelimiter + 
    contents.replaceAllLiterally("/*", "|*").replaceAllLiterally("*/", "*|") + 
    lineDelimiter + "*/"
}

object JavaFileInfo {
  def apply(cu: ICompilationUnit): JavaFileInfo = JavaFileInfo(cu.fileName, cu.packageName, cu.getSource)
}