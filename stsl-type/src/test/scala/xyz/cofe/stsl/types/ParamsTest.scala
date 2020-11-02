package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import Type._

class ParamsTest {
  @Test
  def test01():Unit = {
    var catched = false
    try {
      Params(
        Param("a", ANY),
        Param("a", ANY),
      )
    } catch {
      case e:TypeError => catched = true
    }
    assert(catched, "duplicate name not matched")
  }

  @Test
  def test02():Unit = {
    val param1 = Param("a",TypeVariable("A",Type.FN))
    println( param1 )

    val param2 = param1.typeVarReplace( p =>
      if( p.name == "A" ) Some(TypeVariable("B",Type.FN)) else None
    )
    println( param2 )
  }
}
