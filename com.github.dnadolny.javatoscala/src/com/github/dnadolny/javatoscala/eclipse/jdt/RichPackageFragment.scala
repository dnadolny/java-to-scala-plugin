package com.github.dnadolny.javatoscala.convertcu.eclipse.jdt

import com.github.dnadolny.javatoscala.convertcu.eclipse.jdt.RichPackageFragment._

import org.eclipse.jdt.core.IPackageFragment
import org.eclipse.jdt.core.IPackageFragmentRoot

case class RichPackageFragment(pkg: IPackageFragment) {
  private val DefaultPackage = ""
  
  lazy val packageParts = pkg.getElementName.split('.')

  def subpackages: List[IPackageFragment] = pkg.getElementName match {
    case DefaultPackage =>
      List() //default package has no subpackages, but with the algorithm below it would have every package as a subpackage so we special case it
    case packageName =>
      val sourceFolder = pkg.getParent.asInstanceOf[IPackageFragmentRoot]
      for {
        otherPackage <- sourceFolder.getChildren.toList.collect { case pkg: IPackageFragment => pkg }
        if otherPackage.packageParts.startsWith(packageParts) && otherPackage != pkg
      } yield otherPackage
  }
}

object RichPackageFragment {
  implicit def pfToRich(pkg: IPackageFragment): RichPackageFragment = RichPackageFragment(pkg)
}