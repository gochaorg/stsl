package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.types.JvmType.BOOLEAN

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

  @Test
  def assignableFun01():Unit = {
    import JvmType._

    println("assignableFun01")
    println("="*40)

    val f1 = Fn(Params("a" -> INT),BOOLEAN)
    val f2 = Fn(Params("b" -> INT),BOOLEAN)
    val asg1_2 = f1.assignable(f2)
    println(asg1_2)
  }

  @Test
  def assignable02(): Unit ={
    println("assignable02()")
    println("="*40)

    import JvmType._
    import Type._
    val f1 = Fn(Params("a" -> INT),ANY)
    val f2 = Fn(Params("a" -> NUMBER),BOOLEAN)

    println(NUMBER.assignable(INT))
    println(f1.assignable(f2))
  }
}
