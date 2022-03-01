package cmt

sealed trait CliCommand

trait HasConfiguration[T]:
  extension (t: T) def toConfig: CMTaConfig

object HasConfiguration:

  given HasConfiguration[Studentify] with
    extension (studentify: Studentify)
      def toConfig: CMTaConfig =
        new CMTaConfig(studentify.mainRepositoryDirectory.value, studentify.maybeConfigurationFile.map(_.value))
