package com.github.dnadolny.javatoscala.preferences

import org.eclipse.jface.preference.BooleanFieldEditor
import org.eclipse.jface.preference.FieldEditorPreferencePage
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPreferencePage

import com.github.dnadolny.javatoscala.JavaToScalaPlugin

class PreferencesPage extends FieldEditorPreferencePage with IWorkbenchPreferencePage {
  setPreferenceStore(JavaToScalaPlugin.prefStore)

  override def createFieldEditors() {
    addField(new BooleanFieldEditor(Preferences.DeleteJavaAfterConversionKey, Messages.DeleteJavaAfterConversion, getFieldEditorParent))
    addField(new BooleanFieldEditor(Preferences.AppendJavaAsCommentKey, Messages.AppendJavaAsComment, getFieldEditorParent))
  }
  
  def init(workbench: IWorkbench) {}
}