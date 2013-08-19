package com.github.dnadolny.javatoscala.convertcu

import org.eclipse.jdt.core.IPackageFragmentRoot

case class CUConversionSettings(targetSourceFolder: IPackageFragmentRoot, deleteJavaAfterConversion: Boolean, appendJavaAsComment: Boolean, lineDelimiter: String)