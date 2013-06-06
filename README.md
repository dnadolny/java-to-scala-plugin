Java to Scala plugin (Eclipse IDE)
==================================

This is a plugin for the Scala IDE that lets you convert Java to Scala.

To use it, copy the Java code to your clipboard, open a Scala editor, and press Ctrl+Shift+V (or right click and select "Paste (convert Java to Scala)").

## Installation (Eclipse Update Site)

In Eclipse go to Help > Install New Software, then add the appropriate update site below. You need to have the [Scala IDE](http://scala-ide.org) installed already.
* Scala 2.9 and Eclipse 3.7 (Indigo) - http://dnadolny.github.io/java-to-scala-update-site/e37-scala29-3.0/
* Scala 2.9 and Eclipse 4.2 (Juno) - http://dnadolny.github.io/java-to-scala-update-site/e38-scala29-3.0/
* Scala 2.10 and Eclipse 3.7 (Indigo) - http://dnadolny.github.io/java-to-scala-update-site/e37-scala210-3.0/
* Scala 2.10 and Eclipse 4.2 (Juno) - http://dnadolny.github.io/java-to-scala-update-site/e38-scala210-3.0/

## Note

You should treat this as a starting point for the conversion, not the final result. There can be compilation errors in the converted Scala code (for various hard to fix reasons), and there might be actual errors that aren't caught by the compiler.

One current limitation which could come up frequently is that Scalagen (the library that does the conversion) doesn't maintain non-javadoc comments in code. This means you need to manually copy/paste any comments from the original Java to the converted Scala.

## Reporting Bugs

If there are any problems with the converted Scala code, report it at https://github.com/mysema/scalagen/issues

For all other problems, report them to https://github.com/dnadolny/java-to-scala-plugin/issues

## Problems that can't be fixed

There are some problems which are either hard or impossible to fix. Here is a partial list:

* Java allows you to have multiple constructors, each of which could call a different parent class constructor. This cannot be represented in Scala.
* Java allows you to call a static method of a parent class by using name of the subclass. The Scala conversion can't detect that to change it without type information, so you'll get a compile error (which can easily be fixed, by using the name of the class that actually contains the static method rather than the child class).


