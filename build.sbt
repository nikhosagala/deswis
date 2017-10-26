import com.typesafe.sbt.SbtNativePackager.NativePackagerKeys._

name := """deswis"""

version := "0.1"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.8"

maintainer := "Nikho Sagala"

dockerExposedPorts in Docker := Seq(9000, 9443)

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.wordnik" %% "swagger-play2" % "1.3.12" exclude("org.reflections", "reflections"),
  "org.apache.poi" % "poi" % "3.15",
  "org.apache.poi" % "poi-ooxml" % "3.15",
  "org.reflections" % "reflections" % "0.9.8" notTransitive(),
  "org.webjars" % "swagger-ui" % "2.1.8-M1",
  "commons-io" % "commons-io" % "2.4",
  "net.sourceforge.owlapi" % "owlapi-api" % "5.1.0",
  "net.sourceforge.owlapi" % "owlapi-distribution" % "5.1.0",
  "net.sourceforge.owlapi" % "owlapi-parsers" % "5.1.0",
  "org.apache.jena" % "jena-core" % "3.2.0",
  "org.apache.jena" % "jena-arq" % "3.2.0",
  "com.google.maps" % "google-maps-services" % "0.1.20",
  "org.jetbrains" % "annotations" % "13.0",
  "org.eclipse.mylyn.github" % "org.eclipse.egit.github.core" % "2.1.5"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

libraryDependencies += filters

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

dockerBaseImage := "openjdk:latest"

val Success = 0 // 0 exit code
val Error = 1 // 1 exit code

PlayKeys.playRunHooks += baseDirectory.map(UIBuild.apply).value

val isWindows = System.getProperty("os.name").toLowerCase().contains("win")

def runScript(script: String)(implicit dir: File): Int = {
  if (isWindows) {
    Process("cmd /c " + script, dir)
  } else {
    Process(script, dir)
  }
} !

def uiWasInstalled(implicit dir: File): Boolean = (dir / "node_modules").exists()

def runNpmInstall(implicit dir: File): Int =
  if (uiWasInstalled) Success else runScript("npm install")

def ifUiInstalled(task: => Int)(implicit dir: File): Int =
  if (runNpmInstall == Success) task
  else Error

def runProdBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build-prod"))

def runDevBuild(implicit dir: File): Int = ifUiInstalled(runScript("npm run build"))

lazy val `ui-dev-build` = TaskKey[Unit]("Run UI build when developing the application.")

`ui-dev-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runDevBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

lazy val `ui-prod-build` = TaskKey[Unit]("Run UI build when packaging the application.")

`ui-prod-build` := {
  implicit val UIroot = baseDirectory.value / "ui"
  if (runProdBuild != Success) throw new Exception("Oops! UI Build crashed.")
}

dist := (dist dependsOn `ui-prod-build`).value

stage := (stage dependsOn `ui-prod-build`).value
