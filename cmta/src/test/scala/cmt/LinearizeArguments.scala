package cmt

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
import sbt.io.IO

class LinearizeArguments extends AnyWordSpecLike with Matchers with BeforeAndAfterAll {

  private val tempDirectory = IO.createTemporaryDirectory

  override def afterAll(): Unit =
    tempDirectory.delete()

//  private case class TestSetup[R <: RegistrationEventRepository: ClassTag](constructRepository: () => R) {
//    def identifier: String = implicitly[ClassTag[R]].runtimeClass.getSimpleName
//  }
//
//  "Linearize" when {
//
//
//  }
}
