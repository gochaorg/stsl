package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class GenericInstanceTest {
  val listType = TObject("List")
    .generics(AnyVariant("A"))
    .fields("size" -> Type.INT)
    .methods(
      "add" -> Fn(
        Params(
          "this" -> Type.THIS,
          "item" -> TypeVariable("A",Type.THIS),
        ),
        Type.VOID
      ),
      "get" -> Fn(
        Params(
          "this" -> Type.THIS,
          "idx" -> Type.INT,
        ),
        TypeVariable("A",Type.THIS)
      ),
    )
    .build

  val userType = TObject("User")
    .fields("name" -> Type.INT)
    .build

  @Test
  def test01():Unit = {
    println("test01()")
    println("===============")

    val gi1 = GenericInstance(
      Map("A" -> userType),
      listType
    )

    println(gi1)

    val gi2 = GenericInstance(
      Map("A" -> TypeVariable("B", Type.THIS)),
      listType
    )

    println(gi2)

    val gi3 = gi2.typeVarReplace("B" -> TypeVariable("C",Type.THIS))

    println(gi3)
  }
}
