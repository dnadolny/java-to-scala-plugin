package com.github.dnadolny.javatoscala.util

import org.junit.Test
import org.junit.Assert._

class Java7FeatureUTest {
  private def detect = Java7Feature.detectFeatures _
  
  @Test
  def `not java 7` {
    assertEquals(Nil, detect("""class SomeClass {
  public void someMethod() {}
}"""))
  }
  
  @Test
  def `various java 7 features` {
    assertEquals(List(BinaryLiteral), detect("int x = 0b;"))
    assertEquals(List(NumberWithUnderscore), detect("int x = 12_34_45;"))
    assertEquals(List(DiamondOperator), detect("HashSet<String> strings = new HashSet<>();"))
    
    assertEquals(List(MultiCatch), detect("""
try {
  //something that throws multiple exceptions
} catch (NullPointerException | OutOfMemoryError e) {}
"""))

    assertEquals(List(MultiCatch), detect("""
try {
  //something that throws multiple exceptions
} catch (NullPointerException |
OutOfMemoryError e) {}
"""))

    assertEquals(List(TryWithResource), detect("try (FileReader fr = new FileReader(path)) { /* ... */ }"))

    assertEquals(List(TryWithResource), detect("""
try (
  FileReader fr = getFr();
  FileWriter fw = getFw()) { /* ... */ }
"""))
  }
}