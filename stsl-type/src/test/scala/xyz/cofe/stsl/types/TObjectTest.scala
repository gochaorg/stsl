package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class TObjectTest {
  @Test
  def unbindedTypeVariables01():Unit = {
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
}
