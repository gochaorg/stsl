package xyz.cofe.stsl.types

class TypeDescTest {
  val userType = TObject("User")
    .fields("name" -> Type.NUMBER)
    .build

  //val x: (((Int, Int), Int), Int) = 1 -> 2 -> 3 -> 4
}
