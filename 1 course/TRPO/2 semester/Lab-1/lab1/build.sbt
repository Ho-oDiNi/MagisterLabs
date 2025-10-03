name         := "lab1"
organization := "myscalafx"
version      := "0.1-SNAPSHOT"

scalaVersion := "2.13.12"

// Определяем модули JavaFX в зависимости от ОС
lazy val osName = System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
}

lazy val javaFXModules = Seq("base", "controls", "graphics", "fxml")

libraryDependencies ++= Seq(
  "org.scalafx" %% "scalafx" % "21.0.0-R32",
  "org.scalatest" %% "scalatest" % "3.2.17" % "test"
) ++ javaFXModules.map(m => 
  "org.openjfx" % s"javafx-$m" % "21" classifier osName
)

// Настройки для Java 16+
fork := true

// Указываем главный класс
Compile / run / mainClass := Some("my.scalafx.Main")