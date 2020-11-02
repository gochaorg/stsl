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
          Param("a", GenericVariable("A", Type.FN))
        ),
        GenericVariable("C", Type.FN)
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
        Param("a", GenericVariable("A", Type.FN))
      ),
      GenericVariable("B", Type.FN)
    )
    println(fmap)

    val fget = Fn(
      GenericParams(
        AnyVariant("X"),
        AnyVariant("Y"),
      ),
      Params(
        Param("ls", Type.VOID),
        Param("map", fmap)
      ),
      GenericVariable("Y", Type.FN)
    )
    println(fget)
  }
}
