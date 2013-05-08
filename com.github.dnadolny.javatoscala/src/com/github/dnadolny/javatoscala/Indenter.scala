package com.github.dnadolny.javatoscala

object Indenter {
  def indentAllExceptFirstLine(scala: String, numSpaces: Int) = {
    //we really do want to split on '\n' since scalagen gives it to us that way
    if (numSpaces <= 0)
      scala
    else scala.split('\n').toList match {
      case line :: Nil => line
      case head :: tail => head + "\n" + tail.map(" " * numSpaces + _).mkString("\n")
    }
  }
}