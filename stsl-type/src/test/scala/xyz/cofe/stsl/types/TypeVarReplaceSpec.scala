package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.types.TypeDescriber.describe
import Type._
import JvmType._
import org.scalatest.flatspec.AnyFlatSpec

class TypeVarReplaceSpec extends AnyFlatSpec {
  "Замена переменной тип A на B в параметре" should "a:A должно стать a:B" in {
    val param1 = Param("a",TypeVariable("A",FN))
    println( param1 )
    assert( param1.tip.isInstanceOf[TypeVariable] )
    assert( param1.tip.asInstanceOf[TypeVariable].name  == "A" )
    assert( param1.tip.asInstanceOf[TypeVariable].owner == FN )

    val param2 = param1.typeVarReplace( p =>
      if( p.name == "A" ) Some(TypeVariable("B",FN)) else None
    )
    println( param2 )
    assert( param2.tip.isInstanceOf[TypeVariable] )
    assert( param2.tip.asInstanceOf[TypeVariable].name  == "B" )
    assert( param2.tip.asInstanceOf[TypeVariable].owner == FN )
  }

  "Замена типа переменной в параметрах" should "(a:A,b:A,c:B,d:C) замена на (a:E,b:E,c:F,d:C)" in {
    val params1 = Params(
      "a" -> TypeVariable("A",FN),
      "b" -> TypeVariable("A",FN),
      "c" -> TypeVariable("B",FN),
      "d" -> TypeVariable("C",FN)
    )

    println( params1 )

    val params2 = params1.typeVarReplace( p=>
      if( p.name == "A" )
        Some(TypeVariable("E",FN))
      else if( p.name == "B" )
        Some(TypeVariable("F",FN))
      else
        None
    )

    println( params2 )
    
    assert( params2.length==params1.length )
    assert( params2.head.tip.asInstanceOf[TypeVariable].name == "E" )
    assert( params2(1).tip.asInstanceOf[TypeVariable].name == "E" )
    assert( params2(2).tip.asInstanceOf[TypeVariable].name == "F" )
    assert( params2(3).tip.asInstanceOf[TypeVariable].name == "C" )
  }

