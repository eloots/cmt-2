package cmt

import cmt.domain.{ConfigurationFile, LinearizeBaseDirectory, MainRepositoryDirectory, StudentifiedDirectory}

trait FileLike[T]:
  extension (t: T)
    def exists: Boolean
    def isFile: Boolean
    def isDirectory: Boolean
    def getPath: String

object FileLike {
  def apply[T](using fl: FileLike[T]) = fl

  given FileLike[MainRepositoryDirectory] with
    extension (directory: MainRepositoryDirectory)
      def exists: Boolean = directory.value.exists
      def isFile: Boolean = directory.value.isFile
      def isDirectory: Boolean = directory.value.isDirectory
      def getPath: String = directory.value.getPath

  given FileLike[StudentifiedDirectory] with
    extension (directory: StudentifiedDirectory)
      def exists: Boolean = directory.value.exists
      def isFile: Boolean = directory.value.isFile
      def isDirectory: Boolean = directory.value.isDirectory
      def getPath: String = directory.value.getPath

  given FileLike[ConfigurationFile] with
    extension (file: ConfigurationFile)
      def exists: Boolean = file.value.exists
      def isFile: Boolean = file.value.isFile
      def isDirectory: Boolean = file.value.isDirectory
      def getPath: String = file.value.getPath

  given FileLike[LinearizeBaseDirectory] with
    extension (directory: LinearizeBaseDirectory)
      def exists: Boolean = directory.value.exists
      def isFile: Boolean = directory.value.isFile
      def isDirectory: Boolean = directory.value.isDirectory
      def getPath: String = directory.value.getPath
}
