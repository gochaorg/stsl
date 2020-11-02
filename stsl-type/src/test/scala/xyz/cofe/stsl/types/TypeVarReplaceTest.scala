package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

class TypeVarReplaceTest {
  @Test
  def replaceInParam():Unit = {
    val param1 = Param("a",TypeVariable("A",Type.FN))
    println( param1 )

    val param2 = param1.typeVarReplace( p =>
      if( p.name == "A" ) Some(TypeVariable("B",Type.FN)) else None
    )
    println( param2 )
  }

  @Test
  def replaceInParams():Unit = {
    val params1 = Params(
      Param("a",TypeVariable("A",Type.FN)),
      Param("b",TypeVariable("A",Type.FN)),
      Param("c",TypeVariable("B",Type.FN)),
      Param("d",TypeVariable("C",Type.FN))
    )

    println( params1 )

    val params2 = params1.typeVarReplace( p=>
      if( p.name == "A" )
        Some(TypeVariable("E",Type.FN))
      else if( p.name == "B" )
        Some(TypeVariable("F",Type.FN))
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
        Param("a", TypeVariable("A", Type.FN))
      ),
      TypeVariable("B", Type.FN)
    )
    println(fmap)

    val ffmap = fmap.typeVarReplace(tv => tv.name match {
      case "A" => Some(TypeVariable("X", Type.FN))
      case "B" => Some(TypeVariable("Y", Type.FN))
      case _ => None
    })
    println( ffmap )

    val ffmap2 = fmap.typeVarBake.fn(
      "A" -> TypeVariable("W", Type.FN),
      "B" -> TypeVariable("Z", Type.FN),
    )
    println( ffmap2 )

    val ffmap3 = fmap.typeVarBake.fn(
      "A" -> Type.INT,
      "B" -> Type.DOUBLE,
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
        Param("a", TypeVariable("A", Type.FN))
      ),
      TypeVariable("B", Type.FN)
    )
    println(fmap)

    val ffmap3 = fmap.typeVarBake.fn(
      "A" -> Type.INT,
      "B" -> Type.DOUBLE,
    )
    println( ffmap3 )
  }

  @Test
  def replaceInFun03():Unit = {
    var f:Function1[Number,Number] = (a)=>a
    val a:Int = 10
    f(a)

    val contraVar = ContraVariant("A",Type.NUMBER)
    val typeArg = Type.INT
    assert(contraVar.assignable(typeArg))

    val coVar = CoVariant("B",Type.NUMBER)
    val typeRet = Type.DOUBLE
    assert(coVar.assignable(typeRet))

    val fmap = Fn(
      GenericParams(
        contraVar,
        coVar,
      ),
      Params(
        Param("a", TypeVariable("A", Type.FN))
      ),
      TypeVariable("B", Type.FN)
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
        "A" -> Type.ANY,
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
        "B" -> Type.ANY,
      )
    } catch {
      case err:TypeError =>
        catch2 = true
        println(err)
    }
    assert(catch2)
  }
}
