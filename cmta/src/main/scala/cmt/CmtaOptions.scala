package cmt

import cmt.Readers.*
import cmt.Validatable.given
import cmt.domain.*
import sbt.io.syntax.*
import scopt.{OParser, OParserBuilder}

sealed trait CmtaCommands

case object Missing extends CmtaCommands

sealed trait ValidCommand extends CmtaCommands {
  val mainRepositoryDirectory: MainRepositoryDirectory
  val maybeConfigurationFile: Option[ConfigurationFile]

  def toConfig: CMTaConfig =
    new CMTaConfig(mainRepositoryDirectory.value, maybeConfigurationFile.map(_.value))

  def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand
  def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand
}

final case class RenumberExercises(
    mainRepositoryDirectory: MainRepositoryDirectory,
    maybeRenumberFrom: Option[RenumberFrom] = None,
    renumberTo: RenumberTo,
    renumberStep: RenumberStep,
    maybeConfigurationFile: Option[ConfigurationFile])
    extends ValidCommand {
  override def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand =
    copy(mainRepositoryDirectory = mainRepositoryDirectory)

  override def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand =
    copy(maybeConfigurationFile = maybeConfigurationFile)
}
object RenumberExercises:
  def default(
      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory.default(),
      maybeRenumberFrom: Option[RenumberFrom] = None,
      renumberTo: RenumberTo = RenumberTo(1),
      renumberStep: RenumberStep = RenumberStep(1),
      maybeConfigurationFile: Option[ConfigurationFile] = None): RenumberExercises =
    RenumberExercises(mainRepositoryDirectory, maybeRenumberFrom, renumberTo, renumberStep, maybeConfigurationFile)

final case class DuplicateInsertBefore(
    mainRepositoryDirectory: MainRepositoryDirectory,
    exerciseNumber: ExerciseNumber,
    maybeConfigurationFile: Option[ConfigurationFile])
    extends ValidCommand {
  override def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand =
    copy(mainRepositoryDirectory = mainRepositoryDirectory)

  override def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand =
    copy(maybeConfigurationFile = maybeConfigurationFile)
}
object DuplicateInsertBefore:
  def default(
      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory.default(),
      exerciseNumber: ExerciseNumber = ExerciseNumber(0),
      maybeConfigurationFile: Option[ConfigurationFile] = None): DuplicateInsertBefore =
    DuplicateInsertBefore(mainRepositoryDirectory, exerciseNumber, maybeConfigurationFile)

final case class Studentify(
    mainRepositoryDirectory: MainRepositoryDirectory,
    maybeStudentifiedDirectory: Option[StudentifiedDirectory],
    forceDeleteDestinationDirectory: DeleteExistingDirectoryDecision,
    initializeAsGitRepo: InitializeAsGitRepositoryDecision,
    maybeConfigurationFile: Option[ConfigurationFile])
    extends ValidCommand {
  override def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand =
    copy(mainRepositoryDirectory = mainRepositoryDirectory)

  override def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand =
    copy(maybeConfigurationFile = maybeConfigurationFile)
}
object Studentify:
  def default(
      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory.default(),
      maybeStudentifiedDirectory: Option[StudentifiedDirectory] = None,
      forceDeleteDestinationDirectory: DeleteExistingDirectoryDecision =
        DeleteExistingDirectoryDecision.DoNotDeleteExistingDirectory,
      initializeAsGitRepo: InitializeAsGitRepositoryDecision =
        InitializeAsGitRepositoryDecision.DoNotInitializeAsGitRepository,
      maybeConfigurationFile: Option[ConfigurationFile] = None): Studentify =
    Studentify(
      mainRepositoryDirectory,
      maybeStudentifiedDirectory,
      forceDeleteDestinationDirectory,
      initializeAsGitRepo,
      maybeConfigurationFile)

