import AssemblyKeys._

assemblySettings

proguardSettings

name := "lucene-test"

mainClass := Some("DumpIndex")

scalaVersion := "2.10.2"

scalacOptions += "-deprecation"

libraryDependencies ++= Seq(
  "org.neo4j" % "neo4j-kernel" % "1.9.5" exclude("org.neo4j", "neo4j-udc"),
  "org.neo4j" % "neo4j-lucene-index" % "1.9.5"
)

fork := true


ProguardKeys.options in Proguard ++= Seq("-dontnote", "-dontwarn", "-ignorewarnings", "-dontobfuscate", "-dontoptimize")

ProguardKeys.options in Proguard += ProguardOptions.keepMain("DumpIndex")

ProguardKeys.options in Proguard += ProguardOptions.keepMain("RemoveDuplicates")

//ProguardKeys.options in Proguard += "-keepclassmembers public class org.apache.lucene.** { *; }"

ProguardKeys.merge in Proguard := true

ProguardKeys.mergeStrategies in Proguard += ProguardMerge.discard("META-INF/.*txt$".r)


mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case PathList(ps @ _*) if ps.last endsWith ".txt" => MergeStrategy.concat
    case x => old(x)
  }
}
