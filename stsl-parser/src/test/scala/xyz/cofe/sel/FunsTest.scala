package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.cmpl.rt.Funs
import xyz.cofe.sel.cmpl.rt.PredefFun
import xyz.cofe.sel.types.Fun
import xyz.cofe.sel.types.Fun.fn

class FunsTest {
  @Test
  def test01(): Unit ={
    val addObj = fn("a",OBJECT, "b",OBJECT, OBJECT, ((a:Int,b:Int)=>a+b))
    val addNum = fn("a",NUMBER, "b",NUMBER, NUMBER, ((a:Int,b:Int)=>a+b))
    val addInt = fn("a",INT, "b",INT, INT, ((a:Int,b:Int)=>a+b))
    val addLong = fn("a",LONG, "b",LONG, LONG, ((a:Long,b:Long)=>a+b))
    val addDouble = fn("a",DOUBLE, "b",DOUBLE, DOUBLE, ((a:Double,b:Double)=>a+b))

    def funs = new Funs(List(addInt, addLong, addDouble, addObj, addNum))
    println( "for INT INT" )
    funs.find.sameArgs(List(INT,INT)).foreach( println )

    println( "for NUMBER NUMBER" )
    funs.find.sameArgs(List(NUMBER,NUMBER)).foreach( println )

    println( "for OBJECT OBJECT" )
    funs.find.sameArgs(List(OBJECT,OBJECT)).foreach( println )
  }

  @Test
  def test02(): Unit ={
    val int2num = fn("n",INT,NUMBER,(n:Int)=>n.asInstanceOf[Number])
    val num2int = fn("n",NUMBER,INT,(n:Number)=>n.intValue())
    val int2long = fn("n",INT,LONG,(n:Int)=>n.toLong)
    val long2int = fn("n",LONG,INT,(n:Long)=>n.toInt)

    def funs = new Funs(List(int2num, num2int, int2long, long2int))
    funs.find.same(List(LONG),INT).foreach(println)
  }

  @Test
  def test03(): Unit ={
    val addObj = fn("a",OBJECT, "b",OBJECT, OBJECT, ((a:Int,b:Int)=>a+b))
    val addNum = fn("a",NUMBER, "b",NUMBER, NUMBER, ((a:Int,b:Int)=>a+b))
    val addInt = fn("a",INT, "b",INT, INT, ((a:Int,b:Int)=>a+b))
    val addLong = fn("a",LONG, "b",LONG, LONG, ((a:Long,b:Long)=>a+b))
    val addDouble = fn("a",DOUBLE, "b",DOUBLE, DOUBLE, ((a:Double,b:Double)=>a+b))

    def funs = new Funs(List(addInt, addLong, addDouble, addObj, addNum))

    val int2num = fn("n",INT,NUMBER,(n:Int)=>n.asInstanceOf[Number])
    val num2int = fn("n",NUMBER,INT,(n:Number)=>n.intValue())
    val int2long = fn("n",INT,LONG,(n:Int)=>n.toLong)
    val long2int = fn("n",LONG,INT,(n:Long)=>n.toInt)

    def implc = new Funs(List(int2num, num2int, int2long, long2int))

    println( "for INT LONG" )
    funs.find.sameArgs(List(INT,LONG))(implc).foreach( println )
  }
}
