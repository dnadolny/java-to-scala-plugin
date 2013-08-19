package com.github.dnadolny.javatoscala

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer

import com.github.dnadolny.javatoscala.Preferences._

class PreferencesInitializer extends AbstractPreferenceInitializer {
  override def initializeDefaultPreferences(): Unit = {
    prefs.setDefault(DeleteJavaAfterConversionKey, true)
    prefs.setDefault(AppendJavaAsCommentKey, true)
  }
}