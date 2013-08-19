package com.github.dnadolny.javatoscala.convertcu.eclipse.jdt

import org.eclipse.jdt.core.IPackageFragmentRoot

case class RichPackageFragmentRoot(root: IPackageFragmentRoot) {
  def getOrCreatePackageFragment(packageName: String) = {
    val packageFragment = root.getPackageFragment(packageName)
    if (packageFragment.exists) {
      packageFragment
    } else {
      root.createPackageFragment(packageName, /*force*/ false, /*progress monitor*/ null)
    }
  }
}

object RichPackageFragmentRoot {
  implicit def pfrToRich(root: IPackageFragmentRoot) = RichPackageFragmentRoot(root)
}