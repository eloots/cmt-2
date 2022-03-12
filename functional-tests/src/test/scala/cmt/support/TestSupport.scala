package cmt.support

import cmt.CMTaConfig
import cmt.Helpers.{commitToGit, dumpStringToFile, initializeGitRepo, setGitConfig}
import cmt.admin.Domain.MainRepository
import cmt.admin.cli.CliCommand.Studentify
import sbt.io.IO as sbtio
import sbt.io.syntax.*

import java.util.UUID
import scala.annotation.targetName

type FilePath = String
type ExerciseName = String
type SourceFileStruct = List[FilePath]
final case class SourcesStruct(test: SourceFileStruct, readme: SourceFileStruct, main: SourceFileStruct)
opaque type ExerciseMetadata = Map[ExerciseName, SourcesStruct]

extension (sfs: SourceFileStruct)
  def toSourceFiles: SourceFiles =
    sfs.map(n => (n, UUID.randomUUID())).to(Map)

object ExerciseMetadata:
  def apply(): ExerciseMetadata = Map.empty

  extension (es: ExerciseMetadata)
    def addExercise(exercise: (ExerciseName, SourcesStruct)): ExerciseMetadata = es + exercise

    def toExercises: Exercises = es.view
      .mapValues { case SourcesStruct(test, readme, main) =>
        ExercisesStruct(test = test.toSourceFiles, readme = readme.toSourceFiles, main = main.toSourceFiles)
      }
      .to(Map)

end ExerciseMetadata

type CheckSum = UUID
opaque type SourceFiles = Map[FilePath, CheckSum]
final case class ExercisesStruct(test: SourceFiles, readme: SourceFiles, main: SourceFiles)
opaque type Exercises = Map[ExerciseName, ExercisesStruct]

object Exercises:
  extension (exercises: Exercises)
    def createRepo(
        baseFolder: File,
        codeFolder: String,
        additionalFiles: Option[SourceFiles] = None): Either[String, Unit] =
      for {
        exercise <- exercises.keys
        ExercisesStruct(test, readme, main) = exercises(exercise)

      } {
        test.createFiles(baseFolder, exercise, codeFolder)
        readme.createFiles(baseFolder, exercise, codeFolder)
        main.createFiles(baseFolder, exercise, codeFolder)
      }
      initializeGitRepo(baseFolder)
      setGitConfig(baseFolder)
      commitToGit("Initial commit", baseFolder)
      Right(())

    def getMainCode(exerciseName: ExerciseName): SourceFiles =
      exercises(exerciseName).main
    def getTestCode(exerciseName: ExerciseName): SourceFiles =
      exercises(exerciseName).test
    def getReadmeCode(exerciseName: ExerciseName): SourceFiles =
      exercises(exerciseName).readme
    def getAllCode(exerciseName: ExerciseName): SourceFiles =
      getMainCode(exerciseName) ++ getTestCode(exerciseName) ++ getReadmeCode(exerciseName)

end Exercises

object SourceFiles:
  def apply(sourceFiles: Map[FilePath, UUID]): SourceFiles = sourceFiles

  extension (sf: SourceFiles)
    def createFiles(baseFolder: File, exercise: String, codeFolder: String): Unit =
      sf.foreach { case (filePath, checksum) =>
        sbtio.touch(baseFolder / codeFolder / exercise / filePath)
        dumpStringToFile(checksum.toString, baseFolder / codeFolder / exercise / filePath)
      }

    @targetName("mergeWith")
    def ++(other: SourceFiles): SourceFiles =
      sf ++ other

    @targetName("minus")
    def --(other: SourceFiles): SourceFiles =
      sf -- other.keys

    def doesNotContain(otherSourceFiles: SourceFiles): Unit =
      for {
        (filePath, _) <- otherSourceFiles
      } assert(!sf.contains(filePath), s"Actual sourceFiles shouldn't contain ${filePath}")
end SourceFiles
