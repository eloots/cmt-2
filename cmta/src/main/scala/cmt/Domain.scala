package cmt

import sbt.io.syntax.File
import sbt.io.syntax.file

object domain {

  final case class MainRepositoryDirectory(value: File)
  object MainRepositoryDirectory:
    def default(): MainRepositoryDirectory =
      MainRepositoryDirectory(file("."))

  final case class ConfigurationFile(value: File)
  final case class StudentifiedDirectory(value: File)
  final case class ExerciseNumber(value: Int)
  final case class LinearizeBaseDirectory(value: File)
  final case class RenumberFrom(value: Int)
  final case class RenumberTo(value: Int)
  final case class RenumberStep(value: Int)

  enum DeleteExistingDirectoryDecision:
    case DeleteExistingDirectory, DoNotDeleteExistingDirectory

  enum InitializeAsGitRepositoryDecision:
    case InitializeAsGitRepository, DoNotInitializeAsGitRepository
}
