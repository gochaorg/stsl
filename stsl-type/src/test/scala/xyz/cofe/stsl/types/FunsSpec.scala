package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import org.scalatest.flatspec.AnyFlatSpec

class FunsSpec extends AnyFlatSpec {
  "Дублирующиеся сигнатуры в списке функций" should "должно TypeError" in {
    import Type._
    import JvmType._
    var catched = false
  
    val f1=Fn(Params("a" -> INT),INT)
    val f2=Fn(Params("b" -> INT),INT)
    val f3=Fn(Params("c" -> VOID),INT)
    try {
      Funs(f1, f2, f3)
    } catch {
      case e:TypeError => catched = true
    }
    assert(catched, "duplicate fun params types not catched")
  }
}
