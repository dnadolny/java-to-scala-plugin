package com.github.dnadolny.javatoscala.convertcu

import scala.collection.JavaConverters.asScalaIteratorConverter

import org.eclipse.core.runtime.jobs.Job
import org.eclipse.core.runtime.IProgressMonitor
import org.eclipse.core.runtime.IStatus
import org.eclipse.core.runtime.MultiStatus
import org.eclipse.core.runtime.Status
import org.eclipse.core.runtime.SubProgressMonitor
import org.eclipse.jdt.core.ICompilationUnit
import org.eclipse.jdt.core.IPackageFragment
import org.eclipse.jdt.core.IPackageFragmentRoot
import org.eclipse.jdt.core.WorkingCopyOwner
import org.eclipse.jface.action.IAction
import org.eclipse.jface.dialogs.MessageDialog
import org.eclipse.jface.viewers.TreeSelection
import org.eclipse.jface.window.Window
import org.eclipse.ui.actions.ActionDelegate
import org.eclipse.ui.IWorkbenchWindow
import org.eclipse.ui.PlatformUI

import com.github.dnadolny.javatoscala.conversion.ConversionFailure
import com.github.dnadolny.javatoscala.conversion.ConversionSuccess
import com.github.dnadolny.javatoscala.conversion.ScalagenConverter
import com.github.dnadolny.javatoscala.convertcu.eclipse.jdt.RichPackageFragmentRoot._
import com.github.dnadolny.javatoscala.convertcu.eclipse.jdt.RichPackageFragment._
import com.github.dnadolny.javatoscala.EditorHelper
import com.github.dnadolny.javatoscala.JavaToScalaPlugin
   
class ConvertCompilationUnitsAction extends ActionDelegate {
  private val converter = ScalagenConverter
  private val NullProgressMonitor: IProgressMonitor = null //yes, really

  override def run(action: IAction) {
    val window = PlatformUI.getWorkbench.getActiveWorkbenchWindow

    val dialog = new TargetFolderDialog(window.getShell)
    if (dialog.open == Window.OK) {
      val conversionSettings = dialog.settings
      compilationUnitsToConvert(window) match {
        case Nil => MessageDialog.openError(window.getShell, "No Java files selected", "There were no Java files selected to be converted. Select a:\n - Java class\n - Java package\n - Java source folder\nand try again.")
        case oneCompilationUnit :: Nil => convertAndOpen(oneCompilationUnit, conversionSettings, window)
        case toConvert => convertAll(toConvert, conversionSettings, window)
      }
    }
  }
  
  def compilationUnitsToConvert(window: IWorkbenchWindow): List[ICompilationUnit] = {
    val selections = window.getSelectionService.getSelection.asInstanceOf[TreeSelection]
    def cusForSelection(selection: Any) = selection match {
      case cu: ICompilationUnit => List(cu)
      case pkg: IPackageFragment => (pkg :: pkg.subpackages).flatMap(_.getCompilationUnits)
      case sourceFolder: IPackageFragmentRoot => sourceFolder.getChildren.toList.collect{case okg: IPackageFragment => okg.getCompilationUnits}.flatten
    }
    selections.iterator.asScala.toList.flatMap(cusForSelection).distinct
  }
  
  private def convertAndOpen(cu: ICompilationUnit, settings: CUConversionSettings, window: IWorkbenchWindow): Unit = convert(cu, JavaFileInfo(cu), settings) match {
    case CUConversionSuccess(scalaCU) => EditorHelper.openEditor(window, scalaCU)
    case CUConversionFailure(e) => MessageDialog.openError(window.getShell, "Problem converting", "There was a problem with the conversion:\n" + e.getMessage)
  }

  private def convertAll(cus: List[ICompilationUnit], settings: CUConversionSettings, window: IWorkbenchWindow): Unit = {
    val conversionJob = new Job("Java to Scala conversion") {
      setUser(true)

      protected override def run(monitor: IProgressMonitor): IStatus = {
        monitor.beginTask("Converting " + cus.size + " files to Scala", cus.size)

        val results = for (cu <- cus if !monitor.isCanceled) yield {
          val javaInfo = JavaFileInfo(cu)
          monitor.subTask("Converting " + javaInfo.fileName)
          val result = convert(cu, javaInfo, settings)
          monitor.worked(1)
          (cu, result)
        }
        monitor.done()

        results.collect{case (cu, CUConversionFailure(e)) => new Status(IStatus.ERROR, JavaToScalaPlugin.Id, cu.getPath.toString + ": " + e.getMessage)} match {
          case Nil => Status.OK_STATUS
          case errors => new MultiStatus(JavaToScalaPlugin.Id, IStatus.ERROR, errors.toArray, "Not all files could be converted", null)
        }
      }
    }
    conversionJob.schedule()
  }

  private def reconcile(cu: ICompilationUnit, astLevel: Int = ICompilationUnit.NO_AST, forceProblemDetection: Boolean = false, enableStatementsRecovery: Boolean = false, workingCopyOwner: WorkingCopyOwner = null, monitor: IProgressMonitor = null): Unit = {
    cu.reconcile(astLevel, forceProblemDetection, enableStatementsRecovery, workingCopyOwner, monitor) //TODO: do we need to do this?
  }

  private def convert(javaCU: ICompilationUnit, javaInfo: JavaFileInfo, settings: CUConversionSettings): CUConversionResult = converter.safeConvert(javaInfo.contents) match {
    case ConversionSuccess(converted) => {
      val targetPackage = settings.targetSourceFolder.getOrCreatePackageFragment(javaInfo.packageName)
      val scalaFileName = javaInfo.fileName.replaceAllLiterally(".java", ".scala")

      try {
        val scalaCU = targetPackage.createCompilationUnit(scalaFileName, "", false, NullProgressMonitor) //will fail with an exception if it already exists
        scalaCU.becomeWorkingCopy(NullProgressMonitor)

        val buffer = scalaCU.getBuffer
        if (settings.appendJavaAsComment) {
          buffer.append(ScalaSourceModifier.addComment(converted, javaInfo.contentsAsComment(settings.lineDelimiter), settings.lineDelimiter))
        } else {
          buffer.append(converted.replaceAllLiterally("\n", settings.lineDelimiter))
        }
        reconcile(scalaCU)
  
        scalaCU.commitWorkingCopy(true, NullProgressMonitor)
        scalaCU.discardWorkingCopy()
        
        if (settings.deleteJavaAfterConversion) {
          javaCU.delete(false, NullProgressMonitor) //will fail with an exception if the file is out of sync with the fs
        }

        CUConversionSuccess(scalaCU)
      } catch {
        case e: Throwable => CUConversionFailure(e)
      }
    }
    case ConversionFailure(_, e) => CUConversionFailure(e)
  }
}