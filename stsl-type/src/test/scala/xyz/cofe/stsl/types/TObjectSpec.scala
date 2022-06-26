package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import org.scalatest.flatspec.AnyFlatSpec

class TObjectSpec extends AnyFlatSpec{
  "Несвязанные типы-переменные" should "TypeError" in {
    var catched = false
  
    try {
      val obj = TObject("SomeObj")
        .generics(AnyVariant("A"))
        .fields(
          "a" -> TypeVariable("A", Type.THIS),
          "b" -> TypeVariable("B", Type.THIS)
        ).build
    } catch {
      case err:TypeError =>
        println(err)
        catched = true
    }
  
    assert(catched)
  }
  
  "Наследование generic параметров" should "?" in {
    //todo impl ?
  }
}
