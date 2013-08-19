package com.github.dnadolny.javatoscala.convertcu

import org.eclipse.jdt.core.ICompilationUnit

sealed trait CUConversionResult
case class CUConversionSuccess(scalaCU: ICompilationUnit) extends CUConversionResult
case class CUConversionFailure(error: Throwable) extends CUConversionResult