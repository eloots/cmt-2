package cmt

import cmt.domain.{ConfigurationFile, ExerciseNumber, LinearizeBaseDirectory, MainRepositoryDirectory, RenumberFrom, RenumberStep, RenumberTo, StudentifiedDirectory}
import sbt.io.syntax.file

object Readers:

  implicit val mainRepositoryDirectoryReader: scopt.Read[MainRepositoryDirectory] =
    scopt.Read.reads(path => MainRepositoryDirectory(file(path)))

  implicit val configFileReader: scopt.Read[ConfigurationFile] =
    scopt.Read.reads(path => ConfigurationFile(file(path)))

  implicit val studentifiedDirectoryReader: scopt.Read[StudentifiedDirectory] =
    scopt.Read.reads(path => StudentifiedDirectory(file(path)))

  implicit val exerciseNumberReader: scopt.Read[ExerciseNumber] =
    scopt.Read.reads(num => ExerciseNumber(num.toInt))

  implicit val linearizeBaseFolderReader: scopt.Read[LinearizeBaseDirectory] =
    scopt.Read.reads(path => LinearizeBaseDirectory(file(path)))

  implicit val renumberFromReader: scopt.Read[RenumberFrom] =
    scopt.Read.reads(num => RenumberFrom(num.toInt))

  implicit val renumberToReader: scopt.Read[RenumberTo] =
    scopt.Read.reads(num => RenumberTo(num.toInt))

  implicit val renumberStepReader: scopt.Read[RenumberStep] =
    scopt.Read.reads(num => RenumberStep(num.toInt))