  "Замена типа переменной в функции [A,B](a:A):B" should "[A,B](a:A):B замена на [X,Y](a:X):Y - typeVarReplace" in {
    val fmap = Fn(
      GenericParams(
        AnyVariant("A"),
        AnyVariant("B"),
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    val ffmap = fmap.typeVarReplace(tv => tv.name match {
      case "A" => Some(TypeVariable("X", FN))
      case "B" => Some(TypeVariable("Y", FN))
      case _ => None
    })
    println( ffmap )
    
    assert( ffmap.generics.size==2 )
    assert( ffmap.generics.get("X").isDefined )
    assert( ffmap.generics.get("Y").isDefined )
    assert( ffmap.parameters.head.tip.asInstanceOf[TypeVariable].name=="X" )
    assert( ffmap.parameters.head.tip.asInstanceOf[TypeVariable].owner==FN )
    assert( ffmap.returns.asInstanceOf[TypeVariable].name=="Y" )
    assert( ffmap.returns.asInstanceOf[TypeVariable].owner==FN )
  }
  
  it should "[A,B](a:A):B замена на [W,Z](a:W):Z - typeVarBake" in {
    val fmap = Fn(
      GenericParams(
        AnyVariant("A"),
        AnyVariant("B"),
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    val ffmap = fmap.typeVarBake.fn(
      "A" -> TypeVariable("W", FN),
      "B" -> TypeVariable("Z", FN),
    )
    println( ffmap )
  
    assert( ffmap.generics.size==2 )
    assert( ffmap.generics.get("W").isDefined )
    assert( ffmap.generics.get("Z").isDefined )
    assert( ffmap.parameters.head.tip.asInstanceOf[TypeVariable].name=="W" )
    assert( ffmap.parameters.head.tip.asInstanceOf[TypeVariable].owner==FN )
    assert( ffmap.returns.asInstanceOf[TypeVariable].name=="Z" )
    assert( ffmap.returns.asInstanceOf[TypeVariable].owner==FN )
  }
  
  it should "[A,B](a:A):B замена на (a:int):double - typeVarBake" in {
    val fmap = Fn(
      GenericParams(
        AnyVariant("A"),
        AnyVariant("B"),
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    val ffmap = fmap.typeVarBake.fn(
      "A" -> INT,
      "B" -> DOUBLE,
    )
    println( ffmap )
  
    assert( ffmap.generics.isEmpty )
    assert( ffmap.parameters.head.tip == INT )
    assert( ffmap.returns == DOUBLE )
  }

  "Замена типа-переменной в функции [A:number-,B:number+](a:A):B с учетом совместимости типов" should "успешно (a:int):double" in {
    var f:Function1[Number,Number] = (a)=>a
    val a:Int = 10
    f(a)

    val contraVar = ContraVariant("A",NUMBER)
    val typeArg = INT
    assert(contraVar.assignable(typeArg))

    val coVar = CoVariant("B",NUMBER)
    val typeRet = DOUBLE
    assert(coVar.assignable(typeRet))

    val fmap = Fn(
      GenericParams(
        contraVar,
        coVar,
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    val ffmap = fmap.typeVarBake.fn(
      "A" -> typeArg,
      "B" -> typeRet,
    )
    println( ffmap )
    assert( ffmap.generics.isEmpty )
    assert( ffmap.parameters.length==1 )
    assert( ffmap.parameters.head.tip==INT )
    assert( ffmap.returns==DOUBLE )
  }

  it should "недопустима замена A на Any - TypeError" in {
    var f:Function1[Number,Number] = (a)=>a
    val a:Int = 10
    f(a)

    val contraVar = ContraVariant("A",NUMBER)
    val typeArg = INT
    assert(contraVar.assignable(typeArg))

    val coVar = CoVariant("B",NUMBER)
    val typeRet = DOUBLE
    assert(coVar.assignable(typeRet))

    val fmap = Fn(
      GenericParams(
        contraVar,
        coVar,
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    var catch1 = false
    try {
      fmap.typeVarBake.fn(
        "A" -> ANY,
        "B" -> typeRet,
      )
    } catch {
      case err:TypeError =>
        catch1 = true
        println(err)
    }
    assert(catch1)
  }

  it should "недопустима замена B на Any - TypeError" in {
    var f:Function1[Number,Number] = (a)=>a
    val a:Int = 10
    f(a)

    val contraVar = ContraVariant("A",NUMBER)
    val typeArg = INT
    assert(contraVar.assignable(typeArg))

    val coVar = CoVariant("B",NUMBER)
    val typeRet = DOUBLE
    assert(coVar.assignable(typeRet))

    val fmap = Fn(
      GenericParams(
        contraVar,
        coVar,
      ),
      Params(
        "a" -> TypeVariable("A", FN)
      ),
      TypeVariable("B", FN)
    )
    println(fmap)

    var catch1 = false
    try {
      fmap.typeVarBake.fn(
        "A" -> ANY,
        "B" -> typeRet,
      )
    } catch {
      case err:TypeError =>
        catch1 = true
        println(err)
    }
    assert(catch1)

    var catch2 = false
    try {
      fmap.typeVarBake.fn(
        "A" -> typeArg,
        "B" -> ANY,
      )
    } catch {
      case err:TypeError =>
        catch2 = true
        println(err)
    }
    assert(catch2)
  }

  "replaceInObj01" should "?" in {
    import TypeVarReplaceSpec._

    println("replaceInObj01()")
    println("====================")
    println( describe(listType) )

    val listType2 = listType.typeVarBake.thiz("A" -> userType).withName("List_User")
    println( describe(listType2) )
  }

  "replaceInObj02" should "?" in {
    import TypeVarReplaceSpec._

    println("replaceInObj02()")
    println("====================")
    println( describe(listType) )

    val listType3 = listType.typeVarBake.thiz("A" -> TypeVariable("B", THIS)).withName("List_2")
    println( describe(listType3) )
  }

  implicit class TypeVar(varName:String) {
    def fn: TypeVariable = TypeVariable(varName,FN)
  }

  implicit class Bake(base:TObject) {
    def bake(r:(String,Type)*):GenericInstance[TObject] = {
      val m:Map[String,Type] = r.toMap
      new GenericInstance(m,base)
    }
  }
  implicit class BakeFn(base:Fun) {
    def bake(r:(String,Type)*):GenericInstance[Fun] = {
      val m:Map[String,Type] = r.toMap
      new GenericInstance(m,base)
    }
  }

  "replaceInFun04_GI" should "?" in {
    import TypeVarReplaceSpec._

    println("replaceInFun04_GI()")
    println("====================")
    println("list type")
    println( describe(listType) )

    val fmapper = Fn(
      GenericParams(
        AnyVariant("F"),
        AnyVariant("T"),
      ),
      Params(
        "from" -> "F".fn
      ),
      "T".fn
    )
    println("\nfmapper")
    println(describe(fmapper))

    val fn1 = Fn(
      GenericParams(
        AnyVariant("X"),
        AnyVariant("Y"),
      ),
      Params(
        "list" -> listType.bake(
          "A" -> "X".fn),
        "map" -> fmapper.bake(
          "F" -> "X".fn,
          "T" -> "Y".fn )
      ),
      listType.bake("A"->"Y".fn)
    )
    println("\nlist map")
    println(describe(fn1))
  }
}

object TypeVarReplaceSpec {
  val listType = TObject("List")
    .generics(AnyVariant("A"))
    .fields("size" -> INT)
    .methods(
      "add" -> Fn(
        Params(
          "this" -> THIS,
          "item" -> TypeVariable("A",THIS),
        ),
        VOID
      ),
      "get" -> Fn(
        Params(
          "this" -> THIS,
          "idx" -> INT,
        ),
        TypeVariable("A",THIS)
      ),
    )
    .build

  val userType = TObject("User")
    .fields("name" -> INT)
    .build
}
