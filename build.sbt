// Project template

// Supported operating systems: Windows, Mac, Linux
// Supported JDKs: 8, 10+

// Project name
name := "visualnotes"

// organization name
organization := "fi.utu.tech"

version := "1.0"

// project description
description := "Visual Notes"

// main class
Compile/mainClass := Some("fi.utu.tech.visualnotes.Main")

// force the java version by typing it here (remove the comment)
val force_javaVersion = None // Some(13)

// force the javafx version by typing it here (remove the comment)
val force_javaFxVersion = None // Some(13)

val useJavaFX = true

val useScalaOrScalaFX = true

// END_OF_SIMPLE_CONFIGURATION
// you can copy the rest for each new project
// --- --- ---

def fail(msg: String) = {
  println("Error :-/")
  println
  println(msg)
  System.exit(1)
  null
}

val detectedJDK = System.getProperty("java.version").replace("-ea","").split('.').dropWhile(_.toInt<8).head.toInt

val javaVersionNum = force_javaVersion.getOrElse(detectedJDK)

val javaVersionString = javaVersionNum match {
  case 7 => "1.7"
  case 8 => "1.8"
  case x if x > 8 => x.toString
}

val lts = 11
val dev = 13

val supported = javaVersionNum match {
  case x if x < 8              => fail("Your Java installation is obsolete. Please upgrade to Java " + lts + "LTS")
  case 9                       => fail("Your Java installation is unsupported and has known issues. Please upgrade to Java " + lts + "LTS")
  case x if x < lts            => println("Consider upgrading to Java " + lts + " LTS"); true
  case x if x > lts && x < dev => println("Consider upgrading to Java " + dev); true
  case x if x > dev            => println("Unsupported early access version. Consider switching back to Java " + dev); true
  case _                       => true
}

javacOptions ++= Seq("-source", javaVersionString, "-target", javaVersionString, "-encoding", "utf8", "-Xlint:unchecked", "-Xlint:deprecation")
javacOptions in doc := Seq("-source", javaVersionString) 

enablePlugins(JShellPlugin)

compileOrder := CompileOrder.JavaThenScala

// Enables publishing to maven repo
publishMavenStyle := true

// Do not append Scala versions to the generated artifacts
crossPaths := false

// This forbids including Scala related libraries into the dependency
autoScalaLibrary := false

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case _ => MergeStrategy.first
}

// contains libraries provided by utu/ft dep
resolvers += "ftdev" at "https://ftdev.utu.fi/maven2"

fork in Global := true

val javaVersion = taskKey[Unit]("Prints the Java version.")

javaVersion := { println("SBT uses Java SDK located at "+System.getProperty("java.home")) }

publishTo := Some(Resolver.file("file", new File("/tmp/repository")))

val oomkit = "fi.utu.tech" % "oomkit" % "1.15"

libraryDependencies ++= Seq()

////
//// JQWIK / JUNIT configuration
////

resolvers in ThisBuild += Resolver.jcenterRepo

val junit_version = "5.5.2"

