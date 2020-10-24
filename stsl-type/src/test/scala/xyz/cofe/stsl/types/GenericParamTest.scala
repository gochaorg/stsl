package xyz.cofe.stsl.types

import Type._
import org.junit.jupiter.api.Test

class GenericParamTest {
  @Test
  def test01():Unit = {
    val t1 = CoVariant("a",OBJECT)
    assert(t1.assignable(OBJECT))
    assert(t1.assignable(NUMBER))

    val t2 = CoVariant("b",NUMBER)
    assert(t2.assignable(NUMBER), "NUMBER+ = NUMBER")
    assert(t2.assignable(INT), "NUMBER+ = INT")
    assert(NUMBER.assignable(t2), "NUMBER = NUMBER+")
  }

  @Test
  def test02():Unit = {
    val t2 = CoVariant("b",NUMBER)
    assert(OBJECT.assignable(t2), "OBJECT = NUMBER+")
  }
}
