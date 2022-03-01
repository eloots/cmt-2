package cmt

import cmt.FileLike.given
import cmt.Validations.*
import cmt.domain.*

trait Validatable[T]:
  extension (t: T) def validate: Either[String, Unit]

object Validatable:

  given Validatable[MainRepositoryDirectory] with
    extension (directory: MainRepositoryDirectory)
      def validate: Either[String, Unit] =
        for {
          _ <- existsAndIsADirectory(directory)
          _ <- isAGitRepository(directory.value)
        } yield ()

  given Validatable[StudentifiedDirectory] with
    extension (directory: StudentifiedDirectory)
      def validate: Either[String, Unit] =
        existsAndIsADirectory(directory)

  given Validatable[ConfigurationFile] with
    extension (directory: ConfigurationFile)
      def validate: Either[String, Unit] =
        existsAndIsAFile(directory)

  given Validatable[LinearizeBaseDirectory] with
    extension (directory: LinearizeBaseDirectory)
      def validate: Either[String, Unit] =
        existsAndIsADirectory(directory)

  given Validatable[RenumberFrom] with
    extension (renumberFrom: RenumberFrom)
      def validate: Either[String, Unit] =
        greaterThanOrEqualToZero(renumberFrom.value)

  given Validatable[RenumberTo] with
    extension (renumberTo: RenumberTo)
      def validate: Either[String, Unit] =
        greaterThanOrEqualToOne(renumberTo.value)

  given Validatable[RenumberStep] with
    extension (renumberStep: RenumberStep)
      def validate: Either[String, Unit] =
        greaterThanOrEqualToOne(renumberStep.value)

  given Validatable[Studentify] with
    extension (studentify: Studentify)
      def validate: Either[String, Unit] = {
        Right(())
      }
