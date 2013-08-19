package com.github.dnadolny.javatoscala

import scala.tools.eclipse.ScalaPlugin

object Preferences {
  private val PrefPrefix = "com.github.dnadolny.java-to-scala."

  private def key(name: String) = PrefPrefix + name

  def prefs = ScalaPlugin.plugin.getPreferenceStore

  val AlreadyPrintedNoticeKey = key("alreadyPrintedNotice")
  def alreadyPrintedNotice: Boolean = prefs.getBoolean(AlreadyPrintedNoticeKey)
  def setAlreadyPrintedNotice(): Unit = prefs.setValue(AlreadyPrintedNoticeKey, true)

  val DeleteJavaAfterConversionKey = key("deleteJavaAfterConversion")
  def deleteJavaAfterConversion: Boolean = prefs.getBoolean(DeleteJavaAfterConversionKey)
  def deleteJavaAfterConversion_=(value: Boolean): Unit = prefs.setValue(DeleteJavaAfterConversionKey, value)

  val AppendJavaAsCommentKey = key("appendJavaAsComment")
  def appendJavaAsComment: Boolean = prefs.getBoolean(AppendJavaAsCommentKey)
  def appendJavaAsComment_=(value: Boolean): Unit = prefs.setValue(AppendJavaAsCommentKey, value)
}