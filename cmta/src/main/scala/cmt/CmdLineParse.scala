package cmt

import scopt.{OEffect, OParser}

object CmdLineParse:

  final case class CmdLineParseError(errors: List[OEffect])

  def parse(args: Array[String]): Either[CmdLineParseError, CmtaCommands] =
    OParser.runParser(cmtaParser, args, Missing) match {
      case (result, effects) => handleParsingResult(result, effects)
    }

  private def handleParsingResult(
      maybeResult: Option[CmtaCommands],
      effects: List[OEffect]): Either[CmdLineParseError, CmtaCommands] =
    maybeResult match {
      case Some(validOptions) => Right(validOptions)
      case _                  => Left(CmdLineParseError(effects))
    }
