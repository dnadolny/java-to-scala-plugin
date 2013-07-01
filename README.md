Java to Scala plugin (Scala IDE)
==================================

This is a plugin for the Scala IDE for eclipse that lets you convert Java to Scala.

To use it, copy the Java code to your clipboard, open a Scala editor, and press Ctrl+Shift+V (or right click and select "Paste (convert Java to Scala)").

![Screenshot](https://github.com/dnadolny/java-to-scala-plugin/raw/master/website/screenshot.png "Screenshot of Scala editor context menu")

## Installation (Eclipse Update Site)

In Eclipse go to Help > Install New Software, then add the appropriate update site below. You need to have the [Scala IDE](http://scala-ide.org) installed already.
* Scala 2.9 (any Eclipse version) - http://dnadolny.github.io/java-to-scala-update-site/generic-scala29/
* Scala 2.10 (any Eclipse version) - http://dnadolny.github.io/java-to-scala-update-site/generic-scala210/

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
* Passing an array to a method that is expecting varargs is valid in Java but must be indicated explicitly in Scala (by adding the type annotation `: _*`)
* If a generic parameter is expected but not supplied (i.e. using raw types), Java gives a warning but Scala will give an error.
* Some operations are allowed in Java but not in Scala. For example, `int a = 0; double d = 3.9; a += d;` is valid Java but the equivalent code is not valid in Scala.
* Any field in a class converted to Scala but still called from Java will not compile, since Scala exposes fields as a getter and possibly setter method. Java code calling the converted Scala code will need to change field access `obj.someField` to the method `obj.someField()`, and assignment `obj.someField = 3` to `obj.someField_$eq(3)`. Alternately, annotate the field with `@BeanProperty` and Scala will generate Java style getters and setters.
* If the same identifier is used in 2 or more of the following places you will probably have problems: as a field name, as a constructor argument name, as a zero-arg method name.
* **Very bad**: Calling a generic varargs method by passing an array will compile but not do what you want at runtime. This is sinister enough that it deserves an example. In Java:

```java
private static <T> void printIt(T... ts) {
    for (T t : ts) {
        System.out.println("one t: " + t);
    }
}
String[] args = {"a", "b", "c"};
printIt(args);
```
The result is "one t: a", "one t: b", "one t: c" (on separate lines). When we convert that to Scala we get:

```scala
private def printIt[T](ts: T*) {
  for (t <- ts) {
    println("one t: " + t)
  }
}
val args = Array("a", "b", "c")
printIt(args)
```
The result this time: "one t: [Ljava.lang.String;@2ce908"
This is a different manifestation of the varargs problem described earlier, only it doesn't result in a compilation error. To fix it, you need to add the type annotation as above (`args: _*`)
* Any Java code that did not use the `@Override` annotation when possible (overriding a method from a parent class or implementing a method from an interface) will not have the `override` keyword when converted to Scala. The `override` keyword is required in those cases, and will result in a compile error.
