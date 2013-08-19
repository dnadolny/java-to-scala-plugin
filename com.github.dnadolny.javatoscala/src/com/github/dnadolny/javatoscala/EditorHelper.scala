package com.github.dnadolny.javatoscala

import org.eclipse.core.resources.IFile
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility
import org.eclipse.jdt.ui.JavaUI
import org.eclipse.jface.text.IDocument
import org.eclipse.ui.IWorkbenchWindow

object EditorHelper {
  def openEditor(window: IWorkbenchWindow, cu: ICompilationUnit): IDocument = {
    val workspaceFile = cu.getUnderlyingResource.asInstanceOf[IFile]
    val page = window.getActivePage()
    val part = EditorUtility.openInEditor(workspaceFile, true)
    page.bringToTop(part)
    JavaUI.getDocumentProvider().getDocument(part.getEditorInput())
  }
}