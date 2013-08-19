package com.github.dnadolny.javatoscala.convertcu

import com.github.dnadolny.javatoscala.util.TryOption._

import org.eclipse.jface.viewers.Viewer
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter
import org.eclipse.jdt.core.IJavaProject

class SourceFolderFilter extends TypedViewerFilter(Array(classOf[IPackageFragmentRoot], classOf[IJavaProject])) {
  override def select(viewer: Viewer, parent: AnyRef, element: AnyRef): Boolean = element match {
    case packageRoot: IPackageFragmentRoot => tryo(packageRoot.getKind == IPackageFragmentRoot.K_SOURCE).getOrElse(false)
    case _ => super.select(viewer, parent, element)
  }
}