package com.github.dnadolny.javatoscala

import scala.tools.eclipse.ScalaSourceFileEditor

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.text.TextSelection
import org.eclipse.swt.dnd.Clipboard
import org.eclipse.swt.dnd.TextTransfer
import org.eclipse.swt.widgets.Display
import org.eclipse.text.edits.ReplaceEdit
import org.eclipse.ui.handlers.HandlerUtil

import com.github.dnadolny.javatoscala.conversion.ScalagenConverter

class PasteJavaAsScalaCommand(snippetConverter: SnippetConverter) extends AbstractHandler {

  def this() = this(new SnippetConverter(ScalagenConverter))

  private def getClipboardText() = {
    val cb = new Clipboard(Display.getCurrent())
    val textTransfer = TextTransfer.getInstance()
    cb.getContents(textTransfer).toString
  }

  override def execute(event: ExecutionEvent): Object = {
    HandlerUtil.getActiveEditor(event) match {
      case editor: ScalaSourceFileEditor =>
        val text = getClipboardText()
        val document = editor.getDocumentProvider().getDocument(editor.getEditorInput)
        val textSelection = editor.getSelectionProvider().getSelection.asInstanceOf[TextSelection]
        val offset = textSelection.getOffset()

        snippetConverter.convertSnippet(text) match {
          case Some(scala) => {
            val numSpaces = document.get(0, offset).reverse.indexOf('\n')
            val indentedScala = Indenter.indentAllExceptFirstLine(scala, numSpaces)

            val edit = new ReplaceEdit(offset, textSelection.getLength, indentedScala)
            edit.apply(document)
            editor.getSelectionProvider().setSelection(new TextSelection(offset + indentedScala.length, 0))
          }
          case None =>
            MessageDialog.openError(null, "Error converting Java to Scala", "There was a problem converting Java to Scala.\n\nThis is probably because there was a problem parsing the Java code.\n\nMake sure that the code on the clipboard has flawless syntax, including closing brackets and semicolons, and try again.")
        }
      case _ => () //TODO: error
    }
    null
  }
}