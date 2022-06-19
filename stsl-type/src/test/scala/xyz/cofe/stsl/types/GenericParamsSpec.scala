package xyz.cofe.stsl.types

import org.scalatest.flatspec.AnyFlatSpec

class GenericParamsSpec extends AnyFlatSpec {
  "Дубли в названии Generic параметров" should "TypeError" in {
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
