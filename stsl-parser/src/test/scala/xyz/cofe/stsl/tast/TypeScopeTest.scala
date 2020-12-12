package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import JvmType._
import xyz.cofe.stsl.types.Type._
import xyz.cofe.stsl.types.{Fn, Fun, Invoke, Obj, Params, TObject, Type}
import xyz.cofe.stsl.types.pset.PartialSet

//noinspection UnitMethodIsParameterless
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
  def graphTest:Unit = {
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

  @Test
  def summ01:Unit = {
    println("summ01")
    println("="*30)

    val fromByte2Short = Fn( Params( "value" -> BYTE ), SHORT ).invoke[Byte,Short]( value => value.toShort )

    val fromByte2Int = Fn( Params( "value" -> BYTE ), INT ).invoke[Byte,Int]( value => value.toInt )
    val fromShort2Int = Fn( Params( "value" -> SHORT ), INT ).invoke[Short,Int]( value => value.toInt )

    val fromByte2Long = Fn( Params( "value" -> BYTE ), LONG ).invoke[Byte,Long]( value => value.toLong )
    val fromShort2Long = Fn( Params( "value" -> SHORT ), LONG ).invoke[Short,Long]( value => value.toLong )
    val fromInt2Long = Fn( Params( "value" -> INT ), LONG ).invoke[Int,Long]( value => value.toLong )

    val fromInt2BigInt = Fn( Params( "value" -> INT ), BIGINT ).invoke[Int,BigInt]( value => BigInt(value) )
    val fromInt2Decimal = Fn( Params( "value" -> INT ), DECIMAL ).invoke[Int,BigDecimal]( value => BigDecimal(value) )

    val implConversion = List(
      fromByte2Short, fromByte2Int, fromByte2Long,
      fromShort2Int, fromShort2Long,
      fromInt2Long,
      fromInt2BigInt, fromInt2Decimal
    )

    val tscope = new TypeScope
    tscope.implicits = implConversion

    val cases = tscope.callCases(INT, "+", List(INT, INT)) //new CallCases(INT, "+", List(INT, INT), tscope)
    cases.cases.foreach( println )

    println( "preferred ")
    println( "-"*30 )

    val prefCases = cases.preferred
    prefCases.foreach( println )

    if( prefCases.size==1 ){
      println("try call")
      println("-"*30)

      val invoke = prefCases.head.invoking()
      println(s"expected type=${invoke._2}")

      val result = invoke._1.invoke(List(1,2))
      println(s"result ${result} : ${if(result!=null){ result.getClass.getName }}")
    }
  }

  @Test
  def ts01():Unit = {
    println("ts01()")

    import JvmType._
    val ts = new TypeScope()
    ts.imports(List(BOOLEAN,CHAR,ANY,NUMBER,INT,LONG))

    println("....")
    ts.graph.ascending(BOOLEAN,BOOLEAN).foreach(println)

    println("....")
    ts.graph.ascending(BOOLEAN,ANY).foreach(println)
  }
}