final case class Linearize(
    mainRepositoryDirectory: MainRepositoryDirectory,
    maybeLinearizeBaseDirectory: Option[LinearizeBaseDirectory],
    forceDeleteDestinationDirectory: DeleteExistingDirectoryDecision,
    maybeConfigurationFile: Option[ConfigurationFile])
    extends ValidCommand {
  override def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand =
    copy(mainRepositoryDirectory = mainRepositoryDirectory)

  override def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand =
    copy(maybeConfigurationFile = maybeConfigurationFile)
}
object Linearize:
  def default(
      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory.default(),
      maybeLinearizeBaseDirectory: Option[LinearizeBaseDirectory] = None,
      forceDeleteDestinationDirectory: DeleteExistingDirectoryDecision =
        DeleteExistingDirectoryDecision.DoNotDeleteExistingDirectory,
      maybeConfigurationFile: Option[ConfigurationFile] = None): Linearize =
    Linearize(
      mainRepositoryDirectory,
      maybeLinearizeBaseDirectory,
      forceDeleteDestinationDirectory,
      maybeConfigurationFile)

final case class Delinearize(
    mainRepositoryDirectory: MainRepositoryDirectory,
    maybeLinearizeBaseDirectory: Option[LinearizeBaseDirectory],
    maybeConfigurationFile: Option[ConfigurationFile])
    extends ValidCommand {
  override def withMainRepositoryDirectory(mainRepositoryDirectory: MainRepositoryDirectory): ValidCommand =
    copy(mainRepositoryDirectory = mainRepositoryDirectory)

  override def withMaybeConfigurationFile(maybeConfigurationFile: Option[ConfigurationFile]): ValidCommand =
    copy(maybeConfigurationFile = maybeConfigurationFile)
}
object Delinearize:
  def default(
      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory.default(),
      maybeLinearizeBaseDirectory: Option[LinearizeBaseDirectory] = None,
      maybeConfigurationFile: Option[ConfigurationFile] = None): Delinearize =
    Delinearize(mainRepositoryDirectory, maybeLinearizeBaseDirectory, maybeConfigurationFile)

//final case class CmtaOptions(
//    command: CmtaCommands,
//    mainRepositoryDirectory: MainRepositoryDirectory,
//    maybeConfigurationFile: Option[ConfigurationFile])
//object CmtaOptions:
//  def default(
//      command: CmtaCommands = Missing,
//      mainRepositoryDirectory: MainRepositoryDirectory = MainRepositoryDirectory(file(".")),
//      maybeConfigurationFile: Option[ConfigurationFile] = None): CmtaOptions =
//    CmtaOptions(command, mainRepositoryDirectory, maybeConfigurationFile)

val cmtaParser = {
  given builder: OParserBuilder[CmtaCommands] = OParser.builder[CmtaCommands]
  import builder.*

  OParser.sequence(
    programName("cmta"),
    renumCmdParser,
    duplicateInsertBeforeParser,
    studentifyCmdParser,
    linearizeCmdParser,
    delinearizeCmdParser,
    configFileParser,
    validateConfig)
}

private def mainRepoArgument(using
    builder: OParserBuilder[CmtaCommands]): OParser[MainRepositoryDirectory, CmtaCommands] =
  import builder.*
  arg[MainRepositoryDirectory]("<Main repo>")
    .text("Root folder (or a subfolder thereof) the main repository")
    .validate(_.validate)
    .throwOrAction { case (mainRepositoryDirectory, command: ValidCommand) =>
      val mainRepositoryRoot = Helpers.resolveMainRepoPath(mainRepositoryDirectory.value) match {
        case Right(root) => root
        case Left(error) =>
          cmt.printError(error)
          mainRepositoryDirectory.value
      }
      command.withMainRepositoryDirectory(MainRepositoryDirectory(mainRepositoryRoot))
    }

private def configFileParser(using builder: OParserBuilder[CmtaCommands]): OParser[ConfigurationFile, CmtaCommands] =
  import builder.*
  opt[ConfigurationFile]("configuration").abbr("cfg").text("CMT configuration file").throwOrAction {
    case (configurationFile, command: ValidCommand) =>
      command.withMaybeConfigurationFile(maybeConfigurationFile = Some(configurationFile))
  }

