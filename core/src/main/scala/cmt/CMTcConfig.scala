package cmt

import sbt.io.IO as sbtio
import sbt.io.syntax.*
import com.typesafe.config.{Config, ConfigFactory}

import scala.jdk.CollectionConverters.*
import java.nio.charset.StandardCharsets
import Helpers.*

class CMTcConfig(studentifiedRepo: File):
  val bookmarkFile: File = studentifiedRepo / ".bookmark"

  private val cmtConfigFile = studentifiedRepo / ".cmt-config"
  if !cmtConfigFile.exists then printErrorAndExit("missing CMT configuration file")

  val cmtSettings: Config = ConfigFactory.parseFile(cmtConfigFile)

  val exercises: collection.mutable.Seq[String] = cmtSettings.getStringList("exercises").asScala

  val dontTouch: Set[String] =
    cmtSettings.getStringList("cmt-studentified-dont-touch").asScala.toSet

  val testCodeFolders: Set[String] =
    cmtSettings.getStringList("test-code-folders").asScala.toSet

  val readMeFiles: Set[String] = cmtSettings.getStringList("read-me-files").asScala.toSet

  val activeExerciseFolder: File =
    studentifiedRepo / cmtSettings.getString("active-exercise-folder")

  val solutionsFolder: File = studentifiedRepo / cmtSettings.getString("studentified-repo-solutions-folder")
  val studentifiedSavedStatesFolder: File =
    solutionsFolder / cmtSettings.getString("studentified-saved-states-folder")

  val nextExercise: Map[String, String] = exercises.zip(exercises.tail).to(Map)

  val previousExercise: Map[String, String] =
    exercises.tail.zip(exercises).to(Map)

end CMTcConfig
