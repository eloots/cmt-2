package cmt

import scopt.OEffect.ReportError
import cmt.FileLike.given
import cmt.domain.{DeleteExistingDirectoryDecision, InitializeAsGitRepositoryDecision}

object Main:

  def main(args: Array[String]): Unit =
    CmdLineParse.parse(args) match {
      case Right(options) =>
        options match
          case validCommand: ValidCommand => selectAndExecuteCommand(validCommand)
          case invalidCommand => printError(s"expected a valid command but received '$invalidCommand'")

      case Left(CmdLineParse.CmdLineParseError(x)) =>
        printError(x.collect { case ReportError(msg) => msg }.mkString("\n"))
    }

  private def selectAndExecuteCommand(options: ValidCommand): Unit = {
    val config: CMTaConfig = options.toConfig
    given CMTaConfig = config

    options match {
      case Studentify(mainRepo, Some(stuBase), forceDeleteDestinationDirectory, initializeAsGitRepo, _) =>
        CMTStudentify.studentify(
          mainRepo.value,
          stuBase.value,
          forceDeleteDestinationDirectory == DeleteExistingDirectoryDecision.DeleteExistingDirectory,
          initializeAsGitRepo == InitializeAsGitRepositoryDecision.InitializeAsGitRepository)(config)

      case RenumberExercises(mainRepo, renumFromOpt, renumTo, renumBy, _) =>
        val message = renumFromOpt match {
          case Some(renumFrom) =>
            s"Renumbered exercises in ${mainRepo.getPath} from ${renumFrom} to ${renumTo} by ${renumBy}"
          case None => s"Renumbered exercises in ${mainRepo.getPath} to ${renumTo} by ${renumBy}"
        }
        CMTAdmin.renumberExercises(mainRepo.value, renumFromOpt.map(_.value), renumTo.value, renumBy.value)(config).printResultOrError(message)

      case DuplicateInsertBefore(mainRepo, exerciseNumber, _) =>
        CMTAdmin
          .duplicateInsertBefore(mainRepo.value, exerciseNumber.value)(config)
          .printResultOrError(s"Duplicated and inserted exercise $exerciseNumber")

      case Linearize(mainRepo, Some(linBase), forceDeleteDestinationDirectory, _) =>
        CMTLinearize.linearize(
          mainRepo.value,
          linBase.value,
          forceDeleteDestinationDirectory == DeleteExistingDirectoryDecision.DeleteExistingDirectory)(config)

      case Delinearize(mainRepo, Some(linBase), _) =>
        CMTDeLinearize.delinearize(mainRepo.value, linBase.value)(config)

      case _ =>
    }
  }

  extension (result: Either[String, Unit])
    def printResultOrError(message: String): Unit =
      result match
        case Left(errorMessage) =>
          System.err.println(toConsoleRed(s"Error: $errorMessage"))
          System.exit(1)
        case Right(_) =>
          printMessage(message)
