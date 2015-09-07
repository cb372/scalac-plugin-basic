scalaVersion := "2.11.7"
libraryDependencies += "org.scala-lang" % "scala-compiler" % "2.11.7"
scalacOptions in Test += "-Xplugin:target/scala-2.11/scalac-plugin-basic_2.11-0.1-SNAPSHOT.jar"
