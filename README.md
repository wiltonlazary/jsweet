# JSweet: a Java to JavaScript transpiler [![Build Status](https://travis-ci.org/cincheo/jsweet.svg?branch=master)](https://travis-ci.org/cincheo/jsweet)

JSweet leverages TypeScript to write rich and responsive Web applications in Java through the use of JavaScript libraries and frameworks. With JSweet, Java programs are transpiled (source-to-source compiled) to TypeScript and JavaScript for being run in browsers, mobile Web views, or in Node.js. 

* JSweet is safe and reliable. It provides web applications with type-checking and generates fully type-checked JavaScript programs. It stands on Oracle's Java Compiler (javac) and on Microsoft's TypeScript (tsc). 
* JSweet allows you to use your favorite JS library ([JSweet+Angular2](https://github.com/cincheo/jsweet-angular2-quickstart), [JSweet+threejs](https://github.com/cincheo/jsweet-examples-threejs), [IONIC/Cordova](https://github.com/lgrignon/jsweet-cordova-ionic-example), ...).
* JSweet enables code sharing between server-side Java and client-side JavaScript. JSweet provides implementations for the core Java libraries for code sharing and legacy Java migration purpose.
* JSweet is fast, lightweight and fully JavaScript-interoperable. The generated code is regular JavaScript code, which implies no overhead compared to JavaScript, and can directly interoperate with existing JavaScript programs and libraries.

How does it work? JSweet depends on well-typed descriptions of JavaScript APIs, so-called "candies", most of them being automatically generated from TypeScript definition files. These API descriptions in Java can be seen as headers (similarly to *.h header files in C) to bridge JavaSript libraries from Java. There are several sources of candies for existing libraries and you can easily build a candy for any library out there (see [more details](http://www.jsweet.org/jsweet-candies/)). 

With JSweet, you take advantage of all the Java tooling (IDE's, Maven, ...) to program real JavaScript applications using the latest JavaScript libraries.

## Java -> TypeScript -> JavaScript

Here is a first taste of what you get by using JSweet. Consider this simple Java program:

```java
package org.jsweet;

import static jsweet.dom.Globals.*;

/**
 * This is a very simple example that just shows an alert.
 */
public class HelloWorld {
	public static void main(String[] args) {
		alert("Hi there!");
	}
}
```

Transpiling with JSweet gives the following TypeScript program:

```TypeScript
namespace org.jsweet {
    /**
     * This is a very simple example that just shows an alert.
     */
    export class HelloWorld {
        public static main(args : string[]) {
            alert("Hi there!");
        }
    }
}
org.jsweet.HelloWorld.main(null);
```

Which in turn produces the following JavaScript output:

```JavaScript
var org;
(function (org) {
    var jsweet;
    (function (jsweet) {
        /**
         * This is a very simple example that just shows an alert.
         */
        var HelloWorld = (function () {
            function HelloWorld() {
            }
            HelloWorld.main = function (args) {
                alert("Hi there!");
            };
            return HelloWorld;
        }());
        jsweet.HelloWorld = HelloWorld;
    })(jsweet = org.jsweet || (org.jsweet = {}));
})(org || (org = {}));
org.jsweet.HelloWorld.main(null);
```

More with the [live sandbox](http://www.jsweet.org/jsweet-live-sandbox/).

## Features

- Full syntax mapping between Java and TypeScript, including classes, interfaces, functional types, union types, tuple types, object types, string types, and so on.
- Extensive support of Java constructs and semantics added since [version 1.1.0](https://github.com/cincheo/jsweet/releases/tag/v1.1.0) (inner classes, anonymous classes, final fields, method overloading, instanceof operator, static initializers, ...).
- *Over 1000 JavaScript libraries*, frameworks and plugins to write Web and Mobile HTML5 applications (JQuery, Underscore, Angular, Backbone, Cordova, Node.js, and much [more](http://www.jsweet.org/candies-snapshots/)).
- A [Maven repository](http://repository.jsweet.org/artifactory) containing all the available libraries in Maven artifacts (a.k.a. candies).
- Support for Java basic APIs as the [J4TS](https://github.com/cincheo/j4ts) candy (forked from the GWT's JRE emulation).
- An [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin) for easy installation and use.
- A [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin) to use JSweet from any other IDE or from the command line.
- A debug mode to enable Java code debugging within your favorite browser.
- A set of nice [WEB/Mobile HTML5 examples](https://github.com/cincheo/jsweet-examples) to get started and get used to JSweet and the most common JavaScript APIs (even more examples in the Examples section). 
- Support for bundles to run the generated programs in the most simple way.
- Support for JavaScript modules (commonjs, amd, umd). JSweet programs *can* run in a browser or in Node.js.
- Support for various EcmaScript target versions (ES3 to ES6).
- ...

For more details, go to the [language specifications](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md) ([PDF](https://github.com/cincheo/jsweet/raw/master/doc/jsweet-language-specifications.pdf)).

## Getting started

- Step 1: Install (or check that you have installed) [Git](https://git-scm.com/downloads), [Node.js](https://nodejs.org) and [Maven](https://maven.apache.org/) (commands `git`, `node`, `npm` and `mvn` should be in your path).
- Step 2: Clone the [jsweet-quickstart](https://github.com/cincheo/jsweet-quickstart) project from Github:
```
> git clone https://github.com/cincheo/jsweet-quickstart.git
```
- Step 3: Run the transpiler to generate the JavaScript code:
```
> cd jsweet-quickstart
> mvn generate-sources
```
- Step 4: Check out the result in your browser:
```
> firefox webapp/index.html
```
- Step 5: Edit the project and start programming:
	- Checkout the examples to see various use cases 
	- Get access to hundreds of [libs (candies)](http://www.jsweet.org/jsweet-candies/)
	- Refer to the [language specifications](https://github.com/cincheo/jsweet/blob/master/doc/jsweet-language-specifications.md) to know more about programming with JSweet
	- Eclipse users: install the [Eclipse plugin](http://www.jsweet.org/eclipse-plugin/) to get inline error reporting, build-on-save, and easy configuration UI

More info at http://www.jsweet.org.

## Examples

- Simple examples illustrating the use of various frameworks in Java (jQuery, Underscore, Backbone, AngularJS, Knockout): https://github.com/cincheo/jsweet-examples 
- Simple examples illustrating the use of the Threejs framework in Java: https://github.com/cincheo/jsweet-examples-threejs) 
- Node.js + Socket.IO + AngularJS: https://github.com/lgrignon/jsweet-node-example
- Some simple examples to get started with React.js: https://github.com/cincheo/jsweet-examples-react
- JSweet JAX-RS server example (how to share a Java model between client and server): https://github.com/lgrignon/jsweet-jaxrs-server-example 
- JSweet Cordova / Polymer example: https://github.com/lgrignon/jsweet-cordova-polymer-example
- JSweet Cordova / Ionic example: https://github.com/lgrignon/jsweet-cordova-ionic-example
- JSweet Angular 2 example: https://github.com/cincheo/jsweet-angular2-quickstart
- JSweet Angular 2 + PrimeNG: https://github.com/cincheo/jsweet-primeng-quickstart 

## Tooling

- [Eclipse plugin](https://github.com/cincheo/jsweet-eclipse-plugin)
- [Maven plugin](https://github.com/lgrignon/jsweet-maven-plugin)
- [Gradle plugin](https://github.com/lgrignon/jsweet-gradle-plugin)

## News and more information

Most information is available on the http://www.jsweet.org. Developers, check out the [wiki](https://github.com/cincheo/jsweet/wiki).

## How to build

Prerequisites: `node` and `npm` executables must be in the path (https://nodejs.org). Note that there seem to be an issue with the `node` executable on some Unix-based machines, which should be fixed (see for instance: http://askubuntu.com/questions/235655/node-js-conflicts-sbin-node-vs-usr-bin-node).

To build the `jsweet-transpiler` jars (in the project's directory):

```
> mvn package
```

To install the `jsweet-transpiler` artifact in your local Maven repository:

```
> mvn install
```

Note that current JUnit tests launch a Node.js instance for each test and will be quite slow (this will be improved). In order to easily test some changes locally without having to run all the tests, use the following command:

```
> mvn package -Dmaven.test.skip=true
```

or

```
> mvn install -Dmaven.test.skip=true
```

To generate the markdown language specifications from the Latex source file with [Pandoc](http://pandoc.org/):

```
> cd doc
> pandoc -r latex -w markdown_github --base-header-level=2 -s --toc --number-sections -B header.md -o jsweet-language-specifications.md jsweet-language-specifications.tex
```

Note that the following command will output the document in HTML:

```
> pandoc -r latex -w html5 --base-header-level=3 -o jsweet-language-specifications.html jsweet-language-specifications.tex
```

## License

JSweet is under the Apache 2.0 Open Source license.

