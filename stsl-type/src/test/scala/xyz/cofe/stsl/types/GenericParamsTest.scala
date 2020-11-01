package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class GenericParamsTest {
  @Test
  def duplicateName01():Unit = {
    var catched = false
    try {
      GenericParams(
        CoVariant("a", Type.VOID),
        CoVariant("a", Type.VOID)
      )
    } catch {
      case _:TypeError => catched = true
    }
    assert(catched)
  }
}
