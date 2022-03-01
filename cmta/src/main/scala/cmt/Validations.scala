package cmt

import sbt.io.syntax.File

object Validations:

  def exists[T](maybeExists: T)(using x: FileLike[T]): Either[String, Unit] =
    maybeExists.exists match
      case true  => Right(())
      case false => Left(s"${maybeExists.getPath} does not exist")

  def isADirectory[T](maybeDirectory: T)(using x: FileLike[T]): Either[String, Unit] =
    maybeDirectory.isDirectory match
      case true  => Right(())
      case false => Left(s"${maybeDirectory.getPath} is not a directory")

  def isAFile[T](maybeFile: T)(using x: FileLike[T]): Either[String, Unit] =
    maybeFile.isFile match
      case true  => Right(())
      case false => Left(s"${maybeFile.getPath} is not a file")

  def isAGitRepository(directory: File): Either[String, Unit] =
    Helpers.resolveMainRepoPath(directory) match
      case Right(_) => Right(())
      case Left(_)  => Left(s"${directory.getPath} is not a git repository")

  def existsAndIsADirectory[T](maybe: T)(using x: FileLike[T]): Either[String, Unit] =
    for {
      _ <- exists(maybe)
      _ <- isADirectory(maybe)
    } yield ()

  def existsAndIsAFile[T](maybe: T)(using x: FileLike[T]): Either[String, Unit] =
    for {
      _ <- exists(maybe)
      _ <- isAFile(maybe)
    } yield ()

  def greaterThanOrEqualToZero(num: Int): Either[String, Unit] =
    greaterThanOrEqualTo(num, target = 0)

  def greaterThanOrEqualToOne(num: Int): Either[String, Unit] =
    greaterThanOrEqualTo(num, target = 1)

  def greaterThanOrEqualTo(num: Int, target: Int): Either[String, Unit] =
    if num >= 0 then Right(())
    else Left(s"number should be >= $target")
