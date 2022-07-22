package xyz.cofe.stsl.tast

import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.stsl.ast.Parser
import xyz.cofe.stsl.types.{CallableFn, Fields, Fn, Fun, Funs, GenericInstance, Methods, MutableFuns, Param, Params, TAnon, TObject, Type, TypeDescriber}
import xyz.cofe.stsl.tast.JvmType.{INT, NUMBER, STRING}
import xyz.cofe.stsl.tast.isect._
import xyz.cofe.stsl.types.Type.THIS

/**
 * пересечение несколько TAnon типов
 */
class AnonIntersectSpec extends AnyFlatSpec {
  
  "commonType between int, int" should "Some(int)" in {
    val ct = commonType(JvmType.INT,JvmType.INT)
    assert(ct.isDefined)
    assert(ct.get == JvmType.INT)
  }
  
  "commonType between int, number" should "Some(number)" in {
    val ct = commonType(JvmType.INT,JvmType.NUMBER)
    assert(ct.isDefined)
    assert(ct.get == JvmType.NUMBER)
  }
  
  "commonType between number, int" should "Some(number)" in {
    val ct = commonType(JvmType.NUMBER,JvmType.INT)
    assert(ct.isDefined)
    assert(ct.get == JvmType.NUMBER)
  }
  
  "commonType between double, int" should "None" in {
    val ct = commonType(JvmType.DOUBLE,JvmType.INT)
    assert(ct.isEmpty)
  }
  
  "merge [{a:NUMBER,c:INT},{a:NUMBER,b:NUMBER,c:STRING}]" should "{a:NUMBER,b:Opt[NUMBER]}" in {
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
    
    val a0 = TAnon( Fields("a"->NUMBER, "c"->INT) )
    val a1 = TAnon( Fields("a"->NUMBER, "b"->NUMBER, "c"->STRING) )
    
    val fc = FieldsCollector(List(a0,a1))
    val fr = FieldsReductor(OptionalField(optType,"A"),OptGenInstance())
    val fields = fr.reduce(fc)
    fields.foreach( fld => {
      println(s"${fld.name} : ${fld.tip}")
    })
    
    val fieldNames = fields.map(_.name).toSeq
    assert(fieldNames.length==2)
    assert(fieldNames.contains("a"))
    assert(fieldNames.contains("b"))
    
    val field_a = fields.find(_.name=="a").get
    val field_b = fields.find(_.name=="b").get
    assert(field_a.tip == NUMBER)
    assert(field_b.tip.isInstanceOf[GenericInstance[_]])
    
    val gi = field_b.tip.asInstanceOf[GenericInstance[_]]
    println(gi.source)
    //noinspection ComparingUnrelatedTypes
    println(gi.source==optType)
    
    println(gi.recipe.contains("A"))
    println(gi.recipe.get("A"))
  
    //noinspection ComparingUnrelatedTypes
    assert(gi.source==optType)
    assert(gi.recipe("A")==NUMBER)
  }
  
  "merge [{a:NUMBER,c:INT},{a:NUMBER,b:NUMBER,c:STRING}] with AnonFieldsReductor" should "{a:NUMBER,b:Opt[NUMBER]}" in {
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
  
    val a0 = TAnon( Fields("a"->NUMBER, "c"->INT) )
    val a1 = TAnon( Fields("a"->NUMBER, "b"->NUMBER, "c"->STRING) )
  
    val a_reduct = AnonFieldsReductor(OptionalField(optType,"A"),OptGenInstance())
    val collect = a_reduct.AnonCollector
    val reduct = a_reduct.AnonReductor
    
    val result = reduct.reduce(
      List(a0,a1).foldLeft( collect.initial )( (a,itm) => collect.collect(a,itm) )
    )
    
    println( TypeDescriber.describe(result) )
    assert( result.fields.length==2 )
    assert( result.fields.get("a").isDefined )
    assert( result.fields.get("b").isDefined )
    assert( result.fields("a").tip == NUMBER )
    assert( result.fields("b").tip.isInstanceOf[GenericInstance[_]] )
    //noinspection ComparingUnrelatedTypes
    assert( result.fields("b").tip.asInstanceOf[GenericInstance[_]].source == optType )
    assert( result.fields("b").tip.asInstanceOf[GenericInstance[_]].recipe.contains("A") )
    assert( result.fields("b").tip.asInstanceOf[GenericInstance[_]].recipe("A")==NUMBER )
  }
  