private def duplicateInsertBeforeParser(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  cmd("dib")
    .text("Duplicate exercise and insert before")
    .action((_, _) => DuplicateInsertBefore.default())
    .children(
      mainRepoArgument,
      opt[ExerciseNumber]("exercise-number").required().text("exercise number to duplicate").abbr("n").throwOrAction {
        case (exerciseNumber, command: DuplicateInsertBefore) =>
          command.copy(exerciseNumber = exerciseNumber)
      })

private def linearizeCmdParser(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  cmd("linearize")
    .text("Generate a linearized repository from a given main repository")
    .action { (_, c) => Linearize.default() }
    .children(
      mainRepoArgument,
      arg[LinearizeBaseDirectory]("linearized repo parent folder")
        .text("Folder in which the linearized repository will be created")
        .validate(_.validate)
        .throwOrAction { case (linearizeBaseDirectory, command: Linearize) =>
          command.copy(maybeLinearizeBaseDirectory = Some(linearizeBaseDirectory))
        },
      opt[Unit]("force-delete").text("Force-delete a pre-existing destination folder").abbr("f").throwOrAction {
        case (_, command: Linearize) =>
          command.copy(forceDeleteDestinationDirectory = DeleteExistingDirectoryDecision.DoNotDeleteExistingDirectory)
      })

private def delinearizeCmdParser(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  cmd("delinearize")
    .text("De-linearize a linearized repository to its corresponding main repository")
    .action { (_, c) => Delinearize.default() }
    .children(
      mainRepoArgument,
      arg[LinearizeBaseDirectory]("linearized repo parent folder")
        .text("Folder holding the linearized repository")
        .validate(_.validate)
        .throwOrAction { case (linearizeBaseDirectory, command: Delinearize) =>
          command.copy(maybeLinearizeBaseDirectory = Some(linearizeBaseDirectory))
        })

private def studentifyCmdParser(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  cmd("studentify")
    .text("Generate a studentified repository from a given main repository")
    .action { (_, args) => Studentify.default() }
    .children(
      mainRepoArgument,
      arg[StudentifiedDirectory]("<studentified repo parent folder>")
        .text("Folder in which the studentified repository will be created")
        .validate(_.validate)
        .throwOrAction { case (studentifiedDirectory, command: Studentify) =>
          command.copy(maybeStudentifiedDirectory = Some(studentifiedDirectory))
        },
      opt[Unit]("force-delete").text("Force-delete a pre-existing destination folder").abbr("f").throwOrAction {
        case (_, command: Studentify) =>
          command.copy(forceDeleteDestinationDirectory = DeleteExistingDirectoryDecision.DeleteExistingDirectory)
      },
      opt[Unit]("init-git").text("Initialize studentified repo as a git repo").abbr("g").throwOrAction {
        case (_, command: Studentify) =>
          command.copy(initializeAsGitRepo = InitializeAsGitRepositoryDecision.InitializeAsGitRepository)
      })

private def renumCmdParser(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  cmd("renum")
    .text("Renumber exercises starting at a given offset and increment by a given step size")
    .action { (_, c) => RenumberExercises.default() }
    .children(
      mainRepoArgument,
      opt[RenumberFrom]("renumber-from")
        .text("Start renumbering from exercise number #")
        .abbr("from")
        .validate(_.validate)
        .throwOrAction { case (renumberFrom, command: RenumberExercises) =>
          command.copy(maybeRenumberFrom = Some(renumberFrom))
        },
      opt[RenumberTo]("renumber-to")
        .text("Renumber start offset (default=1)")
        .abbr("to")
        .validate(_.validate)
        .throwOrAction { case (renumberTo, command: RenumberExercises) =>
          command.copy(renumberTo = renumberTo)
        },
      opt[RenumberStep]("renumber-step")
        .text("Renumber step size (default=1)")
        .abbr("step")
        .validate(_.validate)
        .throwOrAction { case (renumberStep, command: RenumberExercises) =>
          command.copy(renumberStep = renumberStep)
        })

extension [T](parser: OParser[T, CmtaCommands])
  def throwOrAction(pf: PartialFunction[(T, CmtaCommands), CmtaCommands]): OParser[T, CmtaCommands] =
    parser.action((argOpt, command) =>
      pf.lift((argOpt, command))
        .getOrElse(
          throw new IllegalStateException(s"Received an unexpected command type '${command.getClass.getName}'")))

private def validateConfig(using builder: OParserBuilder[CmtaCommands]): OParser[Unit, CmtaCommands] =
  import builder.*
  checkConfig(command =>
    command match
      case Missing => failure("missing command")
      case _       => success
  )
