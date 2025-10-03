error id: 
file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/build.sbt
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -run.
	 -run#
	 -run().
	 -scala/Predef.run.
	 -scala/Predef.run#
	 -scala/Predef.run().
offset: 473
uri: file:///C:/labs/MAGISTER/1%20course/TRPO/Lab-2-1/lab1/build.sbt
text:
```scala
name         := "lab1"
organization := "myscalafx"
version      := "0.1-SNAPSHOT"

scalaVersion := "2.13.12"

libraryDependencies ++= Seq(
  "org.scalafx"   %% "scalafx"   % "21.0.0-R32",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"
)

// Fork a new JVM for 'run' and 'test:run' to avoid JavaFX double initialization problems
fork := true

// set the main class for the main 'run' task
// change Compile to Test to set it for 'test:run'
Compile / ru@@n / mainClass := Some("my.scalafx.Main")
```


#### Short summary: 

empty definition using pc, found symbol in pc: 