package xyz.cofe.stsl.tast

import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.stsl.ast.{ASTDump, Parser}
import xyz.cofe.stsl.tast.AnonymousObject.MethodBuilder
import xyz.cofe.stsl.tast.isect.{AnonFieldsReductor, OptGenInstance, OptionalField, TAnonReductor}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.{AnyVariant, CallableFn, Fn, GenericInstance, GenericParams, Named, Obj, Params, TObject, Type, TypeDescriber, TypeVariable}

import scala.collection.JavaConverters._

class ArrayTest extends AnyFlatSpec {
  val javaListType: TObject = {
    val openType = TObject("List").extend(Type.ANY).build
    
    openType.generics.append.any("A")
    
    openType.fields ++= "size" -> JvmType.INT ->
      ((inst:Any) => inst.asInstanceOf[java.util.List[Any]].size()) ->
      ((inst:Any, newValue:Any) => throw new RuntimeException("immutable"))
    
    openType.methods += "get" -> Fn(
      Params(
        "this" -> Type.THIS,
        "index" -> JvmType.INT
      ),
      TypeVariable("A",THIS)
    )
    
    openType.freeze
    openType
  }
  
  "Toaster for [1,2,3]" should "return List[Int] of 1,2,3" in {
    val parser = new Parser(
      arraySupport = true
    )
    
    val vs = new VarScope()
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports( JvmType.types )
    
    val toaster = new Toaster(
      typeScope = ts,
      varScope = vs,
      arrayCompiler =
        ArrayCompiler.FirstAssignType[java.util.List[Any],TObject](
          typeConstruct = itemType => {
            val listType = javaListType.typeVarBake.thiz("A" -> itemType)
            itemType match {
              case n:Named => listType.withName(s"List_${n.name}")
              case _ => listType
            }
          },
          emptyArray = ()=>new java.util.ArrayList[Any](),
          appendItem = (inst,item,itemType) => {
            inst.add(item)
            inst
          }
        )
    )
    
    val astOpt = parser.parse("[ 1, 2, 3 ]")
    assert(astOpt.isDefined)
    
    astOpt.foreach( ast => {
      ASTDump.dump(ast)
      
      val tast = toaster.compile(ast)
      TASTDump.dump(tast)
  
      println(TypeDescriber.describe(tast.supplierType))
      
      val computed = tast.supplier.get()
      println(computed)
      
      assert(computed.isInstanceOf[java.util.List[Any]])
      
      val lst = computed.asInstanceOf[java.util.List[Any]]
      assert(lst.size()==3)
      assert(lst.get(0)==1)
      assert(lst.get(1)==2)
      assert(lst.get(2)==3)
    })
  }
  
  "Toaster for [{a:1},{a:2,b:4},{a:3}] MergeAnon" should "" in {
    val parser = new Parser(
      arraySupport = true
    )
  
    val vs = new VarScope()
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports( JvmType.types )
  
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
  
    val a_reduct = AnonFieldsReductor(OptionalField(optType,"A"),OptGenInstance())
    val collect = a_reduct.AnonCollector
    val reduct = a_reduct.AnonReductor
    
    val toaster = new Toaster(
      typeScope = ts,
      varScope = vs,
      pojoCompiler = PojoCompiler.TAnonPojo(),
      arrayCompiler =
        ArrayCompiler.MergeAnon(
          collector = collect,
          reducer = reduct,
          arrayTypeConstruct = itemType => {
            val listType = GenericInstance.set("A",itemType).build(javaListType) // javaListType.typeVarBake.thiz("A" -> itemType)
            itemType match {
              ///case n:Named => listType.withName(s"List_${n.name}")
              case _ => listType
            }
          },
          emptyArray = ()=>new java.util.ArrayList[Any](),
          appendItem = (inst:Any,item,itemType) => {
            inst.asInstanceOf[java.util.ArrayList[Any]].add(item)
            inst
          }
        )
    )
  
    val astOpt = parser.parse("[{a:1},{a:2,b:4},{a:3}]")
    assert(astOpt.isDefined)
    println("AST:")
    astOpt.foreach(ASTDump.dump)
    
    val tast = toaster.compile(astOpt.get)
    println("\nTAST:")
    TASTDump.dump(tast)
    
    val resultType = tast.supplierType
    println(s"\nresult type ${resultType}")
    println("\ntype desc")
    println(TypeDescriber.describe(resultType))
    
    val resultValue = tast.supplier.get()
    println(resultValue)
    
    assert(resultType.isInstanceOf[GenericInstance[_]])
    
    val giType = resultType.asInstanceOf[GenericInstance[_]]
    assert( giType.recipe.contains("A") )
    
    val targetItem = giType.recipe("A")
    println("\nitem type")
    println(TypeDescriber.describe(targetItem))
  }
  
