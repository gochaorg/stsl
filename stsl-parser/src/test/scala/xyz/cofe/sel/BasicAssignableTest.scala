package xyz.cofe.sel

import org.junit.Test
import xyz.cofe.sel.cmpl.rt.Scope
import xyz.cofe.sel.types.Fun._
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.types._

class BasicAssignableTest {
  @Test
  def test01(): Unit ={
    assert( OBJECT.assignable(NUMBER) )
    assert( NUMBER.assignable(NUMBER) )
    assert( !NUMBER.assignable(OBJECT) )

    println( OBJECT.assignableDistance(NUMBER) )
    println( OBJECT.assignableDistance(INT) )
    println( OBJECT.assignableDistance(INT) )
    println( NUMBER.assignableDistance(INT) )
    println( NUMBER.assignableDistance(OBJECT) )
    println( INT.assignableDistance(OBJECT) )
  }
}
