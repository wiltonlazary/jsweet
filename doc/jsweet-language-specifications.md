JSweet Language Specifications
==============================

Version: 1.2.x (snapshot)

Author: Renaud Pawlak

JSweet JavaDoc API: http://www.jsweet.org/core-api-javadoc/

Note: this markdown is automatically generated from the Latex source file. Do not modify directly.

Content
-------

-   [Basic concepts](#basic-concepts)
    -   [Core types and objects](#core-types-and-objects)
    -   [Classes](#classes)
    -   [Interfaces](#interfaces)
    -   [Untyped objects (maps)](#untyped-objects-maps)
    -   [Enums](#enums)
    -   [Globals](#globals)
    -   [Optional parameters and overloading](#optional-parameters)
-   [Bridging to external JavaScript elements](#bridging-to-external-javascript-elements)
    -   [Ambient declarations](#ambient-declarations)
    -   [Definitions](#definitions)
    -   [Untyped accesses](#untyped-accesses)
    -   [Mixins](#mixins)
-   [Auxiliary types](#auxiliary-types)
    -   [Functional types](#functional-types)
    -   [Object types](#object-types)
    -   [String types](#string-types)
    -   [Tuple types](#tuple-types)
    -   [Union types](#union-types)
    -   [Intersection types](#intersection-types)
-   [Semantics](#semantics)
    -   [Main methods](#main-methods)
    -   [Initializers](#initializers)
    -   [Arrays initialization and allocation](#arrays-initialization-and-allocation)
    -   [Name clashes](#name-clashes)
    -   [Testing the type of an object (`instanceof`)](#testing-the-type-of-an-object-instanceof)
    -   [Variable scoping in lambda expressions](#variable-scoping-in-lambda-expressions)
    -   [Scope of *this*](#scope-of-this)
-   [Packaging](#packaging)
    -   [Use your files without any packaging](#use-your-files-without-any-packaging)
    -   [Creating a bundle for a browser](#creating-a-bundle-for-a-browser)
    -   [Packaging with modules](#packaging-with-modules)
    -   [Root packages](#root-packages)
    -   [Packaging a JSweet jar (candy)](#packaging-a-jsweet-jar-candy)
-   [Appendix 1: JSweet transpiler options](#appendix-1-jsweet-transpiler-options)
-   [Appendix 2: packaging and static behavior](#static_behavior)
    -   [When main methods are invoked](#when-main-methods-are-invoked)
    -   [Static and inheritance dependencies](#static-and-inheritance-dependencies)

Basic concepts
--------------

This section presents the JSweet language basic concepts. One must keep in mind that JSweet, as a Java-to-JavaScript transpiler, is an extension of Java at compile-time, and executes as JavaScript at runtime. Thus, most Java typing and syntactic constraints will apply at compile time, but some JavaScript semantics may apply at runtime. This document will mention JSweet-specific semantics.

### Core types and objects

JSweet allows the use of primitive Java types, core Java objects and of core JavaScript objects, which are defined in the `jsweet.lang` package. Next, we describe the use of such core types and objects.

#### Primitive Java types

JSweet allows the use of Java primitive types (and associated literals).

-   `int`, `byte`, `short`, `double`, `float` are all converted to JavaScript numbers (TypeScript `number` type). Precision usually does not matter in JSweet, however, casting to `int`, `byte`, or `short` forces the number to be rounded to the right-length integer.

-   `char` follows the Java typing rules but is converted to a JavaScript `string` by the transpiler.

-   `boolean` corresponds to the JavaScript `boolean`.

-   `java.lang.String` corresponds to the JavaScript `string`. (not per say a primitive type, but is immutable and used as the class of string literals in Java)

A direct consequence of that conversion is that it is not possible in JSweet to safely overload methods with numbers or chars/strings. For instance, the methods `pow(int, int)` and `pow(double, double)` will be considered as the same method at runtime and should not have different implementations for that reason. Also, there will be no difference between `n instanceof Integer` and `n instanceof Double`, because it both means `typeof n === ’number’`. These behavior ensure low impedance between JSweet programs and JavaScript ones.

Examples of valid statements:

``` java
// warning '==' behaves like JavaScript '===' at runtime
int i = 2;
assert i == 2;
double d = i + 4;
assert d == 6;
String s = "string" + '0' + i;
assert s == "string02";
boolean b = false;
assert !b;
```

Note that since JSweet 1.1.0, the `==` operator behaves like the JavaScript strict equals operator `===` so that it is close to the Java semantics. Similarly, `!=` is mapped to `!==`. There is an exception to that behavior which is when comparing an object to a `null` literal. In that case, JSweet translates to the loose equality operators so that the programmers see no distinction between `null` and `undefined` (which are different in JavaScript but it may be confusing to Java programmers). To control whether JSweet generates strict or loose operators, you can use the following helper methods: `jsweet.util.Globals.equalsStrict` (`===`), `jsweet.util.Globals.notEqualsStrict` (`!==`), `jsweet.util.Globals.equalsLoose` (`==`), and `jsweet.util.Globals.notEqualsLoose` (`!=`). For example:

``` java
import static jsweet.util.Globals.equalsLoose;
[...]
int i = 2;
assert i == 2; // generates i === 2
assert !((Object)"2" == i);
assert equalsLoose("2", i); // generates "2" == i
```

#### Allowed Java objects

By default, JSweet maps core Java objects and methods to JavaScript through the use of built-in macros. It means that the Java code is directly substituted with a valid JavaScript code that implements similar behavior. Here is the list of accepted Java core classes and methods in a JSweet program:

-   `java.lang.Object`

    -   allowed methods: `boolean equals()`

    -   allowed methods: `String toString()`

-   `java.lang.CharSequence`

    -   allowed methods:

        -   `char charAt(int index)`

        -   `int length()` (transpiles to `length`)

        -   `CharSequence subSequence(int beginIndex, int endIndex)`

        -   `String toString()`

-   `java.lang.String`

    -   allowed constructors:

        -   `String()`

        -   `String(byte[] bytes)`

        -   `String(byte[] bytes, int offset, int length)`

        -   `String(char[] value)`

        -   `String(char[] value, int offset, int count)`

    -   allowed methods:

        -   `char charAt(int index)`

        -   `int codePointAt(int index)`

        -   `int compareTo(String anotherString)`

        -   `int compareToIgnoreCase(String str)`

        -   `String concat(String str)`

        -   `boolean equals(String anotherString)`

        -   `boolean equalsIgnoreCase(String anotherString)`

        -   `byte[] getBytes()`

        -   `int indexOf(int ch)`

        -   `int indexOf(String str)`

        -   `boolean isEmpty()`

        -   `int lastIndexOf(int ch)`

        -   `int lastIndexOf(int ch, int fromIndex)`

        -   `int lastIndexOf(String str)`

        -   `int lastIndexOf(String str, int fromIndex)`

        -   `int length()` (transpiles to `length`)

        -   `String replace(CharSequence target, CharSequence replacement)`

        -   `CharSequence subSequence(int beginIndex, int endIndex)`

        -   `String substring(int beginIndex)`

        -   `String substring(int beginIndex, int endIndex)` (with the JavaScript behavior)

        -   `String[] split(String regex)`

        -   `boolean startsWith(String prefix)`

        -   `boolean startsWith(String prefix, int toffset)`

        -   `char[] toCharArray()`

        -   `String toLowerCase()`

        -   `String toString()`

        -   `String toUpperCase()`

        -   `String trim()`

        -   `static String valueOf(boolean b)`

        -   `static String valueOf(char c)`

        -   `static String valueOf(char[] data)`

        -   `static String valueOf(char[] data, int offset, int count)`

        -   `static String valueOf(double d)`

        -   `static String valueOf(float f)`

        -   `static String valueOf(int i)`

        -   `static String valueOf(long l)`

        -   `static String valueOf(Object obj)`

-   `java.lang.Class`

    -   allowed methods:

        -   `String getName()`: only on class literals

        -   `String getSimpleName()`: only on class literals

-   `java.lang.Boolean`

    -   allowed constructors:

        -   `Boolean(value value)`

        -   `Boolean(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `String toString()`

-   `java.lang.Void`

    -   allowed methods: none

-   `java.lang.Integer`

    -   allowed constructors:

        -   `Integer(int value)`

        -   `Integer(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `String toString()`

-   `java.lang.Long`

    -   allowed constructors:

        -   `Long(long value)`

        -   `Long(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `String toString()`

-   `java.lang.Double`

    -   allowed constructors:

        -   `Double(double value)`

        -   `Double(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `boolean isInfinite()`

        -   `boolean isNaN()`

        -   `String toString()`

-   `java.lang.Number`

    -   allowed methods: none

-   `java.lang.Float`

    -   allowed constructors:

        -   `Float(float value)`

        -   `Float(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `boolean isInfinite()`

        -   `boolean isNaN()`

        -   `String toString()`

-   `java.lang.Byte`

    -   allowed constructors:

        -   `Byte(byte value)`

        -   `Byte(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `String toString()`

-   `java.lang.Short`

    -   allowed constructors:

        -   `Short(short value)`

        -   `Short(String s)`

    -   allowed methods:

        -   `boolean equals()`

        -   `int hashCode()`

        -   `String toString()`

-   `java.lang.Iterable`

    -   allowed methods: none (for using the *foreach* loop on indexed objects)

-   `java.lang.Runnable`

    -   allowed methods: none (for declaring lambdas)

-   `java.lang.Throwable` and all sub-classes

    -   allowed methods:

        -   `getMessage()`

        -   `getCause()`: valid but always return `null` by default

-   `java.lang.Math`

    -   allowed fields:

        -   `static double E`

        -   `static double PI`

    -   allowed methods:

        -   `static double abs(double a)`

        -   `static float abs(float a)`

        -   `static int abs(int a)`

        -   `static long abs(long a)`

        -   `static double acos(double a)`

        -   `static double asin(double a)`

        -   `static double atan(double a)`

        -   `static double atan2(double y, double x)`

        -   `static double cbrt(double a)`

        -   `static double ceil(double a)`

        -   `static double copySign(double magnitude, double sign)`

        -   `static double cos(double a)`

        -   `static double cosh(double x)`

        -   `static double exp(double a)`

        -   `static double expm1(double x)`

        -   `static double floor(double a)`

        -   `static double hypot(double x, double y)`

        -   `static double log(double a)`

        -   `static double log10(double a)`

        -   `static double log1p(double a)`

        -   `static double max(double a, double b)`

        -   `static float max(float a, float b)`

        -   `static int max(int a, int b)`

        -   `static long max(long a, long b)`

        -   `static double min(double a, double b)`

        -   `static float min(float a, float b)`

        -   `static int min(int a, int b)`

        -   `static long min(long a, long b)`

        -   `static double pow(double a, double b)`

        -   `static double random()`

        -   `static double rint(double a)`

        -   `static long round(double a)`

        -   `static int round(float a)`

        -   `static double scalb(double d, int scaleFactor)`

        -   `static float scalb(float f, int scaleFactor)`

        -   `static double signum(double d)`

        -   `static float signum(float f)`

        -   `static double sin(double a)`

        -   `static double sinh(double x)`

        -   `static double sqrt(double a)`

        -   `static double tan(double a)`

        -   `static double tanh(double x)`

        -   `static double toDegrees(double angrad)`

        -   `static double toRadians(double angdeg)`

-   `java.util.function.*` (for declaring lambdas)

    -   prohibited method names:

        -   `and`

        -   `negate`

        -   `or`

        -   `andThen`

Examples of valid statements:

``` java
Integer i = 2;
assert i == 2;
Double d = i + 4d;
assert d.toString() == "6";
assert !((Object) d == "6");
BiFunction<String, Integer, String> f = (s, i) -> { return s.substring(i); };
assert "bc" == f.apply("abc", 1);
```

#### Getting more Java APIs

With JSweet, it is possible to add a runtime that implements Java APIs in JavaScript, so that programmers can access more Java APIs and thus share the same code between Java and JavaScript. The core project for implementing Java APIs for JSweet is J4TS (<https://github.com/cincheo/j4ts>) and contains a quite complete implementation of `java.util.*` classes and other core package. J4TS is based on a fork of the GWT’s JRE emulation, but it is adapted to be compiled with JSweet. Programmers can use J4TS as a regular JavaScript library available in our Maven repository.

Although J4TS cannot directly implement the Java core types that conflict with JavaScript ones (`Boolean`, `Byte`, `Short`, `Integer`, `Long`, `Float`, `Double`, `Character`, `String`), J4TS contributes to supporting the static part of them by providing helpers for each class (`javaemul.internal.BooleanHelper`, `javaemul.internal.ByteHelper`, ...). When the JSweet transpiler meets a static Java method on a type `java.lang.T` that is not supported as a built-in macro, it delegates to `javaemul.internal.THelper`, which can provide a JavaScript implementation for the given static method. That way, by using J4TS, programmers can use even more of the core JRE API.

#### Java arrays

Arrays can be used in JSweet and are transpiled to JavaScript arrays. Array initialization, accesses and and iteration are all valid statements.

``` java
int[] arrayOfInts = { 1, 2, 3, 4};
assert arrayOfInts.length == 4;  
assert arrayOfInts[0] == 1;
int i = 0;
for (int intItem : arrayOfInts) { 
    assert arrayOfInts[i++] == intItem;
}
```

#### Core JavaScript API

The core JavaScript API is defined in `jsweet.lang` (the full documentation can be found at <http://www.jsweet.org/core-api-javadoc/>). Main JavaScript classes are:

-   `jsweet.lang.Object`: JavaScript Object class. Common ancestor for JavaScript objects functions and properties.

-   `jsweet.lang.Boolean`: JavaScript Boolean class. A wrapper for boolean values.

-   `jsweet.lang.Number`: JavaScript Number class. A wrapper for numerical values.

-   `jsweet.lang.String`: JavaScript String class. A wrapper and constructor for strings.

-   `jsweet.lang.Function`: JavaScript Function class. A constructor for functions.

-   `jsweet.lang.Date`: JavaScript Date class, which enables basic storage and retrieval of dates and times.

-   `jsweet.lang.Array<T>`: JavaScript Array class. It is used in the construction of arrays, which are high-level, list-like objects.

-   `jsweet.lang.Error`: JavaScript Error class. This class implements `java.lang.RuntimeException` and can be thrown and caught with `try` ... `catch` statements.

When using JavaScript frameworks, programmers should use this API most of the time, which is HTML5 compatible and follows the JavaScript latest supported versions. However, for objects that need to be used with Java literals (numbers, booleans, and strings), the use of the `java.lang` package classes is recommended. For instance, the jQuery API declares `$(java.lang.String)` instead of `$(jsweet.lang.String)`. This allows the programmer to write expressions using literals, such as `$("a")` (for selecting all links in a document).

As a consequence, programmers need to be able to switch to the JavaScript API when coming from a Java object. The `jsweet.util.Globals` class defines convenient static methods to cast back and forth core Java objects to their corresponding JSweet objects. For instance the `string(...)` method will allow the programmer to switch from the Java to the JSweet strings and conversely.

``` java
import static jsweet.util.Globals.string;
String str = "This is a test string"; // str is actually a JavaScript string at runtime
str.toLowerCase(); // valid: toLowerCase it defined both in Java and JavaScript
str.substr(1); // this is ok, but it is a macro that will generate JavaScript code
string(str).substr(1); // direct call to the substr method on the JavaScript string
```

Note: for code sharing between a JavaScript client and a Java server for instance, it is better to use Java APIs only and not JavaScript ones.

Here is another example that shows the use of the `array` method to access the `push` method available on JavaScript arrays.

``` java
import static jsweet.util.Globals.array;
String[] strings = { "a", "b", "c" };
array(strings).push("d");
assert strings[3] == "d";
```

### Classes

Classes in JSweet fully support all types of Java classes declarations. For example:

``` java
public class BankAccount {  
    public double balance = 0;  
    public double deposit(double credit) {  
        balance += credit;  
        return this.balance;  
    }  
}  
```

Which is transpiled to the following JavaScript code:

``` java
var BankAccount = (function () {  
    function BankAccount() {  
        this.balance = 0;  
    }  
    BankAccount.prototype.deposit = function(credit) {  
        this.balance += credit;  
        return this.balance;  
    };  
    return BankAccount;  
})();
```

Classes can define constructors, have super classes and be instantiated exactly like in Java. Similarly to Java, inner classes and anonymous classes are allowed in JSweet since version 1.1.0. JSweet supports both static and regular inner/anonymous classes, which can share state with enclosing classes. Still like in Java, anonymous classes can access final variables declared in their scope. For example, the following declarations are valid in JSweet and will mimic the Java semantics at runtime so that Java programmers can benefit all the features of the Java language.

``` java
abstract class C {
    public abstract int m();
}
public class ContainerClass {
    // inner class
    public class InnerClass {
        public I aMethod(final int i) {
            // anonymous class
            return new C() {
                @Override
                public int m() {
                    // access to final variable i
                    return i;
                }
            }
        }    
    }
}
```

### Interfaces

In JSweet, an interface (a.k.a. object type) can be seen as object signature, that is to say the accessible functions and properties of an object (without specifying any implementation). An interface is defined for typing only and has no runtime representation (no instances), however, they can be used to type objects.

JSweet interfaces can be defined as regular Java interfaces, but also as Java classes annotated with `@jsweet.lang.Interface`, so that is is possible to define properties as fields. Such classes impose many constraints, as shown in the following code.

``` java
@Interface
public class WrongConstructsInInterfaces {
    native public void m1(); // OK
    // error: field initializers are not allowed
    public long l = 4;
    // error: statics are not allowed
    static String s1;
    // error: private are not allowed
    private String s2;
    // error: constructors are not allowed
    public WrongConstructsInInterfaces() {
        l = 4;
    }
    // error: bodies are not allowed
    public void m2() {
        l = 4;
    }
    // error: statics are not allowed
    native static void m3();
    // error: initializers are not allowed
    {
        l = 4;
    }
    // error: static initializers are not allowed
    static {
        s1 = "";
    }
}
```

#### Object typing

In JSweet, typed objects can be constructed out of interfaces. If we take the following interface:

``` java
@Interface
public class Point {
    public double x;
    public double y;
}
```

We can create an object typed after the interface. Note that the following code is not actually creating an instance of the `Point` interface, it is creating an object that conforms to the interface.

``` java
Point p1 = new Point() {{ x=1; y=1; }};
```

This object creation mechanism is a TypeScript/JavaScript mechanism and shall not be confused with anonymous classes, which is a Java-like construction.

Note also that, for each object, JSweet keeps track of which interface it was created from and of all the potential interfaces implemented by its class. This interface tracking system is implemented as a special object property called `__interfaces`. Using that property, JSweet allows the use of the `instanceof` operator on interfaces, exactly like in Java, as we will see later in this document.

#### Optional fields

Interfaces can define *optional fields*, which are used to report errors when the programmer forgets to initialize a mandatory field in an object. Supporting optional fields in JSweet is done through the use of `@jsweet.lang.Optional` annotations. For instance:

``` java
@Interface
public class Point {
    public double x;
    public double y;
    @Optional
    public double z = 0;
}
```

It is the JSweet compiler that will check that the fields are correctly initialized, when constructing an object from an interface.

``` java
// no errors (z is optional)
Point p1 = new Point() {{ x=1; y=1; }};
// JSweet reports a compile error since y is not optional
Point p2 = new Point() {{ x=1; z=1; }};
```

#### Special functions in interfaces

In JavaScript, objects can have properties and functions, but can also (not exclusively), be used as constructors and functions themselves. This is not possible in Java, so JSweet defines special functions for handling these cases.

-   `apply` is used to state that the object can be used as a function.

-   `$new` is used to state that the object can be used as a constructor.

### Untyped objects (maps)

In JavaScript, object can be seen as maps containing key-value pairs (key is often called *index*, especially when it is a number). So, in JSweet, all objects define the special functions (defined on `jsweet.lang.Object`):

-   `$get(key)` accesses a value with the given key.

-   `$set(key,value)` sets or replace a value for the given key.

-   `$delete(key)` deletes the value for the given key.

#### Reflective/untyped accesses

The functions `$get(key)`, `$set(key,value)` and `$delete(key)` can be seen as a simple reflective API to access object fields and state. Note also the static method `jsweet.lang.Object.keys(object)`, which returns all the keys defined on a given object.

The following code uses this API to introspect the state of an object `o`.

``` java
for(String key : jsweet.lang.Object.keys(o)) {
  console.log("key=" + key +  " value=" + o.$get(key));
});
```

When not having the typed API of a given object, this API can be useful to manipulate the object in an untyped way (of course it should be avoided as much as possible).

#### Untyped objects initialization

One can use the `$set(key,value)` function to create new untyped object. For instance:

``` java
Object point = new jsweet.lang.Object() {{ $set("x", 1); $set("y", 1); }};
```

As a shortcut, one can use the `jsweet.util.Global.$map` function:

``` java
import static jsweet.util.Global.$map;
[...]
Object point = $map("x", 1, "y", 1);
```

#### Indexed objects

The type of keys and values can be overloaded for every object. For example, the `Array<T>` class, will define keys as numbers and values as objects conforming to type `T`.

In the case of objects indexed with number keys, it is allowed to implement the `java.lang.Iterable` interface so that it is possible to use they in *foreach* loops. For instance, the `NodeList` type (from the DOM) defines an indexed function:

``` java
@Interface
class NodeList implements java.lang.Iterable {
    public double length;
    public Node item(double index);
    public Node $get(double index);
}
```

In JSweet, you can access the node list elements with the `$get` function, and you can also iterate with the *foreach* syntax. The following code generates fully valid JavaScript code.

``` java
NodeList nodes = ...
for (int i = 0; i < nodes.length; i++) {
    HTMLElement element = (HTMLElement) nodes.$get(i);
    [...]
}
// same as:
NodeList nodes = ...
for (Node node : nodes) {
    HTMLElement element = (HTMLElement) node;
    [...]
}
```

### Enums

JSweet allows the definition of enums similarly to Java, but with some restrictions. The following code declares an enum with tree possible values (`A`, `B`, and `C`).

``` java
enum MyEnum {
    A, B, C
}
```

The following statements are valid statements in JSweet.

``` java
MyEnum e = MyEnum.A;
assert MyEnum.A == e;
assert e.name() == "A";
assert e.ordinal() == 0;
assert MyEnum.valueOf("A") == e;
assert array(MyEnum.values()).indexOf(MyEnum.valueOf("C")) == 2;
```

On the other hand, unlike Java enums, other members than constants are not allowed, raising JSweet transpilation errors, as shown in the following code.

``` java
public enum WrongConstructsInEnums {

    A, B, C;
    
    // error: fields are not allowed
    public long l = 4;
    // error: constructors are not allowed  
    private WrongConstructsInEnums() {
        l = 4;
    }
    // error: methods are not allowed
    public void m2() {
        l = 4;
    }
    // error: initializers are not allowed
    {
        l = 4;
    }
}
```

These constraints will be relaxed in future releases.

### Globals

In Java, on contrary to JavaScript, there is no such thing as global variables or functions (there are only static members, but even those must belong to a class). Thus, JSweet introduces reserved `Globals` classes and `globals` packages. These have two purposes:

-   Generate code that has global variables and functions (this is discouraged in Java)

-   Bind to existing JavaScript code that defines global variables and functions (as many JavaScript frameworks do)

In Globals classes, only static fields (global variables) and static methods (global functions) are allowed. Here are the main constraints applying to Globals classes:

-   no non-static members

-   no super class

-   cannot be extended

-   cannot be used as types like regular classes

-   no public constructor (empty private constructor is OK)

-   cannot use $get, $set and $delete within the methods

For instance, the following code snippets will raise transpilation errors.

``` java
class Globals {
    public int a;
    // error: public constructors are not allowed
    public Globals() {
        this.a = 3;
    }
    public static void test() {
        // error: no instance is available
        $delete("key");
    }
}
```

``` java
// error: Globals classes cannot be used as types
Globals myVariable = null;
```

One must remember that `Globals` classes and `global` packages are erased at runtime so that their members will be directly accessible. For instance `mypackage.Globals.m()` in a JSweet program corresponds to the `mypackage.m()` function in the generated code and in the JavaScript VM at runtime. Also, `mypackage.globals.Globals.m()` corresponds to *m()*.

In order to erase packages in the generated code, programmers can also use the `@Root` annotation, which will be explained in Section \[packaging\].

### Optional parameters and overloading

In JavaScript, parameters can be optional, in the sense that a parameter value does not need to be provided when calling a function. Except for varargs, which are fully supported in JSweet, the general concept of an optional parameter does not exist in Java. To simulate optional parameters, JSweet programmers can use method overloading, which is supported in Java. Here are some examples of supported overloads in JSweet:

``` java
String m(String s, double n) { return s + n; }
// simple overloading (JSweet transpiles to optional parameter)
String m(String s) { return m(s, 0); }
// complex overloading (JSweet generates more complex code to mimic the Java behavior)
String m(String s) { return s; }
```

Bridging to external JavaScript elements
----------------------------------------

It can be the case that programmers need to use existing libraries from JSweet. In most cases, one should look up in the available candies (<http://www.jsweet.org/candies-releases/> and <http://www.jsweet.org/candies-snapshots/>), which are automatically generated from TypeScript’s DefinitelyTyped. When the candy does not exist, or does not entirely cover what is needed, one can use the `@jsweet.lang.Ambient` annotation, which will make available to the programmers a class definition or an interface.

### Ambient declarations

The following example shows the backbone store class made accessible to the JSweet programmer with a simple ambient declaration. This class is only for typing and will be erased during the JavaScript generation.

``` java
@Ambient
class Store {
    public Store(String dbName) {}
}
```

Note that ambient classes constructors must have an empty body. Also, ambient classes methods must be `abstract` or `native`. For instance:

``` java
@Ambient
class MyExternalJavaScriptClass {
    public native myExternalJavaScriptMethod();
}
```

### Definitions

By convention, putting the classes in a `def.libname` package defines a set of definitions for the `libname` external JavaScript library called `libname`. Definitions are by default all ambient declarations and do not need to be annotated with `@jsweet.lang.Ambient` annotations since they are implicit in `def.*` packages and sub-packages. Note that this mechanism is similar to the TypeScript `d.ts` definition files.

Candies (bridges to external JavaScript libraries) use definitions. For instance, the jQuery candy defines all the jQuery API in the `def.jquery` package.

Here is a list of rules and constraints that need to be followed when writing definitions.

-   The `def.libname` package must be annotated with a `@jsweet.lang.Root` (to be placed in a `package-info.java` file).

-   Within a `def.*` package, `@Ambient` annotations are not required. By conventions all declarations are ambient.

-   Interfaces are preferred over classes, because interfaces can be merged and classes can be instantiated. Classes should be used only if the API defines an explicit constructor (objects can be created with `new`). To define an interface in JSweet, just annotate a class with `@jsweet.lang.Interface`.

-   Top-level functions and variables must be defined as `public static` members in a `Globals` class.

-   All classes, interfaces and packages, should be documented with comments following the Javadoc standard.

-   When several types are possible for a function parameter, method overloading should be preferred over using union types. When method overloading is not possible, it can be more convenient to simply use the `Object` type. It is less strongly typed but it is easier to use.

-   One can use string types to provide function overloading depending on a string parameter value.

-   In a method signature, optional parameters can be defined with the `@jsweet.lang.Optional` annotation.

-   In an interface, optional fields can be defined with the `@jsweet.lang.Optional` annotation.

Definitions can be embedded directly in a JSweet project to access an external library in a typed way, of they can be packaged in a candy (a Maven artifact), so that they can be shared by other projects. See the *Packaging* section for full details on how to create a candy. Note that you do not need to write definitions when a library is written with JSweet because the Java API is directly accessible and the TypeScript definitions can be automatically generated by JSweet using the `declaration` option.

### Untyped accesses

Sometimes, definitions are not available or are not correct, and just a small patch is required to access a functionality. Programmers have to keep in mind that JSweet is just a syntactic layer and that it is always possible to bypass typing in order to access a field or a function that is not explicitly specified in an API.

Although, having a well-typed API is the preferred and advised way, when such an API is not available, the use of `jsweet.lang.Object.$get` allows reflective access to methods and properties that can then be cast to the right type. For accessing functions in an untyped way, one can cast to `jsweet.lang.Function` and call the generic and untyped method `apply` on it. For example, here is how to invoke the jQuery `$` method when the jQuery API is not available :

``` java
import jsweet.dom.Globals.window;
[...]
Function $ = (Function)window.$get("$");
$.apply("aCssSelector"):
```

The `$get` function is available on instances of `jsweet.lang.Object` (or subclasses). For a `java.lang.Object`, you can cast it using the `jsweet.util.Globals.object` helper method. For example:

``` java
import static jsweet.dom.Globals.object;
[...]
object(anyObject).$get("$");
```

The other way it to use the `jsweet.util.Globals.$get` helper method. Using helper methods can be convenient to easily write typical (untyped JavaScript statement). For example:

``` java
import static jsweet.dom.Globals.$get;
import static jsweet.dom.Globals.$apply;
[...]
// generate anyObject["prop"]("param"); 
$apply($get(anyObject, "prop"), "param"); 
```

Finally, note also the use of the `jsweet.util.Globals.any` helper method, which can be extremely useful to erase typing. Since the `any` method generates a cast to the `any` type in TypeScript, it is more radical than a cast to `Object` for instance. The following example shows how to use the `any` method to cast an `Int32Array` to a Java `int[]` (and then allow direct indexed accesses to it.

``` java
ArrayBuffer arb = new ArrayBuffer(2 * 2 * 4);
int[] array = any(new Int32Array(arb));
int whatever = array[0];
```

### Mixins

In JavaScript, it is common practice to enhance an existing class with news elements (field and methods). It is an extension mechanism used when a framework defines plugins for instance. Typically, jQuery plugins add new elements to the `JQuery` class. For example the jQuery timer plugin adds a `timer` field to the `JQuery` class. As a consequence, the `JQuery` class does not have the same prototype if you are using jQuery alone, or jQuery enhanced with its timer plugin.

In Java, this extension mechanism is problematic because Java cannot dynamically enhance a given class.

#### Untyped accesses to mixins

Programmers can access the added element with `$get` accessors and/or with brute-force casting.

Here is an example using `$get` for the timer plugin case:

``` java
((Timer)$("#myId").$get("timer")).pause();
```

Here is an other way to do it exampled through the use of the jQuery UI plugin (note that this solution forces the use of `def.jqueryui.JQuery` instead of `def.jquery.JQuery` in order to access the `menu()` function, added by the UI plugin):

``` java
import def.jqueryui.JQuery;
[...]
Object obj = $("#myMenu");
JQuery jq = (JQuery) obj;
jq.menu();
```

However, these solutions are not satisfying because clearly unsafe in terms of typing.

#### Typed accesses with mixins

When cross-candy dynamic extension is needed, JSweet defines the notion of a mixin. A mixin is a class that defines members that will end up being directly accessible within a target class (mixin-ed class). Mixins are defined with a `@Mixin` annotation. Here is the excerpt of the `def.jqueryui.JQuery` mixin:

``` java
package def.jqueryui;
import jsweet.dom.MouseEvent;
import jsweet.lang.Function;
import jsweet.lang.Date;
import jsweet.lang.Array;
import jsweet.lang.RegExp;
import jsweet.dom.Element;
import def.jquery.JQueryEventObject;
@jsweet.lang.Interface
@jsweet.lang.Mixin(target=def.jquery.JQuery.class)
public abstract class JQuery extends jsweet.lang.Object {
    native public JQuery accordion();
    native public void accordion(jsweet.util.StringTypes.destroy methodName);
    native public void accordion(jsweet.util.StringTypes.disable methodName);
    native public void accordion(jsweet.util.StringTypes.enable methodName);
    native public void accordion(jsweet.util.StringTypes.refresh methodName);
    ...
    native public def.jqueryui.JQuery menu();
    ...
```

One can notice the `@jsweet.lang.Mixin(target=def.jquery.JQuery.class)` that states that this mixin will be merged to the `def.jquery.JQuery` so that users will be able to use all the UI plugin members directly and in a well-typed way.

#### Implementation and how to use

JSweet merges mixins using a bytecode manipulation tool called Javassist. It takes the mixin classes bytecode, copies all the members to the target classes, and writes the resulting merged classes bytecode to the `.jsweet/candies/processed` directory. As a consequence, in order to benefit the JSweet mixin mechanism, one must add the `.jsweet/candies/processed` directory to the compilation classpath. This directory should be placed before all the other classpath elements so that the mixined results override the original classes (for example the `def.jquery.JQuery` should be overridden and, as a consequence, `.jsweet/candies/processed/def/jquery/JQuery.class` must be found first in the classpath).

The JSweet transpiler automatically adds the `.jsweet/candies/processed` directory to the compilation classpath so that you do not have to do anything special when using JSweet with Maven. However, when using mixins within an IDE, you must force your project classpath to include this directory in order to ensure compilation of mixin-ed elements. When using the JSweet Eclipse plugin for instance, this is done automatically and transparently for the user. But when not using any plugins, this configuration must be done manually.

For example, with Eclipse (similar configuration can be made with other IDEs):

1.  Right-click on the project &gt;Build path &gt;Configure build path... &gt;Libraries (tab) &gt;Add class folder (button). Then choose the `.jsweet/candies/processed` directory.

2.  In the “order and export” tab of the build path dialog, make sure that the `.jsweet/candies/processed` directory appears at the top of the list (or at least before the Maven dependencies).

NOTE: you do not have to configure anything if you are not using mixins or if you are using the Eclipse plugin.

Once this configuration is done, you can safely use mixins. For instance, if using the jQuery candy along with jQuery UI, you will be able to write statements such as:

``` java
$("#myMenu").menu();
```

This is neat compared to the untyped access solution because it is checked by the Java compiler (and you will also have completion on mixin-ed elements).

Auxiliary types
---------------

JSweet uses most Java typing features (including functional types) but also extends the Java type system with so-called *auxiliary types*. The idea behind auxiliary types is to create classes or interfaces that can hold the typing information through the use of type parameters (a.k.a *generics*), so that the JSweet transpiler can cover more typing scenarios. These types have been mapped from TypeScript type system, which is much richer than the Java one (mostly because JavaScript is a dynamic language and requires more typing scenarios than Java).

### Functional types

For functional types, JSweet reuses the `java.Runnable` and `java.util.function` functional interfaces of Java 8. These interfaces are generic but only support up to 2-parameter functions. Thus, JSweet adds some support for more parameters in `jsweet.util.function`, since it is a common case in JavaScript APIs.

Here is an example using the `Function` generic functional type:

``` java
import java.util.function.Function;

public class C {

    String test(Function<String, String> f) {
        f.apply("a");
    }

    public static void main(String[] args) {
        String s = new C().test(p -> p);
        assert s == "a";
    }
}
```

We encourage programmers to use the generic functional interfaces defined in the `jsweet.util.function` and `java.util.function` (besides `java.lang.Runnable`). When requiring functions with more parameters, programmers can define their own generic functional types in `jsweet.util.function` by following the same template as the existing ones.

In some cases, programmers will prefer defining their own specific functional interfaces. This is supported by JSweet. For example:

``` java
@FunctionalInterface
interface MyFunction {
    void run(int i, String s);
}

public class C {
    void m(MyFunction f) {
        f.run(1, "test");
    }
    public static void main(String[] args) {
        new C().m((i, s) -> {
            // do something with i and s
        });
    }
}
```

Important warning: it is to be noted here that, on contrary to Java, the use of the `@FunctionInterface` annotation is mandatory.

Note also the possible use of the `apply` function, which is by convention always a functional definition on the target object (unless if `apply` is annotated with the `@Name` annotation). Defining/invoking `apply` can done on any class/object (because in JavaScript any object can become a functional object).

### Object types

Object types are similar to interfaces: they define a set of fields and methods that are applicable to an object (but remember that it is a compile-time contract). In TypeScript, object types are inlined and anonymous. For instance, in TypeScript, the following method `m` takes a parameter, which is an object containing an `index` field:

``` java
// TypeScript:
public class C {
    public m(param : { index : number }) { ... }
}
```

Object types are a convenient way to write shorter code. One can pass an object that is correctly typed by constructing an object on the fly:

``` java
// TypeScript:
var c : C = ...;
c.m({ index : 2 });
```

Obviously, object types are a way to make the typing of JavaScript programs very easy to programmers, which is one of the main goals of TypeScript. It makes the typing concise, intuitive and straightforward to JavaScript programmers. In Java/JSweet, no similar inlined types exist and Java programmers are used to defining classes or interfaces for such cases. So, in JSweet, programmers have to define auxiliary classes annotated with `@ObjectType` for object types. This may seem more complicated, but it has the advantage to force the programmers to name all the types, which, in the end, can lead to more readable and maintenable code depending on the context. Note that similarily to interfaces, object types are erased at runtime. Also `@ObjectType` annotated classes can be inner classes so that they are used locally.

Here is the JSweet version of the previous TypeScript program.

``` java
public class C {
    @ObjectType
    public static class Indexed {
        int index;
    }
    public void m(Indexed param) { ... }
}
```

Using an object type is similar to using an interface:

``` java
C c = ...;
c.m(new Indexed() {{ index = 2; }});
```

When object types are shared objects and represent a typing entity that can be used in several contexts, it is recommended to use the `@Interface` annotation instead of `@ObjectType`. Here is the interface-based version.

``` java
@Interface
public class Indexed {
    int index;
}

public class C {
    public m(Indexed param) { ... }
}

C c = ...;
c.m(new Indexed {{ index = 2; }});
```

### String types

In TypeScript, string types are a way to simulate function overloading depending on the value of a string parameter. For instance, here is a simplified excerpt of the DOM TypeScript definition file:

``` java
// TypeScript:
interface Document {
    [...]
    getElementsByTagName(tagname: "a"): NodeListOf<HTMLAnchorElement>;
    getElementsByTagName(tagname: "b"): NodeListOf<HTMLPhraseElement>;
    getElementsByTagName(tagname: "body"): NodeListOf<HTMLBodyElement>;
    getElementsByTagName(tagname: "button"): NodeListOf<HTMLButtonElement>;
    [...]
}
```

In this code, the `getElementsByTagName` functions are all overloads that depend on the strings passed to the `tagname` parameter. Not only string types allow function overloading (which is in general not allowed in TypeScript/JavaScript), but they also constrain the string values (similarly to an enumeration), so that the compiler can automatically detect typos in string values and raise errors.

This feature being useful for code quality, JSweet provides a mechanism to simulate string types with the same level of type safety. A string type is a public static field annotated with `@StringType`. It must be typed with an interface of the same name declared in the same container type.

For JSweet translated libraries (candies), all string types are declared in a the `jsweet.util.StringTypes` class, so that it is easy for the programmers to find them. For instance, if a `"body"` string type needs to be defined, a Java interface called `body` and a static final field called `body` are defined in a `jsweet.util.StringTypes`.

Note that each candy may have its own string types defined in the `jsweet.util.StringTypes` class. The JSweet transpiler merges all these classes at the bytecode level so that all the string types of all candies are available in the same `jsweet.util.StringTypes` utility class. As a result, the JSweet DOM API will look like:

``` java
@Interface
public class Document {
    [...]
    public native NodeListOf<HTMLAnchorElement> getElementsByTagName(a tagname);
    public native NodeListOf<HTMLPhraseElement> getElementsByTagName(b tagname);
    public native NodeListOf<HTMLBodyElement> getElementsByTagName(body tagname);
    public native NodeListOf<HTMLButtonElement> getElementsByTagName(button tagname);
    [...]
}
```

In this API, `a`, `b`, `body` and `button` are interfaces defined in the `jsweet.util.StringTypes` class. When using one the method of `Document`, the programmer just need to use the corresponding type instance (of the same name). For instance:

``` java
Document doc = ...;
NodeListOf<HTMLAnchorElement> elts = doc.getElementsByTagName(StringTypes.a);
```

Note: if the string value is not a valid Java identifier (for instance `"2d"` or `"string-with-dashes"`), it is then translated to a valid one and annotated with `@Name("originalName")`, so that the JSweet transpiler knows what actual string value must be used in the generated code. For instance, by default, `"2d"` and `"string-with-dashes"` will correspond to the interfaces `StringTypes._2d` and `StringTypes.string_with_dashes` with `@Name` annotations.

Programmers can define string types for their own needs, as shown below:

``` java
import jsweet.lang.Erased;
import jsweet.lang.StringType;

public class CustomStringTypes {
    @Erased
    public interface abc {}
    
    @StringType
    public static final abc abc = null;

    // This method takes a string type parameter
    void m2(abc arg) {
    }
    
    public static void main(String[] args) {
        new CustomStringTypes().m2(abc);
    }
}
```

Note the use of the `@Erased` annotation, which allows the declaration of the `abc` inner interface. This interface is used to type the string type field `abc`. In general, we advise the programmer to group all the string types of a program in the same utility class so that it is easy to find them.

### Tuple types

Tuple types represent JavaScript arrays with individually tracked element types. For tuple types, JSweet defines parameterized auxiliary classes `TupleN<T0, ... TN-1>`, which define `$0`, `$1`, ... `$N-1` public fields to simulate typed array accessed (field `$i` is typed with `Ti`).

For instance, given the following tuple of size 2:

``` java
Tuple2<String, Integer> tuple = new Tuple2<String, Integer>("test", 10);
```

We can expect the following (well-typed) behavior:

``` java
assert tuple.$0 == "test";
assert tuple.$1 == 10;
tuple.$0 = "ok";
tuple.$1--;
assert tuple.$0 == "ok";
assert tuple.$1 == 9;
```

Tuple types are all defined (and must be defined) in the `jsweet.util.tuple` package. By default classes `Tuple[2..6]` are defined. Other tuples (\(>6\)) are automatically generated when encountered in the candy APIs. Of course, when requiring larger tuples that cannot be found in the `jsweet.util.tuple` package, programmers can add their own tuples in that package depending on their needs, just by following the same template as existing tuples.

### Union types

Union types represent values that may have one of several distinct representations. When such a case happens within a method signature (for instance a method allowing several types for a given parameter), JSweet takes advantage of the *method overloading* mechanism available in Java. For instance, the following `m` method accept a parameter `p`, which can be either a `String` or a `Integer`.

``` java
public void m(String p) {...}
public void m(Integer p) {...}
```

In the previous case, the use of explicit union types is not required. For more general cases, JSweet defines an auxiliary interface `Union<T1, T2>` (and `UnionN<T1, ... TN>`) in the `jsweet.util.union` package. By using this auxiliary type and a `union` utility method, programmers can cast back and forth between union types and union-ed type, so that JSweet can ensure similar properties as TypeScript union types.

The following code shows a typical use of union types in JSweet. It simply declares a variable as a union between a string and a number, which means that the variable can actually be of one of that types (but of no other types). The switch from a union type to a regular type is done through the `jsweet.util.Globals.union` helper method. This helper method is completely untyped, allowing from a Java perspective any union to be transformed to another type. It is actually the JSweet transpiler that checks that the union type is consistently used.

``` java
import static jsweet.util.Globals.union;
import jsweet.util.union.Union;
[...]
Union<String, Number> u = ...;
// u can be used as a String
String s = union(u);
// or a number
Number n = union(u);
// but nothing else
Date d = union(u); // JSweet error
```

The `union` helper can also be used the other way, to switch from a regular type back to a union type, when expected.

``` java
import static jsweet.util.Globals.union;
import jsweet.util.union.Union3;
[...]
public void m(Union3<String, Number, Date>> u) { ... }
[...]
// u can be a String, a Number or a Date
m(union("a string"));
// but nothing else
m(union(new RegExp(".*"))); // compile error
```

Note: the use of Java function overloading is preferred over union types when typing function parameters. For example:

``` java
// with union types (discouraged)
native public void m(Union3<String, Number, Date>> u);
// with overloading (preferred way)
native public void m(String s);
native public void m(Number n);
native public void m(Date d);
```

### Intersection types

TypeScript defines the notion of type intersection. When types are intersected, it means that the resulting type is a larger type, which is the sum of all the intersected types. For instance, in TypeScript, `A & B` corresponds to a type that defines both `A` and `B` members.

Intersection types in Java cannot be implemented easily for many reasons. So, the practical choice being made here is to use union types in place of intersection types. In JSweet, `A & B` is thus defined as `Union<A, B>`, which means that the programmer can access both `A` and `B` members by using the `jsweet.util.Globals.union` helper method. It is of course less convenient than the TypeScript version, but it is still type safe.

Semantics
---------

Semantics designate how a given program behaves when executed. Although JSweet relies on the Java syntax, programs are transpiled to JavaScript and do not run in a JRE. As a consequence, the JavaScript semantics will impact the final semantics of a JSweet program compared to a Java program. In this section, we discuss the semantics by focusing on differences or commonalities between Java/JavaSript and JSweet.

### Main methods

Main methods are the program execution entry points and will be invoked globally when a class containing a `main` method is evaluated. For instance:

``` java
public class C {
    private int n;
    public static C instance;
    public static void main(String[] args) {
        instance = new C();
        instance.n = 4;
    }
    public int getN() {
        return n;
    }
}
// when the source file containing C has been evaluated:
assert C.instance != null;
assert C.instance.getN() == 4;
```

The way main methods are globally invoked depends on how the program is packaged. See the appendixes for more details.

### Initializers

Initializers behave like in Java.

For example:

``` java
public class C1 {
    int n;
    {
        n = 4;
    }
}
assert new C1().n == 4;
```

And similarly with static initializers:

``` java
public class C2 {
    static int n;
    static {
        n = 4;
    }
}
assert C2.n == 4;
```

While regular initializers are evaluated when the class is instantiated, static initializers are lazily evaluated in order to avoid forward-dependency issues, and mimic the Java behavior for initializers. With JSweet, it is possible for a programmer to define a static field or a static intializer that relies on a static field that has not yet been initialized.

More details on this behavior can be found in the appendixes.

### Arrays initialization and allocation

Arrays can be used like in Java.

``` java
String[] strings = { "a", "b", "c" };
assert strings[1] == "b";
```

When specifying dimensions, arrays are pre-allocated (like in Java), so that they are initialized with the right length, and with the right sub-arrays in case of multiple-dimensions arrays.

``` java
String[][] strings = new String[2][2];
assert strings.length == 2;
assert strings[0].length == 2;
strings[0][0] = "a";
assert strings[0][0] == "a";
```

The JavaScript API can be used on an array by casting to a `jsweet.lang.Array` with `jsweet.util.Globals.array`.

``` java
import static jsweet.util.Globals.array;
[...]
String[] strings = { "a", "b", "c" };
assert strings.length == 3;
array(strings).push("d");
assert strings.length == 4;
assert strings[3] == "d";
```

In some cases it is preferable to use the `jsweet.lang.Array` class directly.

``` java
Array<String> strings = new Array<String>("a", "b", "c");
// same as: Array<String> strings = array(new String[] { "a", "b", "c" });
// same as: Array<String> strings = new Array<String>(); strings.push("a", "b", "c");
assert strings.length == 3;
strings.push("d");
assert strings.length == 4;
assert strings.$get(3) == "d";
```

### Name clashes

On contrary to TypeScript/JavaScript, Java makes a fundamental difference between methods, fields, and packages. Java also support method overloading (methods having different signatures with the same name). In JavaScript, object variables and functions are stored within the same object map, which basically means that you cannot have the same key for several object members (this also explains that method overloading in the Java sense is not possible in JavaScript). Because of this, some Java code may contain name clashes when generated as is in TypeScript. JSweet will avoid name clashes automatically when possible, and will report sound errors in the other cases.

#### Methods and fields names clashes

JSweet performs a transformation to automatically allow methods and private fields to have the same name. On the other hand, methods and public fields of the same name are not allowed within the same class or within classes having a subclassing link.

To avoid programming mistakes due to this JavaScript behavior, JSweet adds a semantics check to detect duplicate names in classes (this also takes into account members defined in parent classes). As an example:

``` java
public class NameClashes {

    // error: field name clashes with existing method name
    public String a;

    // error: method name clashes with existing field name
    public void a() {
        return a;
    }

}
```

#### Method overloading

On contrary to TypeScript and JavaScript (but similarly to Java), it is possible in JSweet to have several methods with the same name but with different parameters (so-called overloads). We make a distinction between simple overloads and complex overloads. Simple overloading is the use of method overloading for defining optional parameters. JSweet allows this idiom under the condition that it corresponds to the following template:

``` java
String m(String s, double n) { return s + n; }
// valid overloading (JSweet transpiles to optional parameter)
String m(String s) { return m(s, 0); }
```

In that case, JSweet will generate JavaScript code with only one method having default values for the optional parameters, so that the behavior of the generated program corresponds to the original one. In this case:

``` java
function m(s, n = 0) { return s + n; }
```

If the programmer tries to use overloading differently, for example by defining two different implementations for the same method name, JSweet will fallback on a complex overload, which consists of generating a root implementation (the method that hold the more parameters) and one subsidiary implementation per overloading method (named with a suffix representing the method signature). The root implementation is generic and dispatches to other implementations by testing the values and types of the given parameters. For example:

``` java
String m(String s, double n) { return s + n; }
String m(String s) { return s; }
```

Generates the following (slightly simplified) JavaScript code:

``` java
function m(s, n) { 
    if(typeof s === 'string' && typeof n === 'number') {    
        return s + n; 
    } else if(typeof s === 'string' && n === undefined) {
        return this.m$java_lang_String(s);
    } else {
        throw new Error("invalid overload");    
    }
}

function m$java_lang_String(s) { return s; }
```

#### Local variable names

In TypeScript/JavaScript, local variables can clash with the use of a global method. For instance, using the `alert` global method from the DOM (`jsweet.dom.Globals.alert`) requires that no local variable hides it:

``` java
import static jsweet.dom.Globals.alert;

[...]

public void m1(boolean alert) {
    // JSweet compile error: name clash between parameter and method call
    alert("test");
}

public void m2() {
    // JSweet compile error: name clash between local variable and method call
    String alert = "test";
    alert(alert);
}
```

Note that this problem also happens when using fully qualified names when calling the global methods (that is because the qualification gets erased in TypeScript/JavaScript). In any case, JSweet will report sound errors when such problems happen so that programmers can adjust local variable names to avoid clashes with globals.

### Testing the type of an object (`instanceof`)

To test the type of a given object at runtime, one can use the `instanceof` Java operator. It is the advised and preferred way to test types at runtime. JSweet will transpile to a regular `instanceof` or to a `typeof` operator depending on the tested type (it will fallback on `typeof` for `number`, `string`, and `boolean` core types).

Although not necessary, it is also possible to directly use the `typeof` operator from JSweet with the `jsweet.util.Globals.typeof` utility method. Here are some examples of valid type tests:

``` java
import static jsweet.util.Globals.typeof;
import static jsweet.util.Globals.equalsStrict;
[...]
Number n1 = 2;
Object n2 = 2;
int n3 = 2;
Object s = "test";
MyClass c = new MyClass();

assert n1 instanceof Number; // transpiles to a typeof
assert n2 instanceof Number; // transpiles to a typeof
assert n2 instanceof Integer; // transpiles to a typeof
assert !(n2 instanceof String); // transpiles to a typeof
assert s instanceof String; // transpiles to a typeof
assert !(s instanceof Integer); // transpiles to a typeof
assert c instanceof MyClass;
assert typeof(n3) == "number";
```

From JSweet version 1.1.0, the `instanceof` operator is also allowed on interfaces, because JSweet keeps track of all the implemented interfaces for all objects. This interface tracking is ensured through an additional hidden property in the objects called `__interfaces` and containing the names of all the interfaces implemented by the objects (either directly or through its class inheritance tree determined at compile time). So, in case the type argument of the `instanceof` operator is an interface, JSweet simply checks out if the object’s `__interfaces` field exists and contains the given interface. For example, this code is fully valid in JSweet when `Point` is an interface:

``` java
Point p1 = new Point() {{ x=1; y=1; }};
[...]
assert p1 instanceof Point
```

#### Limitations and constraints

Since all numbers are mapped to JavaScript numbers, JSweet make no distinction between integers and floats for example. So, `n instanceof Integer` and `n instanceof Float` will always give the same result whatever the actual type of `n` is. The same limitation exists for strings and chars, which are not distinguishable at runtime, but also for functions that have the same number of parameters. For example, an instance of `IntFunction<R>` will not be distinguishable at runtime from a `Function<String,R>`.

These limitations have a direct impact on function overloading, since overloading uses the `instanceof` operator to decide which overload to be called.

Like it is usually the case when working in JavaScript, serialized objects must be properly “revived” with their actual classes so that the `instanceof` operator can work again. For example a point object created through `Point p = (Point)JSON.parse({x:1,y:1})` will not work with regard to the `instanceof` operator. In case you meet such a use case, you can contact us to get some useful JSweet code to properly revive object types.

### Variable scoping in lambda expressions

JavaScript variable scoping is known to pose some problems to the programmers, because it is possible to change the reference to a variable from outside of a lambda that would use this variable. As a consequence, a JavaScript programmer cannot rely on a variable declared outside of a lambda scope, because when the lambda is executed, the variable may have been modified somewhere else in the program. For instance, the following program shows a typical case:

``` java
NodeList nodes = document.querySelectorAll(".control");
for (int i = 0; i < nodes.length; i++) {
    HTMLElement element = (HTMLElement) nodes.$get(i); // final
    element.addEventListener("keyup", (evt) -> {
        // this element variable will not change here 
        element.classList.add("hit");
    });
}
```

In JavaScript (note that EcmaScript 6 fixes this issue), such a program would fail its purpose because the `element` variable used in the event listener is modified by the for loop and does not hold the expected value. In JSweet, such problems are dealt with similarly to final Java variables. In our example, the `element` variable is re-scoped in the lambda expression so that the enclosing loop does not change its value and so that the program behaves like in Java (as expected by most programmers).

### Scope of *this*

On contrary to JavaScript and similarly to Java, using a method as a lambda will prevent loosing the reference to `this`. For instance, in the `action` method of the following program, `this` holds the right value, even when `action` was called as a lambda in the `main` method. Although this seem logical to Java programmers, it is not a given that the JavaScript semantics ensures this behavior.

``` java
package example;
import static jsweet.dom.Globals.console;

public class Example {
    private int i = 8;
    public Runnable getAction() {
        return this::action;
    }
    public void action() {
        console.log(this.i); // this.i is 8
    }
    public static void main(String[] args) {
        Example instance = new Example();
        instance.getAction().run();
    }
}
```

It is important to stress that the `this` correct value is ensured thanks to a similar mechanism as the ES5 `bind` function. A consequence is that function references are wrapped in functions, which means that function pointers (such as `this::action`) create wrapping functions on the fly. It has side effects when manipulating function pointers, which are well described in this issue <https://github.com/cincheo/jsweet/issues/65>.

Packaging
---------

Packaging is one of the complex point of JavaScript, especially when coming from Java. Complexity with JavaScript packaging boils down to the fact that JavaScript did not define any packaging natively. As a consequence, many *de facto* solutions and guidelines came up along the years, making the understanding of packaging uneasy for regular Java programmers. JSweet provides useful options and generates code in order to simplify the life of Java programmers by making the packaging issues much more transparent and as “easy” as in Java for most cases. In this section, we will describe and explain typical packaging scenarios.

### Use your files without any packaging

The most common and simple case for running a program is just to include each generated file in an HTML page. This is the default mode when not precising any packaging options. For example, when your program defines two classes `x.y.z.A` and `x.y.z.B` in two separated files, you can use them as following:

```
<script type="text/javascript" src="target/js/x/y/z/A.js"></script>
<script type="text/javascript" src="target/js/x/y/z/B.js"></script>
[...]
<!-- access a method later in the file -->
<script type="text/javascript">x.y.z.B.myMethod()</script>
```

When doing so, programmers need to be extremely cautious to avoid forward static dependencies between the files. In other words, the `A` class cannot use anything from `B` in static fields, static initializers, or static imports, otherwise leading to runtime errors when trying to load the page. Additionally, the `A` class cannot extend the `B` class. These constraints come from JavaScript/TypeScript and have nothing to do with JSweet.

As you can imagine, running simple programs with this manual technique is fine, but can become really uncomfortable for developing complex applications. Complex applications most of the time bundle and/or package the program with appropriate tools in order to avoid having to manually handle dependencies between JavaScript files.

### Creating a bundle for a browser

To avoid having to take care of the dependencies manually, programmers use bundling tools to bundle up their classes into a single file. Such a bundle is included in any web page using something like this:

```
<script type="text/javascript" src="target/js/bundle.js"></script>
[...]
<!-- access a method later in the file -->
<script type="text/javascript">x.y.z.B.myMethod()</script>
```

JSweet comes with such a bundling facility. To create a bundle file, just set to `true` the `bundle` option of JSweet. Note that you can also set to `true` the `declaration` option that will ask JSweet to generate the TypeScript definition file (`bundle.d.ts`). This file allows you to use/compile your JSweet program from TypeScript in a well-typed way.

The “magic” with JSweet bundling option is that it analyzes the dependencies in the source code and takes care of solving forward references when building the bundle. In particular, JSweet implements a lazy initialization mechanism for static fields and initializers in order to break down static forward references across the classes. There are no specific additional declarations to be made by the programmers to make it work (on contrary to TypeScript).

Note that there are still some minor limitations to it (when using inner and anonymous classes for instance), but these limitations will be rarely encountered and will be removed in future releases.

Note also that JSweet will raise an error if you specify the `module` option along with the `bundle` option.

### Packaging with modules

First, let us start by explaining modules and focus on the difference between Java *packages* (or TypeScript *namespaces*) and *modules*. If you feel comfortable with the difference, just skip this section.

Packages and modules are two similar concepts but for different contexts. Java packages must be understood as compile-time *namespaces*. They allow a compile-time structuration of the programs through name paths, with implicit or explicit visibility rules. Packages have usually not much impact on how the program is actually bundled and deployed.

Modules must be understood as deployment / runtime “bundles”, which can be `required` by other modules. The closest concept to a module in the Java world would probably be an OSGi bundle. A module defines imported and exported elements so that they create a strong runtime structure that can be used for deploying software components independently and thus avoiding name clashes. For instance, with modules, two different libraries may define a `util.List` class and be actually running and used on the same VM with no naming issues (as long as the libraries are bundled in different modules).

Nowadays, a lot of libraries are packaged and accessible through modules. The standard way to use modules in a browser is the AMD, but in Node.js it is the commonjs module system.

#### Modules in JSweet

JSweet supports AMD, commonjs, and UMD module systems for packaging. JSweet defines a `module` option (value: `amd`, `commonjs` or `umd`). When specifying this option, JSweet automatically creates a default module organization following the simple rule: one package = one module generated in a single file called `module.js`.

For example, when packaged with the `module` option set to `commonjs`, one can write:

```
> node target/js/x/y/z/module.js
```

The module system will automatically take care of the references and require other modules when needed.

Note: once the program has been compiled with the `module` option, it is easy to package it as a bundle using appropriate tools such as Browserify, which would give similar output as using the `bundle` option of JSweet. Note also that JSweet will raise an error when specifying both `module` and `bundle`, which are exclusive options.

#### External modules

When compiling JSweet programs with the `module` options, all external libraries and components must be required as external modules. JSweet can automatically require modules, simply by using the `@Module(name)` annotation. In JSweet, importing or using a class or a member annotated with `@Module(name)` will automatically require the corresponding module at runtime. Please not that it is true only when the code is generated with the `module` option. If the `module` option is off, the `@Module` annotations are ignored.

``` java
package def.jquery;
public final class Globals extends jsweet.lang.Object {
    ...
    @jsweet.lang.Module("jquery")
    native public static def.jquery.JQuery $(java.lang.String selector);
    ...
}
```

The above code shows an excerpt of the JSweet jQuery API. As we can notice, the `$` function is annotated with `@Module("jquery")`. As a consequence, any call to this function will trigger the require of the `jquery` module.

Note: the notion of manual require of a module may be available in future releases. However, automatic require is sufficient for most programmers and hides the complexity of having to require modules explicitly. It also brings the advantage of having the same code whether modules are used or not.

Troubleshooting: when a candy does not define properly the `@Module` annotation, it is possible to force the declaration within the comment of a special file called `module_defs.java`. For example, to force the `BABYLON` namespace of the Babylonjs candy to be exported as a `babylonjs` module, you can write the following file:

``` java
package myprogram;
// declare module "babylonjs" {
//    export = BABYLON;
// } 
```

Note that a JSweet project can only define one `module_defs.java` file, which shall contain all the module declarations in a comment.

### Root packages

Root packages are a way to tune the generated code so that JSweet packages are erased in the generated code and thus at runtime. To set a root package, just define a `package-info.java` file and use the `@Root` annotation on the package, as follows:

``` java
@Root
package a.b.c;
```

The above declaration means that the `c` package is a root package, i.e. it will be erased in the generated code, as well as all its parent packages. Thus, if `c` contains a package `d`, and a class `C`, these will be top-level objects at runtime. In other words, `a.b.c.d` becomes `d`, and `a.b.c.C` becomes `C`.

Note that since that packaged placed before the `@Root` package are erased, there cannot be any type defined before a `@Root` package. In the previous example, the *a* and *b* packages are necessarily empty packages.

#### Behavior when not using modules (default)

By default, root packages do not change the folder hierarchy of the generated files. For instance, the `a.b.c.C` class will still be generated in the `<jsout>/a/b/c/C.js` file (relatively to the `<jsout>` output directory). However, switching on the `noRootDirectories` option will remove the root directories so that the `a.b.c.C` class gets generated to the `<jsout>/C.js` file.

When not using modules (default), it is possible to have several `@Root` packages (but a `@Root` package can never contain another `@Root` package).

#### Behavior when using modules

When using modules (see the *module* option), only one `@Root` package is allowed, and when having one `@Root` package, no other package or type can be outside of the scope of that `@Root` package. The generated folder/file hierarchy then starts at the root package so that all the folders before it are actually erased.

### Packaging a JSweet jar (candy)

A candy is a Maven artifact that contains everything required to easily access a JavaScript library from a JSweet client program. This library can be an external JavaScript library, a TypeScript program, or another JSweet program.

#### Anatomy of a candy

Like any Maven artifact, a candy has a group id, a artifact id (name), and a version. Besides, a typical candy should contain the following elements:

1.  The compiled Java files (\*.class), so that your client program that uses the candy can compile.

2.  A `META-INF/candy-metadata.json` file that contains the expected target version of the transpiler (to be adapted to your target transpiler version).

3.  The program’s declarations in `d.ts` files, to be placed in the `src/typings` directory of the jar. Note that these definitions are not mandatory if you intend to use JSweet for generating TypeScript source code (`tsOnly` option). In that case, you may delegate the JavaScript generation to an external `tsc` compiler and access the TypeScript definitions from another source.

4.  Optionally, the JavaScript bundle of the library, which can in turn be automatically extracted and used by the JSweet client programs. JSweet expects the JavaScript to be packaged following the Webjars conventions: <http://www.webjars.org/>. When packaged this way, a JSweet transpiler using your candy will automatically extract the bundled JavaScript in a directory given by the `candiesJsOut` option (default: `js/candies`).

Here is an example of the `META-INF/candy-metadata.json` file:

``` java
{
   "transpilerVersion": "1.0.0"
}
```

#### How to create a candy from a JSweet program

A typical use case when building applications with JSweet, is to share a common library or module between several other JSweet modules/applications. Note that since a JSweet candy is a regular Maven artifact, it can also be used by a regular Java program as long as it does not use any JavaScript APIs.

So, a typical example in a project is to have a *commons* library containing DTOs and common utility functions, which can be shared between a Web client written in JSweet (for example using the angular or knockout libraries) and a mobile client written also in JSweet (for example using the ionic library). The great news is that this *commons* library can also be used by the Java server (JEE, Spring, ...) as is, because the DTOs do not use any JavaScript, and that the compiled Java code packaged in the candy can run on a Java VM. This this extremely helpful, because it means that when you develop this project in your favorite IDE, you will be able to refactor some DTOs and common APIs, and it will directly impact your Java server code, your Web client code, and your mobile client code!

We provide a quick start project to help you starting with such a use case: <https://github.com/cincheo/jsweet-candy-quickstart>

#### How to create a candy for an existing JavaScript or TypeScript library

We provide a quick start project to help you starting with such a use case: <https://github.com/cincheo/jsweet-candy-js-quickstart>

Appendix 1: JSweet transpiler options
-------------------------------------

      [-h|--help]

      [-v|--verbose]
            Turn on all levels of logging.

      [--encoding <encoding>]
            Force the Java compiler to use a specific encoding (UTF-8, UTF-16, ...).
            (default: UTF-8)

      [--jdkHome <jdkHome>]
            Set the JDK home directory to be used to find the Java compiler. If not
            set, the transpiler will try to use the JAVA_HOME environment variable.
            Note that the expected JDK version is greater or equals to version 8.

      (-i|--input) <input>
            An input dir containing Java files to be transpiled.

      [--noRootDirectories]
            Skip the root directories (i.e. packages annotated with
            @jsweet.lang.Root) so that the generated file hierarchy starts at the
            root directories rather than including the entire directory structure.

      [--tsout <tsout>]
            Specify where to place generated TypeScript files. (default: .ts)

      [(-o|--jsout) <jsout>]
            Specify where to place generated JavaScript files (ignored if jsFile is
            specified). (default: js)

      [--tsOnly]
            Tells the transpiler to not compile the TypeScript output (let an
            external TypeScript compiler do so).

      [--disableJavaAddons]
            Tells the transpiler disable runtime addons (instanceof, overloading,
            class name access, static initialization [...] will not be fully
            supported).

      [--definitions]
            Tells the transpiler to generate definitions from def.* packages in d.ts
            definition files. The output directory is given by the tsout
            option. This option can be used to create candies for existing
            JavaScript libraries and must not be confused with the 'declaration'
            option, that generates the definitions along with a program written in
            JSweet.

      [--declaration]
            Tells the transpiler to generate the d.ts files along with the js files,
            so that other programs can use them to compile.

      [--dtsout <dtsout>]
            Specify where to place generated d.ts files when the declaration option
            is set (by default, d.ts files are generated in the JavaScript output
            directory - next to the corresponding js files).

      [--candiesJsOut <candiesJsOut>]
            Specify where to place extracted JavaScript files from candies.
            (default: js/candies)

      [--classpath <classpath>]
            The JSweet transpilation classpath (candy jars). This classpath should
            at least contain the core candy.

      [(-m|--module) <module>]
            The module kind (none, commonjs, amd, system or umd). (default: none)

      [-b|--bundle]
            Bundle up the generated files and used modules to bundle files, which
            can be used in the browser. Bundles contain all the dependencies and are
            thus standalone. There is one bundle generated per entry (a Java 'main'
            method) in the program. By default, bundles are generated in the entry
            directory, but the output directory can be set by using the
            --bundlesDirectory option. NOTE: bundles will be generated only when
            choosing the commonjs module kind.

      [--bundlesDirectory <bundlesDirectory>]
            Generate all the bundles (see option --bundle) within the given
            directory.

      [--sourceMap]
            Set the transpiler to generate source map files for the Java files, so
            that it is possible to debug them in the browser. This feature is not
            available yet when using the --module option. Currently, when this
            option is on, the generated TypeScript file is not pretty printed in a
            programmer-friendly way (disable it in order to generate readable
            TypeScript code).

      [--ignoreAssertions]
            Set the transpiler to ignore 'assert' statements, i.e. no code is
            generated for assertions.

Appendix 2: packaging and static behavior
-----------------------------------------

This appendix explains some static behavior with regards to packaging.

### When main methods are invoked

When main methods are invoked depends on the way the program is packaged.

-   `module`: off, `bundle`: off. With default packaging, one Java source file corresponds to one generated JavaScript file. In that case, when loading a file in the browser, all the main methods will be invoked right at the end of the file.

-   `module`: off, `bundle`: on. When the `bundle` option is on and the `module` option is off, main methods are called at the end of the bundle.

-   `module`: on, `bundle`: off. With module packaging (`module` option), one Java package corresponds to one module. With modules, it is mandatory to have only one main method in the program, which will be the global entry point from which the module dependency graph will be calculated. The main module (the one with the main method) will use directly or transitively all the other modules. The main method will be invoked at the end of the main module evaluation.

Because of modules, it is good practice to have only one main method in an application.

### Static and inheritance dependencies

In TypeScript, programmers need to take care of the ordering of classes with regards to static fields and initializers. Typically, a static member cannot be initialized with a static member of a class that has not yet been defined. Also, a class cannot extend a class that has not been defined yet. This forward-dependency issue triggers runtime errors when evaluating the generated JavaScript code, which can be quite annoying for the programmers and may requires the use of external JavaScript bundling tools, such as Browserify.

JSweet’s statics lazy initialization allows static forward references within a given file, and within an entire bundle when the `bundle` option is set. Also, when bundling a set of files, JSweet analyses the inheritance tree and performs a partial order permutation to eliminate forward references in the inheritance tree. Note that TypeScript bundle provide a similar feature, but the references need to be manually declared, which is not convenient for programmers.

To wrap it up, here are the guidelines to be followed by the programmers depending on the packaging method:

-   `module`: off, `bundle`: off. One JavaScript file is generated per Java file. The programmer must take care of including the files in the right order in the HTML page, so that there are no forward references with regard to inheritance and statics. Within a given file, static forward references are allowed, but inheritance forward reference are not supported yet (this will be supported in coming releases).

-   `module`: off, `bundle`: on. This configuration produces a unique browser-compatible bundle file that can be included in an HTML page. Here, the programmer does not have to take care at all of the forward references across files. Exactly like in Java, the order does not matter. Within a single file, the programmer still have to take care of the inheritance forward references (in other words, a subclass must be declared after its parent class) (this will be supported in coming releases).

-   `module`: commonjs, amd or umd, `bundle`: off. This configuration produces one module file per Java package so that they can be used within a module system. For instance, using the `commonjs` module kind will allow the program to run on Node.js. In that configuration, the program should contain one main method and only the module file containing the main method should be loaded (because it will take care loading all the other modules). This configuration imposes the same constraint within a single file (no forward-references in inheritance).
