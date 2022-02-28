package cmt

import cmt.Helpers.ExercisePrefixesAndExerciseNames
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.*
import sbt.io.IO as sbtio
import sbt.io.syntax.*
import com.typesafe.config.ConfigFactory

import java.util.UUID

class RenumberExercisesSpec extends AnyWordSpec with should.Matchers with BeforeAndAfterAll:

  private val testConfig =
    """cmt {
      |  main-repo-exercise-folder = code
      |  studentified-repo-solutions-folder = .cue
      |  studentified-saved-states-folder = .savedStates
      |  studentified-repo-active-exercise-folder = code
      |  linearized-repo-active-exercise-folder = code
      |  config-file-default-name = course-management.conf
      |  test-code-folders = [ "src/test" ]
      |  read-me-files = [ "README.md" ]
      |  cmt-studentified-config-file = .cmt-config
      |  cmt-studentified-dont-touch = [ ".idea", ".bsp", ".bloop" ]
      |}""".stripMargin

  private val tempDirectory = sbtio.createTemporaryDirectory

  override def afterAll(): Unit =
    tempDirectory.delete()

  "Inserting an exercise before the first exercise at index 0" should {
    "succeed if space is available in the exercise number space" in {
      val mainRepo = tempDirectory / UUID.randomUUID().toString
      sbtio.createDirectory(mainRepo)
      Helpers.dumpStringToFile(testConfig, mainRepo / "course-management.conf")
      val config: CMTaConfig = CMTaConfig(mainRepo, None)
      println(s"Temp folder = ${mainRepo.getPath}")
      val codeFolder = mainRepo / "code"
      sbtio.createDirectory(codeFolder)
      val exercises = Vector("exercise_001_desc", "exercise_002_desc", "exercise_003_desc")
      sbtio.createDirectories(exercises.map(exercise => codeFolder / exercise))
      sbtio.touch(exercises.map(exercise => codeFolder / exercise / "README.md"))
      {
        val result = CMTAdmin.renumberExercises(mainRepo, None, 20, 2)(config)
        result shouldBe Right(())
        val ExercisePrefixesAndExerciseNames(prefixes, renumberedExercises) =
          Helpers.getExercisePrefixAndExercises(mainRepo)(config)
        renumberedExercises shouldBe Vector("exercise_020_desc", "exercise_022_desc", "exercise_024_desc")
      }
      {
        val result = CMTAdmin.renumberExercises(mainRepo, None, 1, 1)(config)
        result shouldBe Right(())
        val ExercisePrefixesAndExerciseNames(prefixes, renumberedExercises) =
          Helpers.getExercisePrefixAndExercises(mainRepo)(config)
        renumberedExercises shouldBe Vector("exercise_001_desc", "exercise_002_desc", "exercise_003_desc")
      }
      {
        val result = CMTAdmin.renumberExercises(mainRepo, Some(1), 0, 1)(config)
        result shouldBe Right(())
        val ExercisePrefixesAndExerciseNames(prefixes, renumberedExercises) =
          Helpers.getExercisePrefixAndExercises(mainRepo)(config)
        renumberedExercises shouldBe Vector("exercise_000_desc", "exercise_001_desc", "exercise_002_desc")
      }
      {
        val result = CMTAdmin.renumberExercises(mainRepo, None, 1, 1)(config)
        result shouldBe Right(())
        val ExercisePrefixesAndExerciseNames(prefixes, renumberedExercises) =
          Helpers.getExercisePrefixAndExercises(mainRepo)(config)
        renumberedExercises shouldBe Vector("exercise_001_desc", "exercise_002_desc", "exercise_003_desc")
      }

    }
  }

end RenumberExercisesSpec
