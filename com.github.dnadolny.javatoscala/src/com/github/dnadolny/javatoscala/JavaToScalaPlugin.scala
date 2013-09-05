package com.github.dnadolny.javatoscala

import org.eclipse.ui.plugin.AbstractUIPlugin
import org.osgi.framework.BundleContext

object JavaToScalaPlugin {
  val Id = "com.github.dnadolny.javatoscala"
  val Url = "https://github.com/dnadolny/java-to-scala-plugin"
    
  @volatile var plugin: JavaToScalaPlugin = _
  
  def prefStore = plugin.getPreferenceStore
}

class JavaToScalaPlugin extends AbstractUIPlugin {
  override def start(context: BundleContext) = {
    super.start(context)
    JavaToScalaPlugin.plugin = this
  }

  override def stop(context: BundleContext) = {
    JavaToScalaPlugin.plugin = null
    super.stop(context)
  }
}