Java to Scala plugin (Eclipse IDE)
==================================

This is a plugin for the Scala IDE that lets you convert Java to Scala.

To use it, copy the Java code to your clipboard, open a Scala editor, and press Ctrl+Shift+V (or right click and select "Paste (convert Java to Scala)").

## Note:

You should treat this as a starting point for the conversion, not the final result. There can be compilation errors in the converted Scala code (for various hard to fix reasons), and there might be actual errors that aren't caught by the compiler.

One current limitation which could come up frequently is that Scalagen (the library that does the conversion) doesn't maintain non-javadoc comments in code. This means you need to manually copy/paste any comments from the original Java to the converted Scala.
