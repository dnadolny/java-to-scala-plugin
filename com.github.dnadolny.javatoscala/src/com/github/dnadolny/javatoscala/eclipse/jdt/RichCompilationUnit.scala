package com.github.dnadolny.javatoscala.convertcu.eclipse.jdt

import org.eclipse.jdt.core.ICompilationUnit

case class RichCompilationUnit(cu: ICompilationUnit) {
  private val DefaultPackageName = ""
  
  def fileName = cu.getUnderlyingResource.getName

  def packageName = cu.getPackageDeclarations.toList match {
    case Nil => DefaultPackageName
    case declaration :: _ => declaration.getElementName //"normally only 1" from the JavaDoc
  }
}

object RichCompilationUnit {
  implicit def compilationUnitToRichCompilationUnit(cu: ICompilationUnit) = RichCompilationUnit(cu)
}