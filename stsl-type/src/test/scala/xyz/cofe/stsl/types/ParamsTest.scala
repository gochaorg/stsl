package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import Type._

class ParamsTest {
  @Test
  def test01():Unit = {
    var catched = false
    try {
      Params(
        "a" -> ANY,
        "a" -> ANY,
      )
    } catch {
      case e:TypeError => catched = true
    }
    assert(catched, "duplicate name not matched")
  }
}
