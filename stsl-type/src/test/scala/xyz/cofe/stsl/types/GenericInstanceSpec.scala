package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import Type._
import JvmType._
import org.scalatest.flatspec.AnyFlatSpec

class GenericInstanceSpec extends AnyFlatSpec {
  val listType = TObject("List")
    .generics(AnyVariant("A"))
    .fields("size" -> INT)
    .methods(
      "add" -> Fn(
        Params(
          "this" -> THIS,
          "item" -> TypeVariable("A",THIS),
        ),
        VOID
      ),
      "get" -> Fn(
        Params(
          "this" -> THIS,
          "idx" -> INT,
        ),
        TypeVariable("A",THIS)
      ),
    )
    .build

  val userType = TObject("User")
    .fields("name" -> INT)
    .build

  "Generic экземпляр List" should "?" in {
    println("test01()")
    println("===============")
  
    val gi1 = new GenericInstance(
      Map("A" -> userType),
      listType
    )
  
    println(gi1)
  
    val gi2 = new GenericInstance(
      Map("A" -> TypeVariable("B", THIS)),
      listType
    )
  
    println(gi2)
  
    val gi3 = gi2.typeVarReplace("B" -> TypeVariable("C",THIS))
  
    println(gi3)
  }
}
