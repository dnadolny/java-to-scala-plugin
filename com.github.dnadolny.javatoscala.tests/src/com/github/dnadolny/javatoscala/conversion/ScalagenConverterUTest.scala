package com.github.dnadolny.javatoscala.conversion

import org.junit.Test
import org.junit.Assert._

class ScalagenConverterUTest {

  @Test
  def `long lines don't get wrapped` {
    assertEquals("""
class A {

  var strings: List[String] = aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb

  def longMethod(a: AnyRef, b: AnyRef, c: AnyRef, d: AnyRef, e: AnyRef, f: AnyRef, g: AnyRef, h: AnyRef, i: AnyRef, j: AnyRef) {
  }
}
""", ScalagenConverter.safeConvert("""
class A {
  List<String> strings = aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb + aaa + bbb;
  
  public void longMethod(Object a, Object b, Object c, Object d, Object e, Object f, Object g, Object h, Object i, Object j) {
  }
}""").toOption.get)
  }
}