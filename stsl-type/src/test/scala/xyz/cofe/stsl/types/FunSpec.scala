package xyz.cofe.stsl.types

import org.scalatest.flatspec.AnyFlatSpec

class FunSpec extends AnyFlatSpec {
  implicit val trace = AssignableTracer(System.out)

  "Неиспользуемый Generic параметр (лишний параметр)" should "TypeError" in {
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
      case err: TypeError =>
        println(err)
        catched = true
    }
    assert(catched)
  }

  "Указаны 2 Generic параметра" should "Успешно и оба используются" in {
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
  }

  "Не указан(ы) Generic параметр (недостающие параметры)" should "TypeError" in {
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

  "Присвоение переменной (лямбда) другой лямбды совпадающей по типам" should "успешно" in {
    import JvmType._

    println("assignableFun01")
    println("=" * 40)

    val f1 = Fn(Params("a" -> INT), BOOLEAN)
    val f2 = Fn(Params("b" -> INT), BOOLEAN)
    val asg1_2 = f1.assignable(f2)
    println(asg1_2)
  }

  "Присвоение переменной (лямбда) другой лямбды ко-вариант: (INT->ANY = NUMBER->BOOLEAN)" should "успешно" in {
    println("assignable02()")
    println("=" * 40)

    import JvmType._
    import Type._
    val f1 = Fn(Params("a" -> INT), ANY)
    val f2 = Fn(Params("a" -> NUMBER), BOOLEAN)

    println(NUMBER.assignable(INT))
    println(f1.assignable(f2))
  }
}
