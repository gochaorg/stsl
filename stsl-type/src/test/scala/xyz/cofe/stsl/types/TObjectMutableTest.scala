package xyz.cofe.stsl.types

import org.junit.jupiter.api.Test

//noinspection UnitMethodIsParameterless
class TObjectMutableTest {
  import TypeDescriber._
  import JvmType._
  import Type._

  @Test
  def test01:Unit = {
    println("test01")

    val tobj = new TObject("Some")
    println(describe(tobj))

    tobj.methods.append("abc", Fn(Params("self" -> THIS),VOID))
    tobj.methods += "abc2" -> Fn(Params("self" -> THIS),VOID)
    tobj.methods += "abc2" -> Fn(Params("self" -> THIS),INT)
    tobj.fields += "fld" -> INT
    tobj.fields += "fld" -> LONG
    println(describe(tobj))

    var catched = false
    try {
      tobj.freeze
      tobj.methods += "abc3" -> Fn(Params("self" -> THIS),VOID)
    } catch {
      case te:TypeError =>
        println(te)
        catched = true
    }

    assert(catched)
  }
}