  "MergeAnon TAnonReductor( MethodBuilder.AsIs ) [{a:1, x:(int):int},{a:2,b:4, x:(int):int},{a:3, x:(int):int}]" should "" in {
    val parser = new Parser(
      arraySupport = true
    )
  
    val vs = new VarScope()
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports( JvmType.types )
  
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
  
    val a_reduct = TAnonReductor(
      OptionalField(optType,"A"),
      OptGenInstance(),
      MethodBuilder.AsIs
    )
    val collect = a_reduct.AnonCollector
    val reduct = a_reduct.AnonReductor
    
    val toaster = new Toaster(
      typeScope = ts,
      varScope = vs,
      pojoCompiler = PojoCompiler.TAnonPojo(),
      arrayCompiler =
        ArrayCompiler.MergeAnon(
          collector = collect,
          reducer = reduct,
          arrayTypeConstruct = itemType => {
            val listType = GenericInstance.set("A",itemType).build(javaListType) // javaListType.typeVarBake.thiz("A" -> itemType)
            itemType match {
              ///case n:Named => listType.withName(s"List_${n.name}")
              case _ => listType
            }
          },
          emptyArray = ()=>new java.util.ArrayList[Any](),
          appendItem = (inst:Any,item,itemType) => {
            inst.asInstanceOf[java.util.ArrayList[Any]].add(item)
            inst
          }
        )
    )
  
    val astOpt = parser.parse(
      """
        |[
        | {   a: 1,
        |     x: x:int => x+1
        | },{ a: 2,
        |     b: 4,
        |     x: x:int => x+2
        | },{ a: 3,
        |     x: x:int => x+3
        | }
        |]
        |""".stripMargin)
    assert(astOpt.isDefined)
    println("AST:")
    astOpt.foreach(ASTDump.dump)
    
    val tast = toaster.compile(astOpt.get)
    println("\nTAST:")
    TASTDump.dump(tast)
    
    val resultType = tast.supplierType
    println(s"\nresult type ${resultType}")
    println("\ntype desc")
    println(TypeDescriber.describe(resultType))
    
    val resultValue = tast.supplier.get()
    println(resultValue)
    
    assert(resultType.isInstanceOf[GenericInstance[_]])
    
    val giType = resultType.asInstanceOf[GenericInstance[_]]
    assert( giType.recipe.contains("A") )
    
    val targetItem = giType.recipe("A")
    println(s"\nitem type ${targetItem.getClass.getName}")
    println(TypeDescriber.describe(targetItem))
    
    assert( resultValue.isInstanceOf[java.util.List[_]] )
    resultValue.asInstanceOf[java.util.List[_]].asScala.zipWithIndex.foreach { case (item, index) => {
      AnonymousObject.definitionOf(item) match {
        case None => fail(s"expect TAnon for $item")
        case Some(tanon) =>
          tanon.methods("x").head match {
            case callFn:CallableFn =>
              val callResult = callFn.invoke(List(1))
              println( s"index $index call with 1, result = $callResult" )
            case _ => fail("expect callable method x")
          }
      }
    }}
  }
  
  "MergeAnon TAnonReductor( MethodBuilder.ThisCallable )" should "" in {
    val parser = new Parser(
      arraySupport = true
    )
  
    val vs = new VarScope()
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports( JvmType.types )
  
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
  
    val a_reduct = TAnonReductor(
      OptionalField(optType,"A"),
      OptGenInstance(),
      MethodBuilder.ThisCallable
    )
    val collect = a_reduct.AnonCollector
    val reduct = a_reduct.AnonReductor
    
    val toaster = new Toaster(
      typeScope = ts,
      varScope = vs,
      pojoCompiler = PojoCompiler.TAnonPojo(),
      arrayCompiler =
        ArrayCompiler.MergeAnon(
          collector = collect,
          reducer = reduct,
          arrayTypeConstruct = itemType => {
            val listType = GenericInstance.set("A",itemType).build(javaListType) // javaListType.typeVarBake.thiz("A" -> itemType)
            itemType match {
              ///case n:Named => listType.withName(s"List_${n.name}")
              case _ => listType
            }
          },
          emptyArray = ()=>new java.util.ArrayList[Any](),
          appendItem = (inst:Any,item,itemType) => {
            inst.asInstanceOf[java.util.ArrayList[Any]].add(item)
            inst
          }
        )
    )
  
    val astOpt = parser.parse(
      """
        |[
        | {   a: 1,
        |     x: x:int => x+1
        | },{ a: 2,
        |     b: 4,
        |     x: x:int => x+2
        | },{ a: 3,
        |     x: x:int => x+3
        | }
        |]
        |""".stripMargin)
    assert(astOpt.isDefined)
    println("AST:")
    astOpt.foreach(ASTDump.dump)
    
    val tast = toaster.compile(astOpt.get)
    println("\nTAST:")
    TASTDump.dump(tast)
    
    val resultType = tast.supplierType
    println(s"\nresult type ${resultType}")
    println("\ntype desc")
    println(TypeDescriber.describe(resultType))
    
    val resultValue = tast.supplier.get()
    println(resultValue)
    
    assert(resultType.isInstanceOf[GenericInstance[_]])
    
    val giType = resultType.asInstanceOf[GenericInstance[_]]
    assert( giType.recipe.contains("A") )
    
    val targetItem = giType.recipe("A")
    println(s"\nitem type ${targetItem.getClass.getName}")
    println(TypeDescriber.describe(targetItem))
    assert(targetItem.isInstanceOf[Obj])
    val targetItemType = targetItem.asInstanceOf[Obj]
    
    assert( resultValue.isInstanceOf[java.util.List[_]] )
    
    resultValue
      .asInstanceOf[java.util.List[_]]
      .asScala.zipWithIndex.foreach { case (item, index) =>
      targetItemType.methods("x").head match {
        case callFn:CallableFn =>
          val callResult = callFn.invoke(List(item,1))
          println( s"index $index call with 1, result = $callResult" )
        case _ => fail("expect callable method x")
      }
  }}
}
