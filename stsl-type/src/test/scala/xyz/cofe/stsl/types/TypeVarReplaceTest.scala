package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class TypeVarReplaceTest {
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
