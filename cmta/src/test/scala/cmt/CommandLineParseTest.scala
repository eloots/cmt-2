package cmt

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import scopt.OEffect.ReportError

import java.io.File

class CommandLineParseTest extends AnyWordSpecLike with Matchers {

  "commmand line parser" when {

    "given the 'studentify' command" should {

      "fail if main repository argument and studentified directories are missing" in {
        val args = Array("studentify")
        val resultOr = CmdLineParse.parse(args)

        val error = assertLeft(resultOr)
        (error.errors should contain).allOf(
          ReportError("Missing argument <Main repo>"),
          ReportError("Missing argument <studentified repo parent folder>"))
      }

      "fail if main repository and studentified directories don't exist" in {
        val args = Array("studentify", "/i/do/not/exist", "neither/do/i")
        val resultOr = CmdLineParse.parse(args)

        val error = assertLeft(resultOr)
        (error.errors should contain)
          .allOf(ReportError("/i/do/not/exist does not exist"), ReportError("neither/do/i does not exist"))
      }

      "fail if main repository and studentified directories are files" in {
        val file1 = "./cmta/src/test/resources/i-am-a-file.txt"
        val file2 = "./cmta/src/test/resources/i-am-a-directory/i-am-a-file-in-a-directory.txt"
        val args = Array("studentify", file1, file2)
        val resultOr = CmdLineParse.parse(args)

        val error = assertLeft(resultOr)
        (error.errors should contain)
          .allOf(ReportError(s"$file1 is not a directory"), ReportError(s"$file2 is not a directory"))
      }

      "succeed if main repository and studentified directories exist and are directories" in {
        val directory1 = "./cmta/src/test/resources/i-am-a-directory"
        val directory2 = "./cmta/src/test/resources/i-am-another-directory"
        val args = Array("studentify", directory1, directory2)
        val resultOr = CmdLineParse.parse(args)

        val result = assertRight(resultOr)
        val expectedResult = CmtaOptions(
          new File(directory1),
          Studentify(
            Some(new File(directory2)),
            forceDeleteExistingDestinationFolder = false,
            initializeAsGitRepo = false))

        result shouldBe expectedResult
      }
    }
  }

  private def assertRight[E, T](either: Either[E, T]): T =
    either match {
      case Left(error)   => throw new IllegalStateException(s"Expected Either.right, got Either.left [$error]")
      case Right(result) => result
    }

  private def assertLeft[E, T](either: Either[E, T]): E =
    either match {
      case Left(error)   => error
      case Right(result) => throw new IllegalStateException(s"Expected Either.left, got Either.right [$result]")
    }
}
