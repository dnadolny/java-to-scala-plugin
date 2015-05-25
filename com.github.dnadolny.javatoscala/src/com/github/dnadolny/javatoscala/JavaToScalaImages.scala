package com.github.dnadolny.javatoscala

import java.net.MalformedURLException
import java.net.URL

import org.eclipse.core.runtime.Platform
import org.eclipse.jface.resource.ImageDescriptor
import org.scalaide.ui.ScalaImages

object JavaToScalaImages {
  val Error = create("icons/full/obj16/error_obj.gif")

  private def create(localPath: String) = {
    try {
      val pluginInstallUrl = Platform.getBundle("com.github.dnadolny.javatoscala").getEntry("/")
      val url = new URL(pluginInstallUrl, localPath)
      ImageDescriptor.createFromURL(url)
    } catch {
      case _: MalformedURLException => ScalaImages.MISSING_ICON
    }
  }
}