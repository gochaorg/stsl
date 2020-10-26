package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions._

class ExtendableTest {
  @Test
  def test01():Unit = {
    import Type._

    assert(ANY.assignable(ANY))
    assert(VOID.assignable(VOID))
    assert(NUMBER.assignable(NUMBER))
    assert(INT.assignable(INT))
    assert(DOUBLE.assignable(DOUBLE))

    assert(!VOID.assignable(ANY))
    assert(!ANY.assignable(VOID))

    assert(ANY.assignable(NUMBER))
    assert(!NUMBER.assignable(ANY))

    assert(ANY.assignable(INT))
    assert(NUMBER.assignable(INT))
    assert(!INT.assignable(NUMBER))
    assert(!INT.assignable(ANY))

    assert(ANY.assignable(DOUBLE))
    assert(NUMBER.assignable(DOUBLE))
    assert(!DOUBLE.assignable(NUMBER))
    assert(!DOUBLE.assignable(ANY))
  }
}
