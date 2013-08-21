package com.github.dnadolny.javatoscala.convertsnippet

import org.junit._
import org.junit.Assert._

class SnippetClassifierUTest {
  import SnippetClassifier.classify
  
  @Test
  def `top level snippet` {
    assertEquals(TopLevel, classify("class SomeClass {}"))
    assertEquals(TopLevel, classify("//some comment\n/*multi line comment*/\nclass SomeClass {}"))
  }
  
  @Test
  def `contents of class` {
    assertEquals(ContentsOfClass, classify("public static void main(String[] args) {}"))
    assertEquals(ContentsOfClass, classify("""public String getSomeString() {\nreturn "asdf";\n}"""))
  }
  
  @Test
  def `contents of method` {
    assertEquals(ContentsOfMethod, classify("int num = 5;"))
    assertEquals(ContentsOfMethod, classify("Class<?> classy = null;"))
  }
  
  @Test @Ignore("the heuristic fails in this case")
  def `contents of method (heuristic fails because we have "public" and "void")` {
    assertEquals(ContentsOfMethod, classify("""new Thread(new Runnable() {
  public void run() {
    System.out.println("running");
  }
}).start()"""))
  }
}