  "merge {a (x:int):int} and {a (x:int):int}" should "{a (x:int):int }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:int):int")
  }

  "merge {a (x:int):int} and {a (x:int):int, b (x:int):int}" should "{a (int):int }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) ),
      "b" -> Funs( Fn(Params("x" -> INT),INT) ),
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:int):int")
  }
  
  "merge {a (x:int):int} and {a (y:int):number}" should "{a (x:int):number }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),NUMBER) )
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:int):number")
  }
  
  "merge {a (x:int):int} and {a (y:int):str}" should "{}" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),STRING) )
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.isEmpty)
  }
  
  "merge {a (x:int):number} and {a (y:int):int}" should "{a (x:int):number }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),NUMBER) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:int):number")
  }
  
  "merge {a (x:int):int} and {a (x:number):int}" should "{a (x:number):int }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> NUMBER),INT) ),
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:number):int")
  }

  "merge {a (x:int):int} and {a (y:number):int}" should "{a (y:number):int }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("y" -> NUMBER),INT) ),
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(y:number):int")
  }
  
  "merge {a (x:int):int} and {a (y:int):int}" should "{a (x:int):int }" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> INT),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("y" -> INT),INT) )
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.contains("a"))
    
    mcoll.common.foreach { case(name,funs) =>
      println(s"method $name")
      funs.foreach { f => println(s"  $f") }
    }
    assert(mcoll.common("a").funs.size==1)
    assert(mcoll.common("a").funs.head.toString=="(x:int):int")
  }
  
  "merge {a (x:str):int} and {a (y:number):int}" should "{}" in {
    val a0 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("x" -> STRING),INT) )
    )))
    val a1 = TAnon( Fields(), new Methods(Map(
      "a" -> Funs( Fn(Params("y" -> NUMBER),INT) ),
    )))
    
    val mcoll = MethodCollector().join(a0.methods).join(a1.methods)
    assert(mcoll.joinCount==2)
    assert(mcoll.common.isEmpty)
  }
  
  "merge and call {a (x:int):int=x+1} and {a (x:int):int=x+2}" should "{ a (x:int):int }" in {
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(JvmType.types)
    
    val ast1 = Parser.parse(
      """
        |{
        |  a: x:int => x+1
        |}
        |""".stripMargin
    )
    assert(ast1.isDefined)
    
    val ast2 = Parser.parse(
      """
        |{
        |  a: x:int => x+2
        |}
        |""".stripMargin
    )
    assert(ast2.isDefined)
  
    val vs = new VarScope()
  
    val toaster = new Toaster(ts,vs,pojoCompiler = PojoCompiler.TAnonPojo())
    val tast1 = toaster.compile(ast1.get)
    val tast2 = toaster.compile(ast2.get)
    
    val inst1 = tast1.supplier.get()
    val inst2 = tast2.supplier.get()
    
    assert( AnonymousObject.definitionOf(inst1).isDefined )
    assert( AnonymousObject.definitionOf(inst2).isDefined )
    
    val tInst1 = AnonymousObject.definitionOf(inst1).get
    val tInst2 = AnonymousObject.definitionOf(inst2).get
    
    val mcoll = MethodCollector().join(tInst1.methods).join(tInst2.methods)
    assert(mcoll.common.nonEmpty)
    assert(mcoll.common.contains("a"))
    assert(mcoll.common("a").funs.size==1)
    println("common lambda: "+mcoll.common("a").head)
    assert(mcoll.common("a").head.toString=="(x:int):int")
    
    val anonCall = anonCallable("a", mcoll.common("a").head)
    println("anon callable: "+anonCall)
    
    val a_call1res = anonCall.invoke(List(inst1,1))
    val a_call2res = anonCall.invoke(List(inst2,1))
  
    val call1res = tInst1.methods("a").head.asInstanceOf[CallableFn].invoke(List(1))
    val call2res = tInst2.methods("a").head.asInstanceOf[CallableFn].invoke(List(1))
    println(s"  call1res=$call1res   call2res=$call2res")
    println(s"a_call1res=$a_call1res a_call2res=$a_call2res")
    
    assert(call1res==a_call1res)
    assert(call2res==a_call2res)
  }
}
