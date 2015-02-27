version in Railo := "4.5.0.042"

resolvers ++= Seq(
  "http://cfmlprojects.org/artifacts/" at "http://cfmlprojects.org/artifacts/",
  "Rhinofly public Release Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-release-local",
  "Rhinofly public Snapshot Repository" at "http://maven-repository.rhinofly.net:8081/artifactory/libs-snapshot-local"
)

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

name := "freemarker-helpers"

name in Railo := "freemarker"

organization := "nl.rhinofly"

scalaVersion := "2.11.5"

crossScalaVersions := Seq("2.10.4", scalaVersion.value)

//def luceeDependency(name: String) = 
  // matches all versions greater or equal to 4.5 and lower than 4.6
  //"org.lucee" % name % "[4.5.0.000,4.6.0.000[" % "provided" intransitive()

libraryDependencies ++= Seq(
   //"javax.servlet" % "javax.servlet-api" % "3.0.1",
   //"org.eclipse.jetty" % "jetty-servlet" % "9.0.3.v20130506",
   //"org.eclipse.jetty" % "jetty-webapp" % "9.0.3.v20130506",
   //luceeDependency("lucee"),
   //luceeDependency("lucee.core").copy(explicitArtifacts = Seq(Artifact("lucee.core", "jar", "lco"))),
   //"org.mortbay.jetty" % "jsp-2.1-glassfish" % "2.1.v20100127",
   //"org.fusesource.jansi" % "jansi" % "1.11",
   "org.freemarker" % "freemarker" % "2.3.20",
   //"org.specs2" %% "specs2" % "2.3.12" % "test",
  //"org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.qirx" %% "little-spec" % "0.3" % "test"
)

testFrameworks += new TestFramework("org.qirx.littlespec.sbt.TestFramework")

lazy val `freemarker-helpers` = project.in( file(".") ).enablePlugins(RailoLibrary)

//classpathTypes += "lco"

publishTo <<= version { (v: String) =>
  val rhinofly = "http://maven-repository.rhinofly.net:8081/artifactory/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at rhinofly + "libs-snapshot-local")
  else
    Some("releases"  at rhinofly + "libs-release-local")
}

releaseSettings

ReleaseKeys.crossBuild := true
