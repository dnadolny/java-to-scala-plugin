package com.github.dnadolny.javatoscala.util

import org.junit.Test
import org.junit.Assert._
import TryOption._

class TryOptionUTest {
  @Test
  def `try option successful becomes Some(value)` {
    assertEquals(Some("abc"), tryo("abc"))
  }

  @Test
  def `try option throwing exception becomes None` {
    assertEquals(None, tryo(throw new Exception))
  }

  @Test
  def `try either successful becomes Right(value)` {
    assertEquals(Right("abc"), trye("abc"))
  }

  @Test
  def `try either throwing exception becomes Left(exception)` {
    val exception = new Exception
    assertEquals(Left(exception), trye(throw exception))
  }
}