package cmt

import cmt.TestDirectories.*
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.prop.Tables
import org.scalatest.wordspec.AnyWordSpecLike
import sbt.io.IO
import sbt.io.syntax.{File, file}
import scopt.OEffect.ReportError

object StudentifyArguments extends CommandLineArguments with Tables {

  val identifier = "studentify"

  def invalidArguments(tempDirectory: File) = Table(
    ("args", "expectedErrors"),
    (
      Seq(identifier),
      Seq(
        ReportError("Missing argument <Main repo>"),
        ReportError("Missing argument <studentified repo parent folder>"))),
    (
      Seq(identifier, nonExistentDirectory(tempDirectory), nonExistentDirectory(tempDirectory)),
      Seq(
        ReportError(s"${nonExistentDirectory(tempDirectory)} does not exist"),
        ReportError(s"${nonExistentDirectory(tempDirectory)} is not a git repository"),
        ReportError(s"${nonExistentDirectory(tempDirectory)} does not exist"))),
    (
      Seq(identifier, realFile, nonExistentDirectory(tempDirectory)),
      Seq(
        ReportError(s"$realFile is not a directory"),
        ReportError(s"$realFile is not a git repository"),
        ReportError(s"${nonExistentDirectory(tempDirectory)} does not exist"))),
    (
      Seq(identifier, tempDirectory.getAbsolutePath, secondRealDirectory),
      Seq(ReportError(s"${tempDirectory.getAbsolutePath} is not a git repository"))))

  def validArguments(tempDirectory: File) = Table(
    ("args", "expectedResult"),
    (
      Seq(identifier, firstRealDirectory, secondRealDirectory),
      CmtaOptions(
        Helpers.resolveMainRepoPath(file(firstRealDirectory)).toOption.get,
        Studentify(
          Some(file(secondRealDirectory)),
          forceDeleteExistingDestinationFolder = false,
          initializeAsGitRepo = false))))
}