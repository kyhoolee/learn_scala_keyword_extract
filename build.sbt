import sbt.Keys._
import sbtprotobuf.{ProtobufPlugin=>JPB}

name := "entity-extractor-service"

organization := "id.co.babe"

version := "0.2"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  "Twitter Maven" at "https://maven.twttr.com",
  Resolver.jcenterRepo,
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/releases/",
  Resolver.mavenLocal
)

compileOrder := CompileOrder.JavaThenScala

PB.targets in Compile := Seq(
  scalapb.gen(grpc=false, javaConversions=true) -> (sourceManaged in Compile).value
)

// assembly for packaging as single jar
assemblyMergeStrategy in assembly := {
  case "BUILD" => MergeStrategy.last
  case PathList("META-INF", "io.netty.versions.properties", xs @ _*) => MergeStrategy.last
  case PathList("com", "google", xs @ _*) => MergeStrategy.last
  case PathList("org", "apache", xs @ _*) => MergeStrategy.last
  case PathList("org", "slf4j", xs @ _*) => MergeStrategy.last
  case other => MergeStrategy.defaultMergeStrategy(other)
}

assemblyJarName in assembly := s"${name.value}-${version.value}.jar"


lazy val versions = new {
  val finatra = "2.5.0"
  val guice = "4.0"
  val logback = "1.1.+"
  val protobuf = "3.0.0"
  val jwtscala = "0.8.1"

  val scalapb = "0.5.42"

  val mockito = "1.9.5"
  val scalatest = "2.2.6"
  val specs2 = "2.3.12"

  val getquill = "0.10.0"

  val typesafeConfig = "1.3.0"
  val ficus = "1.2.6" // for scala friendly typesafe config

  val json4s = "0.1.3-SNAPSHOT"

  val accord = "0.6"
  val swagger = "0.6.0"

  val finagle_metrics     = "0.0.8"
  val metrics             = "3.1.2"
}

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "org.scala-lang.modules" % "scala-xml_2.11" % "1.0.5",
  "com.twitter" % "finatra-http_2.11" % versions.finatra,
  "ch.qos.logback" % "logback-classic" % versions.logback,
  "com.google.protobuf" % "protobuf-java" % versions.protobuf,
  "com.google.protobuf" % "protobuf-java-util" % versions.protobuf,
  "com.pauldijou" % "jwt-core_2.11" % versions.jwtscala,

  "com.twitter" % "finatra-http_2.11" % versions.finatra % Test,
  "com.twitter" % "inject-server_2.11" % versions.finatra % Test,
  "com.twitter" % "inject-app_2.11" % versions.finatra % Test,
  "com.twitter" % "inject-core_2.11" % versions.finatra % Test,
  "com.twitter" % "inject-modules_2.11" % versions.finatra % Test,
  "com.google.inject.extensions" % "guice-testlib" % versions.guice % Test,

  "com.twitter" % "finatra-http_2.11" % versions.finatra % Test classifier "tests",
  "com.twitter" % "inject-server_2.11" % versions.finatra % Test classifier "tests",
  "com.twitter" % "inject-app_2.11" % versions.finatra % Test classifier "tests",
  "com.twitter" % "inject-core_2.11" % versions.finatra % Test classifier "tests",
  "com.twitter" % "inject-modules_2.11" % versions.finatra % Test classifier "tests",

  "org.mockito" % "mockito-core" % versions.mockito % Test,
  "org.scalatest" % "scalatest_2.11" % versions.scalatest % Test,
  "org.specs2" % "specs2_2.11" % versions.specs2 % Test,

  // quill
  "io.getquill" %% "quill-finagle-mysql" % versions.getquill,

  // typesafe config
  "com.typesafe" % "config" % versions.typesafeConfig,
  "com.iheart" %% "ficus" % versions.ficus, // for scala friendly typesafe config

  "com.google.protobuf" % "protobuf-java" % versions.protobuf,
  "com.google.protobuf" % "protobuf-java-util" % versions.protobuf,

  "com.trueaccord.scalapb" %% "scalapb-json4s" % versions.json4s,
  "com.trueaccord.scalapb" %% "scalapb-runtime" % versions.scalapb % "protobuf",

  // validator
  "com.wix" %% "accord-core" % versions.accord,
  "com.github.xiaodongw" %% "swagger-finatra" % versions.swagger,

  "com.twitter" %% "finagle-serversets" % "6.39.0",

  "com.github.rlazoti" %% "finagle-metrics" % versions.finagle_metrics,
  "io.dropwizard.metrics" % "metrics-graphite" % versions.metrics

).map(_.exclude("org.slf4j", "slf4j-log4j12")).map(_.exclude("org.slf4j", "slf4j-jdk14"))

Revolver.settings

// SBT Protobuf
JPB.protobufSettings

// Uncomment this if you want your generated code to be included in main source
//javaSource in JPB.protobufConfig := (javaSource in Compile)(_ / "/java")