package com.github.dnadolny.javatoscala.conversion

trait Converter {
  def safeConvert(javaSource: String): Option[String]
}