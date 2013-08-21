package com.github.dnadolny.javatoscala.util

import scala.util.matching.Regex

object Java7Feature {
  private val features = List(BinaryLiteral, NumberWithUnderscore, DiamondOperator, MultiCatch, TryWithResource)
  
  private implicit def regexMatches(regex: Regex) = new { 
    def matches(str: String) = regex.pattern.matcher(str).matches //TODO: include this in the std lib, in regex?
    def containsMatch(str: String) = regex.findAllIn(str).hasNext
  }
  
  def detectFeatures(src: String): List[Java7Feature] = features.collect{case feature if feature.detectionRegex.containsMatch(src) => feature }
}

sealed abstract class Java7Feature(val description: String, val detectionRegex: Regex)
case object BinaryLiteral extends Java7Feature("Binary literal (eg 0b)", "0b|0B".r)
case object NumberWithUnderscore extends Java7Feature("Number with underscore (eg 123_456)", """\d_\d""".r)
case object DiamondOperator extends Java7Feature("Diamond operator (eg new HashMap<>())", "<>".r)
case object MultiCatch extends Java7Feature("Multi-catch (eg catch (ParseException | IOException e))", """catch \(\w+\s*\|\s*\w+""".r)
case object TryWithResource extends Java7Feature("Try-with-resource (eg try(FileReader fr = ...))", """try\s*\(\s*\w""".r)
