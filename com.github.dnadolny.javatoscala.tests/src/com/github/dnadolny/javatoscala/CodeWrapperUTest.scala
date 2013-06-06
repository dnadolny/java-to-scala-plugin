package com.github.dnadolny.javatoscala

import org.junit.Test
import org.junit.Assert._
import CodeWrapper._
import com.github.dnadolny.javatoscala.conversion.ScalagenConverter

class CodeWrapperUTest {
  @Test
  def `wrap code with class` {
    assertEquals("class Snippet { code }", wrapWithClass("code"))
  }
  
  @Test
  def `wrap code with class and method` {
    assertEquals("class Snippet { public void snippet() { code } }", wrapWithClassAndMethod("code")) 
  }
  
  @Test
  def `remove class wrapper (including newlines) and fix indentation` {
    println(ScalagenConverter.safeConvert("class Snippet { private void snippet() { String str; } }"))
    assertEquals("code\n  code2\ncode3", removeClassWrapper("""class Snippet {

  code
    code2
  code3
}"""))
  }
  
  @Test
  def `remove class/method wrapper and fix indentation` {
    assertEquals("code\n  code2\ncode3", removeClassAndMethodWrapper("""class Snippet {

  def snippet() {
    code
      code2
    code3
  }
}"""))
  }
  
  @Test
  def `remove object wrapper and warn about static code` {
    assertEquals("""//***** Static fields/methods below, these should go in a companion object *****
code
//***** End of static fields/methods *****""", removeClassWrapper("""object Snippet {
  code
}"""))
  }
  
  @Test
  def `remove wrapper import and format code correctly` {
    assertEquals("""import abc

instance code

//***** Static fields/methods below, these should go in a companion object *****
static code
//***** End of static fields/methods *****""", removeClassWrapper("""import abc
import Snippet._

object Snippet {
  static code
}

class Snippet {
  instance code
}"""))
  }
}