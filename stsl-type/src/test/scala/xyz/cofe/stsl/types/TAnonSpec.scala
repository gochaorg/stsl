package xyz.cofe.stsl.types

import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.stsl.types.Type.{ANY, THIS, VOID}

class TAnonSpec extends AnyFlatSpec {

  import JvmType._

  implicit val trace = AssignableTracer(System.out)

  val listType = TObject("List")
    .generics(AnyVariant("A"))
    .fields("size" -> INT)
    .methods(
      "add" -> Fn(
        Params(
          "this" -> THIS,
          "item" -> TypeVariable("A", THIS),
        ),
        VOID
      ),
      "get" -> Fn(
        Params(
          "this" -> THIS,
          "idx" -> INT,
        ),
        TypeVariable("A", THIS)
      ),
    )
    .build

  val userType = TObject("User")
    .fields("name" -> INT)
    .methods(
      "sayHello" -> Fn(
        Params(
          "this" -> THIS
        ),
        THIS
      )
    )
    .build

  "Создание из TAnon из TObject (userType)" should "копируются все поля и методы" in {
    println("\"Создание из TAnon из TObject (userType)\" should \"копируются все поля и методы\"")
    println("=" * 80)

    val anon = TAnon.from(userType)
    println(TypeDescriber.describe(anon))

    assert(anon.fields.size == 1)
    assert(anon.fields.get("name").isDefined)
    assert(anon.fields("name").tip == INT)

    assert(anon.methods.size == 1)

    val mthsOpt = anon.methods.get("sayHello")
    assert(mthsOpt.isDefined)

    val funs = mthsOpt.get.funs
    assert(funs.length == 1)

    val fun = funs.head
    assert(fun.parameters.length == 1)
    assert(fun.parameters.head.name == "this")
    assert(fun.parameters.head.tip == THIS)
    assert(fun.returns == THIS)
    assert(fun.generics.isEmpty)
  }

  val fieldsOnlyType = TObject("FieldsOnly").fields("name" -> INT, "fld2" -> INT).build
  val fieldsOnlyType2 = TObject("FieldsOnly2").fields("name" -> INT, "fld2" -> INT, "fld3" -> INT).build

  "TAnon from FieldsOnly" should "TAnon assignable FieldsOnly должно true" in {
    println("\"TAnon from FieldsOnly\" should \"TAnon assignable FieldsOnly должно true\"")
    println("=" * 80)

    val anon = TAnon.from(fieldsOnlyType)
    assert(anon.assignable(fieldsOnlyType))
  }

  "TAnon from FieldsOnly" should "TAnon assignable FieldsOnly and fieldsOnlyType2 должно true" in {
    println("\"TAnon from FieldsOnly\" should \"TAnon assignable FieldsOnly and fieldsOnlyType2 должно true\"")
    println("=" * 80)

    val anon = TAnon.from(fieldsOnlyType)
    assert(anon.assignable(fieldsOnlyType))
    assert(anon.assignable(fieldsOnlyType2))
  }

  "TAnon from User" should "TAnon assignable User должно true" in {
    println("\"TAnon from User\" should \"TAnon assignable User должно true\"")
    println("=" * 80)

    val anon = TAnon.from(userType)
    println(anon.assignable(userType))
    assert(anon.assignable(userType))
  }

  "TAnon from {fld:INT}" should "does not assignable from {fld:ANY}" in {
    println("\"TAnon from {fld:INT}\" should \"does not assignable from {fld:ANY}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields("fld" -> INT))
    assert(anon0.assignable(anon0))
    val anon1 = TAnon(Fields("fld" -> ANY))
    assert(!anon0.assignable(anon1))
  }

  "TAnon from {fld:INT}" should "does not assignable from {fld:DOUBLE}" in {
    println("\"TAnon from {fld:INT}\" should \"does not assignable from {fld:DOUBLE}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields("fld" -> INT))
    assert(anon0.assignable(anon0))
    val anon1 = TAnon(Fields("fld" -> DOUBLE))
    assert(!anon0.assignable(anon1))
  }

  "TAnon from {fld:NUMBER}" should "does assignable from {fld:DOUBLE}" in {
    println("\"TAnon from {fld:NUMBER}\" should \"does assignable from {fld:DOUBLE}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields("fld" -> NUMBER))
    assert(anon0.assignable(anon0))
    val anon1 = TAnon(Fields("fld" -> DOUBLE))
    assert(anon0.assignable(anon1))
  }

  "TAnon from {some(NUMBER):NUMBER}" should "does assignable from {some(NUMBER):NUMBER}" in {
    println("\"TAnon from {some(NUMBER):NUMBER}\" should \"does assignable from {some(NUMBER):NUMBER}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> NUMBER), NUMBER)))))
    val anon1 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> NUMBER), NUMBER)))))
    assert(anon0.assignable(anon1))
  }

  "TAnon from {some(NUMBER):NUMBER}" should "does assignable from {some(ANY):DOUBLE}" in {
    println("\"TAnon from {some(NUMBER):NUMBER}\" should \"does assignable from {some(ANY):DOUBLE}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> NUMBER), NUMBER)))))
    val anon1 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> ANY), DOUBLE)))))
    assert(anon0.assignable(anon1))
  }

  "TAnon from {some(NUMBER):NUMBER}" should "does not assignable from {some(ANY):ANY}" in {
    println("\"TAnon from {some(NUMBER):NUMBER}\" should \"does not assignable from {some(ANY):ANY}\"")
    println("=" * 80)

    val anon0 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> NUMBER), NUMBER)))))
    val anon1 = TAnon(Fields(), new Methods(Map("some" -> Funs(Fn(Params("a" -> ANY), ANY)))))
    assert(!anon0.assignable(anon1))
  }
}
