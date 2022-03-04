package cmt.admin.command.execution

import cmt.Helpers.*
import cmt.admin.command.AdminCommand.Studentify
import cmt.core.execution.Executable
import cmt.{ProcessDSL, StudentifiedSkelFolders, toConsoleGreen}
import sbt.io.IO as sbtio
import sbt.io.syntax.*

given Executable[Studentify] with
  extension (cmd: Studentify)
    def execute(): Either[String, String] = {

      for {
        a <- exitIfGitIndexOrWorkspaceIsntClean(cmd.mainRepository.value)
        _ = println(s"Studentifying ${toConsoleGreen(cmd.mainRepository.value.getPath)} to ${toConsoleGreen(
            cmd.studentifyBaseDirectory.value.getPath)}")

        mainRepoName = cmd.mainRepository.value.getName
        tmpFolder = sbtio.createTemporaryDirectory
        cleanedMainRepo = ProcessDSL.copyCleanViaGit(cmd.mainRepository.value, tmpFolder, mainRepoName)

        ExercisesMetadata(prefix, exercises, exerciseNumbers) <- getExerciseMetadata(cmd.mainRepository.value)(
          cmd.config)
        studentifiedRootFolder = cmd.studentifyBaseDirectory.value / mainRepoName

        _ = if studentifiedRootFolder.exists && cmd.forceDeleteDestinationDirectory.value then
          sbtio.delete(studentifiedRootFolder)

        StudentifiedSkelFolders(solutionsFolder) =
          createStudentifiedFolderSkeleton(cmd.studentifyBaseDirectory.value, studentifiedRootFolder)(cmd.config)

        _ = buildStudentifiedRepository(
          cleanedMainRepo,
          exercises,
          studentifiedRootFolder,
          solutionsFolder,
          cmd,
          tmpFolder)

        result <- Right(exercises.mkString("Processed exercises:\n  ", "\n  ", "\n"))

      } yield result
    }

  private def buildStudentifiedRepository(
      cleanedMainRepo: File,
      exercises: Vector[String],
      studentifiedRootFolder: File,
      solutionsFolder: File,
      cmd: Studentify,
      tmpFolder: File): Either[String, String] =
    addFirstExercise(cleanedMainRepo, exercises.head, studentifiedRootFolder)(cmd.config)

    hideExercises(cleanedMainRepo, solutionsFolder, exercises)(cmd.config)

    writeStudentifiedCMTConfig(studentifiedRootFolder / cmd.config.cmtStudentifiedConfigFile, exercises)(cmd.config)
    writeStudentifiedCMTBookmark(studentifiedRootFolder / ".bookmark", exercises.head)

    if cmd.initializeAsGitRepo.value then
      val dotIgnoreFile = cleanedMainRepo / ".gitignore"
      if dotIgnoreFile.exists then sbtio.copyFile(dotIgnoreFile, studentifiedRootFolder / ".gitignore")
      initializeGitRepo(studentifiedRootFolder)
      commitToGit("Initial commit", studentifiedRootFolder)

    sbtio.delete(tmpFolder)
    Right(exercises.mkString("Processed exercises:\n  ", "\n  ", "\n"))
