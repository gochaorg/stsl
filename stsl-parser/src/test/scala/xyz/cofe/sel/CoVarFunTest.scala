package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.cmpl.rt.Scope
import xyz.cofe.sel.types.{AnyVariant, BasicType, CoVariant, ContraVariant, Fun, GenericInstance, GenericParam, GenericPlaceholder, Methods, ObjectType, Param, Properties, Property, Type, TypeDescriber}
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.types.Fun._

class CoVarFunTest {
  //#region function co variant test

  val fn1: Fun = fn("a",INT,"b",INT,NUMBER, (a:Int, b:Int)=>(a+b) )
  val fn1b: Fun = fn("a",INT,"b",INT,NUMBER, (a:Int, b:Int)=>(a-b) )
  val fn2: Fun = fn("a",NUMBER,"b",NUMBER,INT, (a:Number, b:Number)=>a.intValue()+b.intValue() )

  @Test
  def coFnTest():Unit = {
    println(s"fn1 = $fn1")
    println(s"fn1b = $fn1b")
    println(s"fn2 = $fn2")

    val asgn1 = fn1.assignable(fn2)
    println(s"$fn1 assignable $fn2 = $asgn1")
//    println(s"$fn1 assignable2 $fn2 = ${fn1.assignable2(fn2)}")
    assert(asgn1)

    val asgn2 = fn2.assignable(fn1)
    println(s"$fn2 assignable $fn1 = $asgn2")
//    println(s"$fn2 assignable2 $fn1 = ${fn2.assignable2(fn1)}")
    assert(!asgn2)

    val asgn1b = fn1.assignable(fn1b)
    println(s"$fn1 assignable $fn1b = $asgn1b")
//    println(s"$fn1 assignable2 $fn1b = ${fn1.assignable2(fn1b)}")
    assert(asgn1b)

    val asgnDist1 = fn1.assignableDistance(fn2)
    println( asgnDist1 )
    assert( asgnDist1.isDefined )
    assert( asgnDist1.get>0 )

    val asgnDist2 = fn2.assignableDistance(fn1)
    println( asgnDist2 )
    assert( asgnDist2.isDefined )
    assert( asgnDist2.get<0 )

    val asgnDist1b = fn1.assignableDistance(fn1b)
    println( asgnDist1b )
    assert( asgnDist1b.isDefined )
    assert( asgnDist1b.get==0 )

    val asgn1_1 = fn1.assignable(fn1)
    assert(asgn1_1)

    val asgn2_1 = fn2.assignable(fn2)
    assert(asgn2_1)
  }

  val obj = new ObjectType(
    name = "Optional",
    extend = Some(OBJECT),
    genericParams = List(CoVariant("V", OBJECT)),
    objectMethods = Methods(
      "get" -> fn(
        "opt", THIS,
        new GenericPlaceholder("V", THIS),
        (opt:Any) => {}
      )
    )
  )

  val usr = new ObjectType(
    name = "User",
    extend = Some(OBJECT),
    props = Properties(
      Property("name", STRING, usr => ???)
    )
  )

  val mapFn = new Fun(
    args => ???,
    List(
      Param("a",
        //new GenericPlaceholder("A", FN)
        GenericInstance(obj, "V" -> new GenericPlaceholder("A", FN))
      ),
      //Param("b",new GenericPlaceholder("B", FN)),
    ),
    new GenericPlaceholder("A", FN),
    genericsParams = List(
      AnyVariant("A"),
    )
  )

  @Test
  def coFnTest2():Unit = {
    println( "obj:" )
    println( TypeDescriber.describe(obj) )

    println( "usr:" )
    println( TypeDescriber.describe(usr) )

    println( "fn:" )
    println( TypeDescriber.describe(mapFn) )
  }

  //#endregion
}
