package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._

class ExtendableTest {
  @Test
  def test01():Unit = {
    import Type._

    assert(OBJECT.assignable(OBJECT))
    assert(VOID.assignable(VOID))
    assert(NUMBER.assignable(NUMBER))
    assert(INT.assignable(INT))
    assert(DOUBLE.assignable(DOUBLE))

    assert(!VOID.assignable(OBJECT))
    assert(!OBJECT.assignable(VOID))

    assert(OBJECT.assignable(NUMBER))
    assert(!NUMBER.assignable(OBJECT))

    assert(OBJECT.assignable(INT))
    assert(NUMBER.assignable(INT))
    assert(!INT.assignable(NUMBER))
    assert(!INT.assignable(OBJECT))

    assert(OBJECT.assignable(DOUBLE))
    assert(NUMBER.assignable(DOUBLE))
    assert(!DOUBLE.assignable(NUMBER))
    assert(!DOUBLE.assignable(OBJECT))
  }
}
