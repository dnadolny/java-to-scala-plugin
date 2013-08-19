package com.github.dnadolny.javatoscala.text

import org.junit.Test
import org.junit.Assert._

class IndenterUTest {
  @Test
  def `doesn't indent first line` {
      val toIndent = 
"""a
b
  c
d
e"""
    val expected = 
"""a
     b
       c
     d
     e"""
    assertEquals(expected, Indenter.indentAllExceptFirstLine(toIndent, numSpaces = 5))
  }
  
  @Test
  def `empty input` {
    assertEquals("", Indenter.indentAllExceptFirstLine("", numSpaces = 2))
  }
}