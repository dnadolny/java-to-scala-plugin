package com.github.dnadolny.javatoscala.jface

import org.eclipse.jface.viewers.ITreeViewerListener
import org.eclipse.jface.viewers.TreeExpansionEvent

trait TreeViewerAdapter extends ITreeViewerListener {
  override def treeCollapsed(event: TreeExpansionEvent): Unit = {}
  override def treeExpanded(event: TreeExpansionEvent): Unit = {}
}