package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class TypeDescTest {
  val userType = TObject("User")
    .fields("name" -> Type.NUMBER)
    .build

  //val x: (((Int, Int), Int), Int) = 1 -> 2 -> 3 -> 4

  @Test
  def test01():Unit = {
    println( TypeDescriber.describe(userType) )
  }

  val list = TObject("List")
    .generics(AnyVariant("A"))
    .fields("size" -> Type.INT)
}
