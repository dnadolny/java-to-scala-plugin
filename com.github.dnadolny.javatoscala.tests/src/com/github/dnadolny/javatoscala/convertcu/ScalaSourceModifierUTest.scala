package com.github.dnadolny.javatoscala.convertcu

import org.junit.Test
import org.junit.Assert._

class ScalaSourceModifierUTest {
  @Test
  def `add comment to class that ends with curly brace` {
    assertEquals("""
class A {
  val a = "abc"

/*
comment
*/
}""", ScalaSourceModifier.addComment("""
class A {
  val a = "abc"
}
""", "\n/*\ncomment\n*/", "\n"))
  }
  
  @Test
  def `add comment to class that doesn't end with curly brace` {
    assertEquals("""class A {
/*
comment
*/
}""", ScalaSourceModifier.addComment("""
class A
""", "\n/*\ncomment\n*/", "\n"))
  }
}