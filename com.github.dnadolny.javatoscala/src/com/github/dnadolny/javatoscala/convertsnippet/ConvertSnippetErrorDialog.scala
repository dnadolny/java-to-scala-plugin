package com.github.dnadolny.javatoscala.convertsnippet

import java.net.URL

import scala.tools.eclipse.util.HasLogger

import org.eclipse.jface.dialogs.Dialog
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.swt.custom.CLabel
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.SWT
import org.eclipse.ui.PlatformUI

import com.github.dnadolny.javatoscala.conversion.ConversionFailure
import com.github.dnadolny.javatoscala.conversion.MultiConversionFailure
import com.github.dnadolny.javatoscala.util.Java7Feature
import com.github.dnadolny.javatoscala.JavaToScalaPlugin

class ConvertSnippetErrorDialog(shell: Shell, conversionFailure: MultiConversionFailure) extends Dialog(shell) with HasLogger {
  protected override def configureShell(shell: Shell): Unit = {
    super.configureShell(shell)
    shell.setText("Error converting Java to Scala")
  }

  protected override def createDialogArea(parent: Composite): Control = {
    val container = createContainer(parent)
    container.setLayoutData(fill)

    createErrorLabel(container)
    createJava7Warning(container)
    createCodeErrorTabs(container)
    createReportBugLink(container)

    container
  }

  private def createContainer(parent: Composite): Composite = {
    val container = new Composite(parent, SWT.NONE)
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false))
    val containerLayout = new GridLayout
    containerLayout.marginHeight = 10
    containerLayout.marginWidth = 10
    container.setLayout(containerLayout)
    container
  }

  def createErrorLabel(container: Composite): Unit = {
    val errorLabel = new CLabel(container, SWT.SHADOW_NONE)
    errorLabel.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false))
    errorLabel.setText("There was a problem converting Java to Scala.\nThis is most likely because there was a problem parsing the Java code.\nMake sure that the code on the clipboard has flawless syntax, including closing brackets and semicolons, and try again.")
    errorLabel.setImage(errorImage)
  }

  def createJava7Warning(container: Composite): Unit = {
    Java7Feature.detectFeatures(conversionFailure.convertFullClass.javaSource) match {
      case Nil =>
      case features =>
        val java7detected = new CLabel(container, SWT.SHADOW_NONE)
        java7detected.setLayoutData(new GridData(SWT.TOP, SWT.LEFT, false, false))
        java7detected.setText("It looks like you might be using Java 7 features.\nUnfortunately, the Java parser used by this plugin doesn't support Java 7 yet.\nThese are the features detected:\n" + features.map(_.description).mkString(" - ", "\n - ", ""))
        java7detected.setImage(warningImage)
    }
  }

  def createCodeErrorTabs(container: Composite): Unit = {
    val codeErrorTabs = new TabFolder(container, SWT.BORDER)
    val tabsLayoutData = new GridData(SWT.TOP, SWT.LEFT, true, true)
    tabsLayoutData.minimumWidth = 500
    tabsLayoutData.minimumHeight = 350
    codeErrorTabs.setLayoutData(tabsLayoutData)

    def addTab(title: String, failure: ConversionFailure): Unit = {
      val tabItem = new TabItem(codeErrorTabs, SWT.NULL)
      tabItem.setText(title)

      val tabContainer = new Composite(codeErrorTabs, SWT.NONE)
      tabContainer.setLayoutData(fill)
      tabContainer.setLayout(new GridLayout)

      val exceptionLabel = new Label(tabContainer, SWT.NONE)
      exceptionLabel.setText("Exception:")

      val fullError = new Text(tabContainer, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER | SWT.V_SCROLL)
      fullError.setText(failure.cause.getMessage)
      val fullErrorLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true)
      fullErrorLayoutData.minimumHeight = 100
      fullError.setLayoutData(fullErrorLayoutData)

      val codeLabel = new Label(tabContainer, SWT.NONE)
      codeLabel.setText("Java code:")
      val javaText = new Text(tabContainer, SWT.READ_ONLY | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL)
      javaText.setText(failure.javaSource)
      val javaTextLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true)
      javaTextLayoutData.minimumHeight = 100
      javaText.setLayoutData(javaTextLayoutData)

      tabItem.setControl(tabContainer)
    }

    addTab("Unmodified", conversionFailure.convertFullClass)
    addTab("Wrapped in a class", conversionFailure.convertContentsOfClass)
    addTab("Wrapped in a method", conversionFailure.convertContentsOfMethod)

    val selectionIndex = SnippetClassifier.classify(conversionFailure.convertFullClass.javaSource) match {
      case TopLevel => 0
      case ContentsOfClass => 1
      case ContentsOfMethod => 2
    }
    codeErrorTabs.setSelection(selectionIndex)

    def addWhy(): Unit = {
      val tabItem = new TabItem(codeErrorTabs, SWT.NULL)
      tabItem.setText("Why?")

      val explanation = new Label(codeErrorTabs, SWT.WRAP)
      explanation.setLayoutData(fill)
      explanation.setText(
        """Why there are 3 tabs with different Java code in each:

In order to be converted to Scala, the Java code needs to have flawless syntax. This includes being part of a class/method.
To allow you to convert snippets like "int x = 5;", it needs to be wrapped in a class.
For convenience, if the code on the clipboard fails to parse as it is, it is wrapped in a class and a method.
Unfortunately this means that when there is an error, there are 3 different pieces of Java code that don't parse, and it's hard to tell for sure which one was intended.""")

      tabItem.setControl(explanation)
    }

    addWhy()
  }

  def createReportBugLink(container: Composite): Unit = {
    val reportBugLink = new Link(container, SWT.NONE)
    reportBugLink.setText("Report any bugs at <a href=\"" + JavaToScalaPlugin.Url + "\">" + JavaToScalaPlugin.Url + "</a>")
    reportBugLink.addListener(SWT.Selection, linkListener)
  }

  override protected def createButtonsForButtonBar(parent: Composite) {
    createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true)
  }

  private def errorImage = getImage(SWT.ICON_ERROR)

  private def warningImage = getImage(SWT.ICON_WARNING)

  private def getImage(id: Int) = {
    var image: Image = null
    val display = shell.getDisplay
    display.syncExec(new Runnable() {
      override def run: Unit = {
        image = display.getSystemImage(id)
      }
    })
    image
  }

  object linkListener extends Listener {
    def handleEvent(e: Event): Unit = {
      val browserSupport = PlatformUI.getWorkbench.getBrowserSupport
      browserSupport.getExternalBrowser.openURL(new URL(e.text))
    }
  }
  
  private def fill = new GridData(SWT.FILL, SWT.FILL, true, true)

  protected override def isResizable = true
}