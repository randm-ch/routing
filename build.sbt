import org.http4s.routing.Build._

Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val http4sRouting = project.in(file("."))
  .settings(commonSettings)
  .settings(publishSettings)
  .settings(testSettings)
  .settings(libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value,
    catsCore,
    http4sCore,
    http4sDsl
  ))
  .settings(Seq(name := "http4s-routing"))

lazy val docs = project.in(file("http4s-routing-docs"))
  .settings(
    moduleName := "http4s-routing-docs",
    mdocExtraArguments += "--no-link-hygiene",
    mdocVariables := Map(
      "VERSION" -> (http4sRouting / version).value,
      "GITHUB_REPO_URL" -> githubRepoUrl,
      "GITHUB_SRC_URL" -> githubSrcUrl,
      "GITHUB_ROUTING_URL" -> s"$githubSrcUrl/org/http4s/routing"
    )
  )
  .dependsOn(http4sRouting)
  .enablePlugins(MdocPlugin, DocusaurusPlugin)

lazy val githubRepoUrl = "https://github.com/mblink/http4s-routing"
lazy val githubSrcUrl = s"$githubRepoUrl/blob/master/src/main/scala"
