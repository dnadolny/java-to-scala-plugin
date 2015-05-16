package com.github.dnadolny.javatoscala.convertsnippet

import org.eclipse.core.commands.AbstractHandler
import org.eclipse.core.commands.ExecutionEvent
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.text.TextSelection
import org.eclipse.swt.dnd.Clipboard
import org.eclipse.swt.dnd.TextTransfer
import org.eclipse.swt.widgets.Display
import org.eclipse.text.edits.ReplaceEdit
import org.eclipse.ui.PlatformUI
import org.eclipse.ui.handlers.HandlerUtil
import org.eclipse.ui.texteditor.ITextEditor

import com.github.dnadolny.javatoscala.conversion.ConversionSuccess
import com.github.dnadolny.javatoscala.conversion.MultiConversionFailure
import com.github.dnadolny.javatoscala.conversion.ScalagenConverter
import com.github.dnadolny.javatoscala.conversion.SnippetConverter
import com.github.dnadolny.javatoscala.text.Indenter
import com.github.dnadolny.javatoscala.JavaToScalaPlugin
import com.github.dnadolny.javatoscala.preferences.Preferences

class PasteJavaAsScalaCommand(snippetConverter: SnippetConverter) extends AbstractHandler {
  
  private val ConversionWarning = """/*
 * One-time warning:
 * The Java to Scala conversion is nowhere near perfect (for various reasons).
 * The recommended way to use this feature is as the starting point for a conversion.
 * You should expect to heavily refactor the converted code, as well as review it for correctness.
 *
 * Report bugs to: """ + JavaToScalaPlugin.Url + """
 */
"""

  def this() = this(new SnippetConverter(ScalagenConverter))

  private def getClipboardText() = {
    val cb = new Clipboard(Display.getCurrent())
    val textTransfer = TextTransfer.getInstance()
    cb.getContents(textTransfer).toString
  }

  override def execute(event: ExecutionEvent): Object = {
    HandlerUtil.getActiveEditor(event) match {
      case editor: ITextEditor =>
        val text = getClipboardText()
        val document = editor.getDocumentProvider().getDocument(editor.getEditorInput)
        val textSelection = editor.getSelectionProvider().getSelection.asInstanceOf[TextSelection]
        val offset = textSelection.getOffset()

        snippetConverter.convertSnippet(text) match {
          case ConversionSuccess(convertedScala) => {
            val scala = if (Preferences.alreadyPrintedNotice) {
              convertedScala
            }
            else {
              Preferences.setAlreadyPrintedNotice()
              ConversionWarning + convertedScala
            }
            val numSpaces = document.get(0, offset).reverse.indexOf('\n')
            val indentedScala = Indenter.indentAllExceptFirstLine(scala, numSpaces)

            val edit = new ReplaceEdit(offset, textSelection.getLength, indentedScala)
            edit.apply(document)
            editor.getSelectionProvider().setSelection(new TextSelection(offset + indentedScala.length, 0))
          }
          case failure: MultiConversionFailure =>
            val window = PlatformUI.getWorkbench.getActiveWorkbenchWindow
            new ConvertSnippetErrorDialog(window.getShell, failure).open()
        }
      case _ => MessageDialog.openError(null, "Error converting Java to Scala", "Couldn't cast the editor to ITextEditor. This should never happen since the plugin.xml won't enable the menu item unless we're in a Scala editor")
    }
    null
  }
}