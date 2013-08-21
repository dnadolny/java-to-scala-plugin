package com.github.dnadolny.javatoscala.conversion

import org.junit.Test
import org.junit.Assert._

class SnippetConverterTest {
  private val converter = new SnippetConverter(ScalagenConverter)
  @Test
  def `convert full java class` {
    assertEquals("""import java.util.List

class A {

  var strings: List[String] = _
}
""", converter.convertSnippet("""
import java.util.List;
class A {
  List<String> strings;
}""").toOption.get)
  }
  
  @Test
  def `convert variable definition by wrapping it in a method` {
    assertEquals("val str: String = null", converter.convertSnippet("String str;").toOption.get)
  }
  
  @Test
  def `convert method that needs to be wrapped in a class` {
    assertEquals("def method() {\n}", converter.convertSnippet("public void method() {}").toOption.get)
  }
  
  @Test
  def `convert variable declaration and keep wrapper` {
    assertEquals("""class Snippet {

  def snippet() {
    val str: String = null
  }
}""", converter.convertSnippet("String str;", keepWrapper = true).toOption.get)
  }
  
  @Test
  def `convert method and keep wrapper` {
    assertEquals("""class Snippet {

  def method() {
  }
}""", converter.convertSnippet("public void method() {}", keepWrapper = true).toOption.get)
  }
  
  @Test
  def `JavaConversions import remains when we convert code that needs it` {
    assertEquals("""//remove if not needed
import scala.collection.JavaConversions._

val strings = java.util.Arrays.asList("a", "b")
for (str <- strings if str.startsWith("b")) {
  println(str)
}""", converter.convertSnippet("""List<String> strings = java.util.Arrays.asList("a", "b");
for (String str : strings) {
    if (str.startsWith("b")) {
        System.out.println(str);
    }
}""").toOption.get)
  }
  
  @Test
  def `static field has warning that it's static because we didn't try to wrap it in a method (workaround for scalagen/javaparser bug)` {
    assertEquals("""//***** Static fields/methods below, these should go in a companion object *****
var a: Int = _
//***** End of static fields/methods *****""", converter.convertSnippet("static int a;").toOption.get)
  }

}