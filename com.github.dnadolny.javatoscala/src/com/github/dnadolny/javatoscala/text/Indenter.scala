package com.github.dnadolny.javatoscala.text

object Indenter {
  private val ScalagenLineBreak = '\n'
  
  def indentAllExceptFirstLine(scala: String, numSpaces: Int) = {
    if (numSpaces <= 0)
      scala
    else (scala.split(ScalagenLineBreak).toList: @unchecked) match { //@unchecked for Nil since String.split never returns an empty array
      case line :: Nil => line
      case head :: tail => head + "\n" + tail.map(" " * numSpaces + _).mkString("\n")
    }
  }
}