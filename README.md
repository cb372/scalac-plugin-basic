## BASIC-emitting Scala compiler plugin

An example of how to write a plugin for the Scala compiler.

This plugin adds a phase that traverses selected parts of the AST, generates corresponding BASIC code and writes it to a file.

Only supports a tiny subset of Scala/BASIC syntax.

## Usage

First package the plugin into a jar:

```
$ sbt package
```

Then compile the sample code in `src/main/test`:

```
$ sbt test:compile
```

This will result in a BASIC file being created:

```
$ cat printHelloForever.bas
10 REM
20 PRINT "Hello Scala World"
30 GOTO 10
```

Run the file in a BASIC interpreter of your choice (I used Chipmunk BASIC on OSX).

```
$ basic printHelloForever.bas
Hello Scala World
Hello Scala World
Hello Scala World
Hello Scala World
...
```
