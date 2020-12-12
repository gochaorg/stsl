package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.types.TypeDescriber.describe
import Type._
import JvmType._

class TypeVarReplaceTest {
  @Test
  def replaceInParam():Unit = {
    val param1 = Param("a",TypeVariable("A",FN))
    println( param1 )

    val param2 = param1.typeVarReplace( p =>
      if( p.name == "A" ) Some(TypeVariable("B",FN)) else None
    )
    println( param2 )
  }

  @Test
  def replaceInParams():Unit = {
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
  }

  @Test
  def replaceInFun():Unit = {
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

    val ffmap2 = fmap.typeVarBake.fn(
      "A" -> TypeVariable("W", FN),
      "B" -> TypeVariable("Z", FN),
    )
    println( ffmap2 )

    val ffmap3 = fmap.typeVarBake.fn(
      "A" -> INT,
      "B" -> DOUBLE,
    )
    println( ffmap3 )
  }

  @Test
  def replaceInFun02():Unit = {
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

    val ffmap3 = fmap.typeVarBake.fn(
      "A" -> INT,
      "B" -> DOUBLE,
    )
    println( ffmap3 )
  }

  @Test
  def replaceInFun03():Unit = {
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

  @Test
  def replaceInObj01():Unit = {
    import TypeVarReplaceTest._

    println("replaceInObj01()")
    println("====================")
    println( describe(listType) )

    val listType2 = listType.typeVarBake.thiz("A" -> userType).withName("List_User")
    println( describe(listType2) )
  }

  @Test
  def replaceInObj02():Unit = {
    import TypeVarReplaceTest._

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

  @Test
  def replaceInFun04_GI():Unit = {
    import TypeVarReplaceTest._

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

object TypeVarReplaceTest {
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
