package cmt

import scala.sys.process.Process
import scala.util.{Failure, Success, Try}

import sbt.io.syntax.*

object ProcessDSL:
  final case class CmdWithsWorkingDir(cmd: String, workingDir: String)

  final case class ProcessCmd(cmd: Seq[String], workingDir: File)

  extension (cmd: ProcessCmd)
    // TODO: Not used anymore - shall we remove or keep it?
//    def runAndExitIfFailed(errorMsg: String): Unit =
//      val status = Try(Process(cmd.cmd, cmd.workingDir).!)
//      if status.getOrElse(-1) != 0 then
//        System.err.println(s"""
//                   |$errorMsg
//                   |  Executed command: ${cmd.cmd.mkString(" ")}
//                   |  Working directory: ${cmd.workingDir}
//           """.stripMargin)
//        System.exit(status.getOrElse(-1))
//
//    def run: Int = {
//      val status = Try(Process(cmd.cmd, cmd.workingDir).!)
//      status.getOrElse(-1)
//    }

    def runWithStatus(msg: String): Either[String, Unit] = {
      val status = Try(Process(cmd.cmd, cmd.workingDir).!)
      status match
        case Success(_)  => Right(())
        case Failure(ex) => Left(msg)
    }

    def runAndReadOutput(): Either[String, String] =
      val consoleRes = Try(Process(cmd.cmd, cmd.workingDir).!!)
      consoleRes match
        case Success(result) => Right(result.trim)
        case Failure(_) =>
          val msg = s"""
                       |  Executed command: ${cmd.cmd.mkString(" ")}
                       |  Working directory: ${cmd.workingDir}
          """.stripMargin
          Left(msg)
  end extension

  extension (command: String)
    def toProcessCmd(workingDir: File): ProcessCmd =
      val SplitRegex = "([^\"]\\S*|\".+?\")\\s*".r
      val cmdArgs =
        SplitRegex.findAllMatchIn(command).map(_.toString.replaceAll(" $", "").replaceAll(""""""", "")).toVector
      ProcessCmd(cmdArgs, workingDir)

end ProcessDSL
