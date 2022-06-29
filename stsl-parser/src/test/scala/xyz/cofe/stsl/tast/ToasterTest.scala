package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.tast.JvmType._
import xyz.cofe.stsl.ast.{ASTDump, AstTest, Parser}
import xyz.cofe.stsl.types.{CallableFn, Fn, Params, TObject, Type, TypeDescriber}

//noinspection UnitMethodIsParameterless
class ToasterTest {
  import AstTest._

  @Test
  def listeralAndBinary:Unit = {
    println("listeralAndBinary")
    println("="*30)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("20 + 20 / 2")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, binary, literal, binary, literal, literal)

    val tst = new Toaster(ts)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( 30 == computedValue )
  }
  
  @Test
  def delegateAstTest:Unit = {
    println("delegateAstTest")
    println("="*30)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("( 10 - 5 ) * 5")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, binary, delegate, binary, literal, literal, literal)

    val tst = new Toaster(ts)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( 25 == computedValue )
  }

  @Test
  def varTest:Unit = {
    println("varTest")
    println("="*30)

    val vs = new VarScope();
    vs.put(
    "a" -> INT -> 12,
    "b" -> INT -> 13,
    "c" -> INT -> 2,
    )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("a + b * c")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, binary, delegate, binary, literal, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (12 + 13 * 2) == computedValue )
  }

  @Test
  def propTest:Unit = {
    class Person( var age:Int = 10 ) {
    }

    println("propTest")
    println("="*30)

    val userType = new TObject("Person");
    userType.fields ++=
      "age" -> INT ->
        ((p:Any) => p.asInstanceOf[Person].age) ->
        ((thiz:Any, pvalue:Any) => pvalue)

    val per1 = new Person(5)
    val per2 = new Person(7)

    val vs = new VarScope();
    vs.put("per1" -> userType -> per1 )
    vs.put("per2" -> userType -> per2 )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(userType)

    val ast = Parser.parse("per1.age + per2.age")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, binary, property, identifier, property, identifier)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (per1.age + per2.age) == computedValue )
  }

  @Test
  def methCallTest:Unit = {
    println("methCallTest")
    println("="*30)

    val vs = new VarScope();
    vs.put("str" -> STRING -> "string" )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("str.substring( 0, 3 )")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, call, property, identifier, literal, literal)
    
    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == STRING )
    assert( computedValue!=null && computedValue.isInstanceOf[String] )
    assert( "str" == computedValue )
  }
  
  @Test
  def funCallTest:Unit = {
    println("funCallTest")
    println("="*30)

    val repeatFn = Fn(Params("str" -> STRING, "count" -> INT),STRING).invoke[String,Int,String]((str:String,cnt:Int)=>str*cnt)

    val vs = new VarScope();
    vs.put("str" -> STRING -> "string" )
    vs.put("repeat" -> repeatFn -> repeatFn )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("repeat( str, 2 )")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == STRING )
    assert( computedValue!=null && computedValue.isInstanceOf[String] )
    assert( "stringstring" == computedValue )
  }

  @Test
  def lambdaCall01:Unit = {
    println("lambdaCall01")
    println("="*30)

    val vs = new VarScope();

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("a:int , b:int => a+b")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( computedValue!=null && computedValue.isInstanceOf[CallableFn] )

    println("call 1,2")
    val lambdaComputedValue = computedValue.asInstanceOf[CallableFn].invoke(List(1,2))

    println(s"result = $lambdaComputedValue")
    assert(lambdaComputedValue!=null)
    assert(lambdaComputedValue.isInstanceOf[Int])
    assert(3 == lambdaComputedValue)
  }

  @Test
  def lambdaCall02:Unit = {
    println("lambdaCall02")
    println("="*30)

    val vs = new VarScope();

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("a:int , r :: int => a < 0 ? 0 : r(a-1) + a")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( computedValue!=null && computedValue.isInstanceOf[CallableFn] )

    List(
      (1,1),
      (2,3),
      (3,6),
      (4,10),
      (5,15),
    ).foreach({case(input,resultExpect)=>
      val lambdaComputedValue = computedValue.asInstanceOf[CallableFn].invoke(List(input))
      println(s"call $input result = $lambdaComputedValue")
      assert(lambdaComputedValue!=null)
      assert(lambdaComputedValue.isInstanceOf[Int])
      assert(resultExpect == lambdaComputedValue)
    })
  }

  @Test
  def lambdaCall03:Unit = {
    println("lambdaCall03")
    println("="*30)

    val vs = new VarScope();
    vs.put("a", INT, 1)
    vs.put("b", INT, 1)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("() => a+b")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )

    var catched = false
    try {
      val tst = new Toaster(ts, vs)
      val tast = tst.compile(ast.get)
    } catch {
      case err:ToasterError =>
        println(err)
        catched = true
    }

    assert(catched)
  }

  @Test
  def objDef01(): Unit ={
    println("objDef01")
    println("="*30)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("{ k1: 1, k2: \"abc\"}")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, pojo, pojoItem, literal, pojoItem, literal)

    val tst = new Toaster(ts)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    //println("exec")
    println("-"*30)
    println(TypeDescriber.describe(tast.supplierType))
  }
  
  @Test
  def objDef02():Unit = {
    println("objDef02")
    println("="*30)
  
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
  
    val ast = Parser.parse(
      """
        |{
        | k1: 1,
        | k2: a:int => a+a
        |}
        |""".stripMargin)
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
  
    val tst = new Toaster(ts)
    ts.implicits = JvmType.implicitConversion
    ts.imports(JvmType.types)
    
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)
  
    println("-"*30)
    println("supplierType:")
    println(TypeDescriber.describe(tast.supplierType))
    println("supplier.get():")
    
    val computedValue = tast.supplier.get()
    println(computedValue)
    println(computedValue.getClass)
    
    val computedMap = computedValue.asInstanceOf[java.util.Map[_,_]]
    computedMap.forEach( (k,v) => {
      print(s"  key(${k} : ${k.getClass})")
      println(s" = value(${v} : ${v.getClass})")
    })
  }
}
