package com.github.dnadolny.javatoscala.preferences

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer

import com.github.dnadolny.javatoscala.preferences.Preferences._

class PreferencesInitializer extends AbstractPreferenceInitializer {
  override def initializeDefaultPreferences(): Unit = {
    prefs.setDefault(AppendJavaAsCommentKey, true)
  }
}