// library dependencies. (orginization name) % (project name) % (version)
libraryDependencies ++= Seq(
  "net.aichler"        % "jupiter-interface"              % JupiterKeys.jupiterVersion.value % Test,
  "org.junit.platform" % "junit-platform-commons"         % ("1"+junit_version.tail) % Test,
  "org.junit.platform" % "junit-platform-runner"          % ("1"+junit_version.tail) % Test,
  "org.junit.jupiter"  % "junit-jupiter-engine"           % junit_version % Test,
  "org.junit.jupiter"  % "junit-jupiter-api"              % junit_version % Test,
  "org.junit.jupiter"  % "junit-jupiter-migrationsupport" % junit_version % Test,
  "org.junit.jupiter"  % "junit-jupiter-params"           % junit_version % Test,
  "net.jqwik"          % "jqwik"                          % "1.2.0" % Test,
  "org.scalatest"      %% "scalatest"                     % "3.0.8" % Test,
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-q", "-c")

////
//// JAVAFX configuration
////

val javafx_versions = if (!useJavaFX) (0,"-","-") else (force_javaFxVersion getOrElse javaVersionNum) match {
  case 7 => (7, "7", "8.0.181-R13")
  case 8 => (8, "8", "8.0.181-R13")
  case 10 => (11, "11.0.2", "11-R16")
  case x if x>10 => (13, "13.0.2", "12.0.2-R18")
  case _ => fail("Unsupported Java version for JavaFX")
}

// JAVA_HOME location
val javaHomeDir = {
  val path = try {
    if (scala.sys.env("JAVA_HOME").trim.isEmpty) throw new Exception("Empty JAVA_HOME") else scala.sys.env("JAVA_HOME")
  } catch {
    case _: Throwable => System.getProperty("java.home") // not set -> ask from current JVM
  }

  val f = file(path)
  if (!f.exists()) fail("The environment variable JAVA_HOME points to a non-existent directory!\nSolution: Edit your system settings (Windows control panel / *nix .bashrc) and fix the JAVA_HOME location.")
  f
}

val osName: SettingKey[String] = SettingKey[String]("osName")

osName := (System.getProperty("os.name") match {
  case n if n.startsWith("Linux")   => "linux"
  case n if n.startsWith("Mac")     => "mac"
  case n if n.startsWith("Windows") => "win"
  case _ => throw new Exception("Unknown platform!")
})

def legacyJavaFX() = {
  val searchDirs = Seq(
    "/jre/lib/jfxrt.jar",     // OpenJDK 7
    "/jre/lib/ext/jfxrt.jar", // OpenJDK 8
    "/lib/ext/jfxrt.jar"      // Windows & Oracle Java 8
  )

  if (detectedJDK > 8) fail(s"Trying to use legacy non-modular JavaFX with a modern JDK [$detectedJDK].\nSolution: Check the line 'val force_javaFxVersion =' in build.sbt.")

  val javaFxJAR = searchDirs.map{ searchDir => file(javaHomeDir + searchDir) }.find{ _.exists() }

  javaFxJAR.getOrElse {
    fail(s"Java FX runtime not installed in [${javaHomeDir.toString}]!\nSolution: Install JavaFX or consider upgrading your JDK so that JavaFX can be installed automatically.")
  }
}

val jfx_sdk_version = javafx_versions._2
val jfx_scalafx_version = javafx_versions._3

val javaFxPath = Def.taskKey[File]("OpenJFX fetcher")
javaFxPath := {
  val javaFxHome =
    try {
      val envHome = file(scala.sys.env("JAVAFX_HOME"))
      if (envHome.toString.trim.isEmpty) throw new Exception("Empty JAVAFX_HOME")
      println("Using OpenJFX from " + envHome)
      envHome
    }
    catch { case _: Throwable =>
        println("Using local OpenJFX")
        baseDirectory.value / "openjfx"
    }

  if (!javaFxHome.exists()) java.nio.file.Files.createDirectory(javaFxHome.toPath)

  val jfx_os = osName.value match {
    case "linux" => "linux"
    case "mac"   => "osx"
    case "win"   => "windows"
  }

  val sdkURL = "http://download2.gluonhq.com/openjfx/" + jfx_sdk_version + "/openjfx-" + jfx_sdk_version + "_" + jfx_os + "-x64_bin-sdk.zip"

  try {
    val testDir = javaFxHome / "all.ok"
    if (!testDir.exists()) {
      println("Fetching OpenJFX from "+sdkURL+"..")
      IO.unzipURL(new URL(sdkURL), javaFxHome)
      java.nio.file.Files.createDirectory(testDir.toPath)
      println("Fetching OpenJFX done.")
    } else {
      println("Using OpenJFX from "+javaFxHome)
    }

    javaFxHome
  }
  catch {
    case t: Throwable => fail("Could not load OpenJFX! Reason:" + t.getMessage)
  }
}

val jfxModules = Seq("base","controls","fxml","graphics","media","swing","web")


if (!useJavaFX) Seq() else javafx_versions._1 match {
  case 7 =>
    // TODO libraryDependencies
    Seq(unmanagedJars in Compile += Attributed.blank(legacyJavaFX()))
  case 8 =>
    (if (useScalaOrScalaFX) Seq(libraryDependencies += "org.scalafx" %% "scalafx" % jfx_scalafx_version) else Seq()) ++
    Seq(unmanagedJars in Compile += Attributed.blank(legacyJavaFX()))
  case _ =>
    Seq(
      javaOptions in run ++= Seq(
        "--module-path", (javaFxPath.value / ("javafx-sdk-" + jfx_sdk_version) / "lib").toString,
        "--add-modules=" + jfxModules.map("javafx."+_).mkString(","))
    ) ++
      (if (useScalaOrScalaFX) Seq(libraryDependencies += "org.scalafx" % "scalafx_2.13" % jfx_scalafx_version) else Seq()) ++
      jfxModules.map(module => libraryDependencies += "org.openjfx" % ("javafx-"+module) % jfx_sdk_version classifier osName.value)
}
