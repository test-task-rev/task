mainClass in assembly := Some("play.core.server.ProdServerStart")
fullClasspath in assembly += Attributed.blank(PlayKeys.playPackageAssets.value)

assemblyMergeStrategy in assembly := {
  case "module-info.class" => MergeStrategy.discard
  case manifest if manifest.contains("MANIFEST.MF") =>
    MergeStrategy.discard
  case referenceOverrides if referenceOverrides.contains("reference-overrides.conf") =>
    MergeStrategy.concat
  case x => MergeStrategy.first
}
lazy val root = (project in file("."))
  .enablePlugins(PlayJava, LauncherJarPlugin)
  .settings(
    name := """test-revolut""",
    version := "1.0-SNAPSHOT",
    scalaVersion := "2.13.1",
    libraryDependencies ++= Seq(
      javaJpa,
      guice,
      "org.hibernate" % "hibernate-core" % "5.4.9.Final",
      "org.projectlombok" % "lombok" % "1.18.10",
      "com.h2database" % "h2" % "1.4.199",
      "org.assertj" % "assertj-core" % "3.14.0" % Test,
      "org.awaitility" % "awaitility" % "4.0.1" % Test,
      "org.mockito" % "mockito-core" % "2.10.0" % Test,
    ),
    scalacOptions ++= Seq("-encoding", "UTF-8"),
    javacOptions ++= Seq(
      "-encoding", "UTF-8",
      "-parameters",
      "-Xlint:unchecked",
      "-Xlint:deprecation",
      "-Werror"
    ),
    // Make verbose tests
    testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))
  )
PlayKeys.externalizeResourcesExcludes += baseDirectory.value / "conf" / "META-INF" / "persistence.xml"
