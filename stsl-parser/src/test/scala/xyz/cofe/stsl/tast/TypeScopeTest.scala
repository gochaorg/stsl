package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import JvmType._
import xyz.cofe.stsl.types.Type._
import xyz.cofe.stsl.types.Type
import xyz.cofe.stsl.types.pset.PartialSet

class TypeScopeTest {
  /**
   * Предопределенный граф типов
   */
  val testTypesAssgn = List(
    (ANY,ANY,true),
    (ANY,NUMBER,true),
    (ANY,INT,true),
    (ANY,LONG,true),
    (NUMBER,INT,true),
    (NUMBER,LONG,true),
    (LONG,ANY,false),
    (LONG,INT,false),
    (LONG,NUMBER,false),
    (INT,LONG,false),
  )

  /**
   * Типы для тестирования графа
   */
  val testTypeSet : Set[Type] = (testTypesAssgn.map(_._1) ++ testTypesAssgn.map(_._2)).distinct.toSet

  @Test
  def testPredefGraph:Unit = {
    println("testPredefGraph")
    println("="*30)
    testTypesAssgn.foreach({case(consumer,supplier,assignExpected)=>
      val asgn = consumer.assignable(supplier)
      val matched = asgn==assignExpected
      println(s"${consumer}.assignable(${supplier})=${assignExpected} expected=${assignExpected} matched=${matched}")
      assert(matched)
    })
  }

  @Test
  def test01:Unit = {
    println("test01")
    println("="*30)

    val pset = PartialSet[Type](
      testTypeSet,
      (a,b) => a == b,
      (a,b) => a.assignable(b)
    )

    println(s"descending $ANY > $INT")
    pset.descending(ANY,INT).foreach(path => println(path.map(_.toString()).reduce((a,b)=>a+" > "+b)))

    println(s"\ndescending $ANY > $LONG")
    pset.descending(ANY,LONG).foreach(path => println(path.map(_.toString()).reduce((a,b)=>a+" > "+b)))

    println(s"\ndescending $LONG > $ANY")
    pset.ascending(LONG,ANY).foreach(path => println(path.map(_.toString()).reduce((a,b)=>a+" < "+b)))
  }
}
