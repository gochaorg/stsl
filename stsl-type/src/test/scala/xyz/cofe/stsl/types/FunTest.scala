package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class FunTest {
  @Test
  def unbindedGenericVariable01():Unit = {
    var catched = false
    try {
      val f1 = Fn(
        GenericParams(
          AnyVariant("A"),
          AnyVariant("B"),
        ),
        Params(
          "a" -> TypeVariable("A", Type.FN)
        ),
        TypeVariable("C", Type.FN)
      )
      println(f1)
    } catch {
      case err : TypeError =>
        println(err)
        catched = true
    }
    assert(catched)
  }

  @Test
  def unbindedGenericVariable02():Unit = {
    val fmap = Fn(
      GenericParams(
        AnyVariant("A"),
        AnyVariant("B"),
      ),
      Params(
        "a" -> TypeVariable("A", Type.FN)
      ),
      TypeVariable("B", Type.FN)
    )
    println(fmap)
    println(fmap.parameters("a").tip.asInstanceOf[TypeVariable].owner)

    var catched = false
    try {
      val fget = Fn(
        GenericParams(
          AnyVariant("X"),
          AnyVariant("Y"),
        ),
        Params(
          "ls" -> Type.VOID,
          "map" -> fmap
        ),
        TypeVariable("Y", Type.FN)
      )
      println(fget)
    } catch {
      case err: TypeError =>
        println(err)
        catched = true
    }
    assert(catched)
  }
}
