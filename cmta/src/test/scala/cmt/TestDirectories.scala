package cmt

import sbt.io.syntax.File

object TestDirectories {

  def nonExistentDirectory(tempDirectory: File) = s"${tempDirectory.getAbsolutePath}/i/do/not/exist"
  val firstRealDirectory = "./cmta/src/test/resources/i-am-a-directory"
  val secondRealDirectory = "./cmta/src/test/resources/i-am-another-directory"
  val realFile = "./cmta/src/test/resources/i-am-a-file.txt"
}
