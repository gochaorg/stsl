package xyz.cofe.stsl.types

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ExtendableSpec extends AnyFlatSpec with Matchers {

  import JvmType._
  import Type._

  implicit val trace = AssignableTracer(System.out)

  "Проверка иерархии типов" should "ANY = ANY" in assert(ANY.assignable(ANY))
  it should "VOID = VOID" in assert(VOID.assignable(VOID))
  it should "NUMBER = NUMBER" in assert(NUMBER.assignable(NUMBER))
  it should "INT = INT" in assert(INT.assignable(INT))
  it should "DOUBLE = DOUBLE" in assert(DOUBLE.assignable(DOUBLE))

  it should "!VOID = ANY" in assert(!VOID.assignable(ANY))
  it should "!ANY = VOID" in assert(!ANY.assignable(VOID))

  it should "ANY = NUMBER" in assert(ANY.assignable(NUMBER))
  it should "!NUMBER = ANY" in assert(!NUMBER.assignable(ANY))

  it should "ANY = INT" in assert(ANY.assignable(INT))
  it should "NUMBER = INT" in assert(NUMBER.assignable(INT))
  it should "!INT = NUMBER" in assert(!INT.assignable(NUMBER))
  it should "!INT = ANY" in assert(!INT.assignable(ANY))

  it should "ANY = DOUBLE" in assert(ANY.assignable(DOUBLE))
  it should "NUMBER = DOUBLE" in assert(NUMBER.assignable(DOUBLE))
  it should "!DOUBLE = NUMBER" in assert(!DOUBLE.assignable(NUMBER))
  it should "!DOUBLE = ANY" in assert(!DOUBLE.assignable(ANY))

  it should "extendPath" in assert(DOUBLE.extendPath == List(ANY, NUMBER, DOUBLE))
}
