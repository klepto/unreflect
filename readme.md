# unreflect 
[![Maven Central](https://img.shields.io/maven-central/v/dev.klepto.unreflect/unreflect.svg)](https://central.sonatype.com/artifact/dev.klepto.unreflect/unreflect) [![javadoc](https://javadoc.io/badge2/dev.klepto.unreflect/unreflect/javadoc.svg)](https://javadoc.io/doc/dev.klepto.unreflect/unreflect)

Small but dangerously powerful wrapper of Reflection API.

**Notable Features**
* High performance reflection using runtime bytecode generation.
* Allows unrestricted access to any modules and members of entire classpath, even if they are private (including internal JDK API).
* Automatically incorporates methods and fields of super classes and interfaces.
* Binds API to an instance, no need to store instances you want to use for reflection.
* Ability to initialiaze classes without calling their constructor.

## Installation
```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation "dev.klepto.unreflect:unreflect:1.2"
}
```

## Getting Started
Creating an instance of a class.
```java
public class HelloClass {
    public HelloClass(String message) {
        System.out.println(message);
    }
}

Unreflect.reflect(HelloClass.class).create("hello from unreflect!");
```

Adding and getting element from a list.
```java
var list = new ArrayList<String>();

reflect(list)
        .method("add")
        .invoke("hello list!");

reflect(list)
        .method("get")
        .invoke(0); // hello list!
```
Adding elements to different lists using same accessor.
```java
var listA = new ArrayList<String>();
var listB = new ArrayList<String>();

var method = reflect(ArrayList.class).method("add");
method.bind(listA).invoke("hello to list A!");
method.bind(listB).invoke("hello to list B!");
```
Reflection not fast enough? Generate some bytecode to access members directly.
```java
var list = new ArrayList<String>();

// Calling unreflect on any member generates direct access bytecode.
var method = reflect(list).method("add").unreflect();
method.invoke("i'm almost as fast as direct call to list.add()! :)");
```
## Performance
Reflection is slow. It's commonly assumed that reflection is around 2x slower than direct access.
If invocation speed is important, you can sacrifice load time to generate bytecode in order to drastically increase your
performance.

![Performance Graph](graphs.png?raw=true)

The performance benefits will vary based on your system and version of JVM. But bytecode access is always guaranteed 
to be significantly faster than Reflection.
## Visibility
Unreflect can access absolutely everything. To achieve this, library contains heavy usage of internal JDK API which means this
library is not guaranteed to work on different versions of JVM or future updates of JVM.
## Compatibility
Library has been tested and confirmed to work with all versions of OpenJDK from OpenJDK 8 to OpenJDK 20.
Different forks of JVM are not guaranteed to work and are not planned to be implemented/maintained.