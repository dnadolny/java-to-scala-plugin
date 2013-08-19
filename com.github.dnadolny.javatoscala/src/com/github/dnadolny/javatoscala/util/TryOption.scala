package com.github.dnadolny.javatoscala.util

object TryOption {
  def tryo[T](toTry: => T): Option[T] =
    try {
      Option(toTry)
    } catch {
      case _: Throwable => None
    }

  def trye[T](toTry: => T): Either[Throwable, T] =
    try {
      Right(toTry)
    } catch {
      case e: Throwable => Left(e)
    }
}