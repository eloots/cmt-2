package cmt.support

/** Copyright 2022 - Eric Loots - eric.loots@gmail.com / Trevor Burton-McCreadie - trevor@thinkmorestupidless.com
  *
  * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
  * the License. You may obtain a copy of the License at
  *
  * http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
  * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  *
  * See the License for the specific language governing permissions and limitations under the License.
  */

import cmt.Helpers
import sbt.io.syntax.{File, file}
import cmt.Helpers.adaptToOSSeparatorChar

trait TestDirectories {

  def baseDirectory = file(".").getAbsoluteFile.getParentFile
  def baseDirectoryGitRoot = Helpers.resolveMainRepoPath(baseDirectory).toOption.getOrElse(baseDirectory)

  def nonExistentDirectory(tempDirectory: File) = adaptToOSSeparatorChar(
    s"${tempDirectory.getAbsolutePath}/i/do/not/exist")
  val firstRealDirectory = adaptToOSSeparatorChar("./cmta/src/test/resources/i-am-a-directory")
  val secondRealDirectory = adaptToOSSeparatorChar("./cmta/src/test/resources/i-am-another-directory")
  val realFile = adaptToOSSeparatorChar("./cmta/src/test/resources/i-am-a-file.txt")

}
