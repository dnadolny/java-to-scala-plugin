package com.github.dnadolny.javatoscala.convertcu

import org.junit.Test
import org.junit.Assert._

class JavaFileInfoUTest {
  @Test
  def `comments in java code are changed from /* ... */ to |* ... *|` {
    val javaFileInfo = JavaFileInfo("", "", """some code here
/*some comment here*/""")
    assertEquals("""
/*
some code here
|*some comment here*|
*/""", javaFileInfo.contentsAsComment("\n"))

  }
}