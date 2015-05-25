package com.github.dnadolny.javatoscala.convertcu

import org.eclipse.core.resources.ResourcesPlugin
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.core.JavaCore
import org.eclipse.jdt.ui._
import org.eclipse.jface.dialogs.Dialog
import org.eclipse.jface.dialogs.IDialogConstants
import org.eclipse.jface.viewers._
import org.eclipse.swt.custom.CLabel
import org.eclipse.swt.events._
import org.eclipse.swt.layout._
import org.eclipse.swt.widgets._
import org.eclipse.swt.SWT
import com.github.dnadolny.javatoscala.jface.TreeViewerAdapter
import com.github.dnadolny.javatoscala._
import com.github.dnadolny.javatoscala.preferences._

class TargetFolderDialog(shell: Shell) extends Dialog(shell) {
  private var errorLabel: CLabel = _

  private var targetSourceFolder: IPackageFragmentRoot = _
  
  def settings = CUConversionSettings(targetSourceFolder, Preferences.deleteJavaAfterConversion, Preferences.appendJavaAsComment, "\n") //can get line delimiter from StubUtility.getLineDelimiterUsed(packageFragment.getJavaProject)

  protected override def configureShell(shell: Shell): Unit = {
    super.configureShell(shell)
    shell.setText("Convert to Scala")
  }

  protected override def createDialogArea(parent: Composite): Control = {
    val container = createContainer(parent)
    createChooseDestinationLabel(container)
    createSourceFolderTreeView(container)
    createDeleteJavaCheckbox(container)
    createAppendJavaCheckbox(container)
    createErrorLabel(container)
    container
  }

  private def createContainer(parent: Composite): Composite = {
    val container = new Composite(parent, SWT.NONE)
    container.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true))
    val containerLayout = new GridLayout
    containerLayout.marginHeight = 10
    containerLayout.marginWidth = 10
    container.setLayout(containerLayout)
    container
  }
  
  private def createChooseDestinationLabel(container: Composite): Unit = {
    val chooseDestinationLabel = new Label(container, SWT.NONE)
    chooseDestinationLabel.setText("Choose destination source folder:")
  }

  private def createSourceFolderTreeView(container: Composite): Unit = {
    val treeViewer = new TreeViewer(new Tree(container, SWT.SINGLE))

    treeViewer.setContentProvider(new StandardJavaElementContentProvider)
    treeViewer.setLabelProvider(new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_DEFAULT))
    treeViewer.setComparator(new JavaElementComparator) //comment out
    treeViewer.addFilter(new SourceFolderFilter)

    val workspaceRoot = ResourcesPlugin.getWorkspace().getRoot()
    treeViewer.setInput(JavaCore.create(workspaceRoot))
    
    treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
      override def selectionChanged(event: SelectionChangedEvent) {
        val selection = event.getSelection.asInstanceOf[IStructuredSelection]
        validate(selection.getFirstElement)
      }
    })

    val treeWidget = treeViewer.getTree
    val widgetGridData = new GridData(GridData.FILL_BOTH)
    widgetGridData.widthHint = convertWidthInCharsToPixels(60)
    widgetGridData.heightHint = convertHeightInCharsToPixels(16)
    treeWidget.setLayoutData(widgetGridData)
    
    treeViewer.addTreeListener(new TreeViewerAdapter() {
      override def treeExpanded(event: TreeExpansionEvent): Unit = {
        //because the tree is filtered (lazily), when you expand a project the source folders below it have a + button
        //when you click the + button, it goes away because there are no un-filtered children
        //we correct that here by removing children of source folders on expansion so the + button goes away
        for {
          expandedItem <- treeViewer.getTree.getItems.find(_.getData == event.getElement)
          child <- expandedItem.getItems if child.getData.isInstanceOf[IPackageFragmentRoot]
        } {
          child.removeAll
        }
      }
    })
    
    treeWidget.addSelectionListener(new SelectionAdapter() {
      override def widgetDefaultSelected(e: SelectionEvent): Unit = { //double click handler
        //TODO: it would be nice to expand a project when double clicked
        if (okButton.isEnabled)
          pressOKButton
      }
    })
  }
  
  private def createDeleteJavaCheckbox(container: Composite): Unit = {
    val deleteJavaCheckbox = new Button(container, SWT.CHECK)
    deleteJavaCheckbox.setText(Messages.DeleteJavaAfterConversion)
    deleteJavaCheckbox.setSelection(Preferences.deleteJavaAfterConversion)
    deleteJavaCheckbox.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(event: SelectionEvent): Unit = {
        Preferences.deleteJavaAfterConversion = deleteJavaCheckbox.getSelection
      }
    })
  }
  
  private def createAppendJavaCheckbox(container: Composite): Unit = {
    val appendJavaCheckbox = new Button(container, SWT.CHECK)
    appendJavaCheckbox.setText(Messages.AppendJavaAsComment)
    appendJavaCheckbox.setSelection(Preferences.appendJavaAsComment)
    appendJavaCheckbox.addSelectionListener(new SelectionAdapter() {
      override def widgetSelected(event: SelectionEvent): Unit = {
        Preferences.appendJavaAsComment = appendJavaCheckbox.getSelection
      }
    })
  }
  
  private def createErrorLabel(container: Composite): Unit = {
    errorLabel = new CLabel(container, SWT.SHADOW_NONE)
    errorLabel.setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, true))
  }

  protected override def createButtonBar(parent: Composite): Control = {
    val control = super.createButtonBar(parent)
    okButton.setEnabled(false)
    control
  }
  
  private def pressOKButton = buttonPressed(IDialogConstants.OK_ID)

  private def okButton = getButton(IDialogConstants.OK_ID)

  private def validate(selection: AnyRef): Unit = selection match {
    case sourceFolder: IPackageFragmentRoot =>
      targetSourceFolder = sourceFolder
      okButton.setEnabled(true)
      errorLabel.setImage(null)
      errorLabel.setText("")
    case _ =>
      targetSourceFolder = null
      okButton.setEnabled(false)
      errorLabel.setImage(JavaToScalaImages.Error.createImage())
      errorLabel.setText("Select a source folder")
  }
  
  protected override def isResizable = true
}
//TODO: persist the target source folder in their preferences
