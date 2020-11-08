package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.Type
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.types.Param
import xyz.cofe.sel.types.Fun

object PredefFun {

  import xyz.cofe.sel.types.Fun.fn

  implicit class ScopeExt(scope: Scope) {
    def operators(name: String, fns: Seq[Fun]): Unit = {
      require(name != null)
      require(fns != null)
      val varOpt = scope.vars.map.get(name)
      var fs = if (varOpt.isDefined && varOpt.get.value != null && varOpt.get.value.isInstanceOf[Funs]) {
        varOpt.get.value.asInstanceOf[Funs]
      } else {
        new Funs(List())
      }
      fns.foreach(f => fs = fs.add(f))
      if (varOpt.isEmpty) {
        scope.vars.define(name, ARRAY, fs, readOnly = false)
      } else {
        scope.vars.set(name, ARRAY, fs)
      }
    }

    def operator(name: String, fns: Fun*): Unit = {
      require(name != null)
      require(fns != null)
      operators(name, fns.toList)
    }
  }

  def init(scope: Scope): Unit = {
    require(scope != null)
    initSpecialVars(scope)
    initNumMath(scope)
    initParamTypeConverting(scope)
    initCompareNumbers(scope)
    initBoolOperators(scope)
    initStringCompare(scope)
  }

  /**
   * Инициализация переменных true, false, null
   *
   * @param scope Область видимости
   */
  def initSpecialVars(scope: Scope): Unit = {
    require(scope != null)
    scope.vars.define("null", OBJECT, null, readOnly = true, overridable = false)
    scope.vars.define("false", BOOL, false, readOnly = true, overridable = false)
    scope.vars.define("true", BOOL, true, readOnly = true, overridable = false)
  }

  /**
   * Инициализация строковых операций
   *
   * @param scope Область видимости
   */
  def initStringCompare(scope: Scope): Unit = {
    require(scope != null)
    scope.operator("==",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => a.equals(b)) description "strEquals"
    )
    scope.operator("!=",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => !a.equals(b)) description "strNotEquals"
    )
    scope.operator("<",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => a < b) description "strLess"
    )
    scope.operator("<=",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => a <= b) description "strLessOrEquals"
    )
    scope.operator(">",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => a > b) description "strMore"
    )
    scope.operator(">=",
      fn("a", STRING, "b", STRING, BOOL, (a: String, b: String) => a >= b) description "strMoreOrEquals"
    )
  }

  //#region boolean operator

  /**
   * Инициализация логических операций
   *
   * @param scope Область видимости
   */
  def initBoolOperators(scope: Scope): Unit = {
    require(scope != null)
    scope.operators("&", List(
      fn("a", BOOL, "b", BOOL, BOOL, (a: Boolean, b: Boolean) => a && b) description "andBool"
    ))
    scope.operators("|", List(
      fn("a", BOOL, "b", BOOL, BOOL, (a: Boolean, b: Boolean) => a || b) description "orBool"
    ))
    scope.operators("!", List(
      fn("a", BOOL, BOOL, (a: Boolean) => !a) description "notBool"
    ))
  }

  //#endregion
  //#region implicit num convert

  val IMPLICIT_PARAM_TYPE_CONV = "$implicit"

  /**
   * Инициализация конверсии числовых типов данных
   *
   * @param scope Область видимости
   */
  def initParamTypeConverting(scope: Scope): Unit = {
    require(scope != null)
    scope.vars.define(IMPLICIT_PARAM_TYPE_CONV, ARRAY, new Funs(List(
      byte2short, byte2int, byte2long, byte2float, byte2double, byte2bigInt, byte2decimal,
      short2int, short2long, short2float, short2double, short2bigInt, short2decimal,
      int2long, int2float, int2double, int2bigInt, int2decimal,
      long2float, long2double, long2bigInt, long2decimal,
      float2double, float2bigInt, float2decimal,
      double2bigInt, double2decimal,
      bigInt2decimal
    )), readOnly = true)
  }

  val byte2short: Fun = fn("a", BYTE, SHORT, (n: Byte) => n.toShort) description "byte2short"
  val byte2int: Fun = fn("a", BYTE, INT, (n: Byte) => n.toInt) description "byte2int"
  val byte2long: Fun = fn("a", BYTE, LONG, (n: Byte) => n.toLong) description "byte2long"
  val byte2float: Fun = fn("a", BYTE, FLOAT, (n: Byte) => n.toFloat) description "byte2float"
  val byte2double: Fun = fn("a", BYTE, DOUBLE, (n: Byte) => n.toDouble) description "byte2double"
  val byte2bigInt: Fun = fn("a", BYTE, BIGINT, (n: Byte) => BigInt.int2bigInt(n)) description "byte2bigInt"
  val byte2decimal: Fun = fn("a", BYTE, DECIMAL, (n: Byte) => BigDecimal.decimal(n)) description "byte2decimal"

  val short2int: Fun = fn("a", SHORT, INT, (n: Short) => n.toInt) description "short2int"
  val short2long: Fun = fn("a", SHORT, LONG, (n: Short) => n.toLong) description "short2long"
  val short2float: Fun = fn("a", SHORT, FLOAT, (n: Short) => n.toFloat) description "short2float"
  val short2double: Fun = fn("a", SHORT, DOUBLE, (n: Short) => n.toDouble) description "short2double"
  val short2bigInt: Fun = fn("a", SHORT, BIGINT, (n: Short) => BigInt.int2bigInt(n)) description "short2bigInt"
  val short2decimal: Fun = fn("a", SHORT, DECIMAL, (n: Short) => BigDecimal.decimal(n)) description "short2decimal"

  val int2long: Fun = fn("a", INT, LONG, (n: Int) => n.toLong) description "int2long"
  val int2float: Fun = fn("a", INT, FLOAT, (n: Int) => n.toFloat) description "int2float"
  val int2double: Fun = fn("a", INT, DOUBLE, (n: Int) => n.toDouble) description "int2double"
  val int2bigInt: Fun = fn("a", INT, BIGINT, (n: Int) => BigInt.int2bigInt(n)) description "int2bigInt"
  val int2decimal: Fun = fn("a", INT, DECIMAL, (n: Int) => BigDecimal.decimal(n)) description "int2decimal"

  val long2float: Fun = fn("a", LONG, FLOAT, (n: Long) => n.toFloat) description "long2float"
  val long2double: Fun = fn("a", LONG, DOUBLE, (n: Long) => n.toDouble) description "long2double"
  val long2bigInt: Fun = fn("a", LONG, BIGINT, (n: Long) => BigInt.long2bigInt(n)) description "long2bigInt"
  val long2decimal: Fun = fn("a", LONG, DECIMAL, (n: Long) => BigDecimal.decimal(n)) description "long2decimal"

  val float2double: Fun = fn("a", FLOAT, DOUBLE, (n: Float) => n.toDouble) description "float2double"
  val float2bigInt: Fun = fn("a", FLOAT, BIGINT, (n: Float) => BigInt.long2bigInt(n.toLong)) description "float2bigInt"
  val float2decimal: Fun = fn("a", FLOAT, DECIMAL, (n: Float) => BigDecimal.decimal(n)) description "float2decimal"

  val double2bigInt: Fun = fn("a", DOUBLE, BIGINT, (n: Double) => BigDecimal.decimal(n).toBigInt()) description "double2bigInt"
  val double2decimal: Fun = fn("a", DOUBLE, DECIMAL, (n: Double) => BigDecimal.decimal(n)) description "double2decimal"

  val bigInt2decimal: Fun = fn("a", BIGINT, DECIMAL, (n: BigInt) => BigDecimal.apply(n)) description "double2decimal"

  //#endregion
  //#region num compare

  /**
   * Инициализация операция сравнения чисел
   *
   * @param scope Область видимости
   */
  def initCompareNumbers(scope: Scope): Unit = {
    require(scope != null)
    scope.operators("==", List(
      cmpEqualsByte, cmpEqualsShort, cmpEqualsInt, cmpEqualsLong,
      cmpEqualsFloat, cmpEqualsDouble,
      cmpEqualsBigInt, cmpEqualsDecimal
    ))
    scope.operators("!=", List(
      cmpNotEqualsByte, cmpNotEqualsShort, cmpNotEqualsInt, cmpNotEqualsLong,
      cmpNotEqualsFloat, cmpNotEqualsDouble,
      cmpNotEqualsBigInt, cmpNotEqualsDecimal
    ))
    scope.operators("<", List(
      cmpLessByte, cmpLessShort, cmpLessInt, cmpLessLong,
      cmpLessFloat, cmpLessDouble,
      cmpLessBigInt, cmpLessDecimal
    ))
    scope.operators(">", List(
      cmpMoreByte, cmpMoreShort, cmpMoreInt, cmpMoreLong,
      cmpMoreFloat, cmpMoreDouble,
      cmpMoreBigInt, cmpMoreDecimal
    ))
    scope.operators("<=", List(
      cmpLessOrEqualsByte, cmpLessOrEqualsShort, cmpLessOrEqualsInt, cmpLessOrEqualsLong,
      cmpLessOrEqualsFloat, cmpLessOrEqualsDouble,
      cmpLessOrEqualsBigInt, cmpLessOrEqualsDecimal
    ))
    scope.operators(">=", List(
      cmpMoreOrEqualsByte, cmpMoreOrEqualsShort, cmpMoreOrEqualsInt, cmpMoreOrEqualsLong,
      cmpMoreOrEqualsFloat, cmpMoreOrEqualsDouble,
      cmpMoreOrEqualsBigInt, cmpMoreOrEqualsDecimal
    ))
  }

  val cmpEqualsByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a == b))
  val cmpNotEqualsByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a != b))
  val cmpLessByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a < b))
  val cmpLessOrEqualsByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a <= b))
  val cmpMoreByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a > b))
  val cmpMoreOrEqualsByte: Fun = fn("a", BYTE, "b", BYTE, BOOL, ((a: Byte, b: Byte) => a >= b))

  val cmpEqualsShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a == b))
  val cmpNotEqualsShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a != b))
  val cmpLessShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a < b))
  val cmpLessOrEqualsShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a <= b))
  val cmpMoreShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a > b))
  val cmpMoreOrEqualsShort: Fun = fn("a", SHORT, "b", SHORT, BOOL, ((a: Short, b: Short) => a >= b))

  val cmpEqualsInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a == b))
  val cmpNotEqualsInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a != b))
  val cmpLessInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a < b))
  val cmpLessOrEqualsInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a <= b))
  val cmpMoreInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a > b))
  val cmpMoreOrEqualsInt: Fun = fn("a", INT, "b", INT, BOOL, ((a: Int, b: Int) => a >= b))

  val cmpEqualsLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a == b))
  val cmpNotEqualsLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a != b))
  val cmpLessLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a < b))
  val cmpLessOrEqualsLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a <= b))
  val cmpMoreLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a > b))
  val cmpMoreOrEqualsLong: Fun = fn("a", LONG, "b", LONG, BOOL, ((a: Long, b: Long) => a >= b))

  val cmpEqualsBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a == b))
  val cmpNotEqualsBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a != b))
  val cmpLessBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a < b))
  val cmpLessOrEqualsBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a <= b))
  val cmpMoreBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a > b))
  val cmpMoreOrEqualsBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BOOL, ((a: BigInt, b: BigInt) => a >= b))

  val cmpEqualsFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a == b))
  val cmpNotEqualsFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a != b))
  val cmpLessFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a < b))
  val cmpLessOrEqualsFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a <= b))
  val cmpMoreFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a > b))
  val cmpMoreOrEqualsFloat: Fun = fn("a", FLOAT, "b", FLOAT, BOOL, ((a: Float, b: Float) => a >= b))

  val cmpEqualsDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a == b))
  val cmpNotEqualsDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a != b))
  val cmpLessDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a < b))
  val cmpLessOrEqualsDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a <= b))
  val cmpMoreDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a > b))
  val cmpMoreOrEqualsDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, BOOL, ((a: Double, b: Double) => a >= b))

  val cmpEqualsDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a == b))
  val cmpNotEqualsDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a != b))
  val cmpLessDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a < b))
  val cmpLessOrEqualsDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a <= b))
  val cmpMoreDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a > b))
  val cmpMoreOrEqualsDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, BOOL, ((a: BigDecimal, b: BigDecimal) => a >= b))

  //#endregion
  //#region num math

  /**
   * Инициализая мат операций
   *
   * @param scope Область видимости
   */
  def initNumMath(scope: Scope): Unit = {
    scope.operators("+", List(addByte, addShort, addInt, addLong, addFloat, addDouble, addBigInt, addBigDecimal))
    scope.operators("-", List(subByte, subShort, subInt, subLong, subFloat, subDouble, subBigInt, subBigDecimal))
    scope.operators("*", List(mulByte, mulShort, mulInt, mulLong, mulFloat, mulDouble, mulBigInt, mulBigDecimal))
    scope.operators("/", List(divByte, divShort, divInt, divLong, divFloat, divDouble, divBigInt, divBigDecimal))
    scope.operators("%", List(remainderByte, remainderShort, remainderInt, remainderLong, remainderFloat,
      remainderDouble, remainderBigInt, remainderDecimal))
  }

  val remainderByte: Fun = fn("a", BYTE, "b", BYTE, BYTE, ((a: Byte, b: Byte) => (a % b).toByte))
  val remainderShort: Fun = fn("a", SHORT, "b", SHORT, SHORT, ((a: Short, b: Short) => (a % b).toShort))
  val remainderInt: Fun = fn("a", INT, "b", INT, INT, (a: Int, b: Int) => (a % b).toInt)
  val remainderLong: Fun = fn("a", LONG, "b", LONG, LONG, (a: Long, b: Long) => a % b)
  val remainderFloat: Fun = fn("a", FLOAT, "b", FLOAT, FLOAT, ((a: Float, b: Float) => (a % b).toFloat))
  val remainderDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, DOUBLE, ((a: Double, b: Double) => (a % b).toDouble))
  val remainderBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BIGINT, ((a: BigInt, b: BigInt) => a % b))
  val remainderDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, DECIMAL, ((a: BigDecimal, b: BigDecimal) => a % b))

  val addByte: Fun = fn("a", BYTE, "b", BYTE, BYTE, ((a: Byte, b: Byte) => (a + b).toByte))
  val addShort: Fun = fn("a", SHORT, "b", SHORT, SHORT, ((a: Short, b: Short) => (a + b).toShort))
  val addInt: Fun = fn("a", INT, "b", INT, INT, ((a: Int, b: Int) => a + b))
  val addLong: Fun = fn("a", LONG, "b", LONG, LONG, ((a: Long, b: Long) => a + b))
  val addFloat: Fun = fn("a", FLOAT, "b", FLOAT, FLOAT, ((a: Float, b: Float) => a + b))
  val addDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, DOUBLE, ((a: Double, b: Double) => a + b))
  val addBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BIGINT, ((a: BigInt, b: BigInt) => a + b))
  val addBigDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, DECIMAL, ((a: BigDecimal, b: BigDecimal) => a + b))

  val subByte: Fun = fn("a", BYTE, "b", BYTE, BYTE, ((a: Byte, b: Byte) => (a - b).toByte))
  val subShort: Fun = fn("a", SHORT, "b", SHORT, SHORT, ((a: Short, b: Short) => (a - b).toShort))
  val subInt: Fun = fn("a", INT, "b", INT, INT, ((a: Int, b: Int) => a - b))
  val subLong: Fun = fn("a", LONG, "b", LONG, LONG, ((a: Long, b: Long) => a - b))
  val subFloat: Fun = fn("a", FLOAT, "b", FLOAT, FLOAT, ((a: Float, b: Float) => a - b))
  val subDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, DOUBLE, ((a: Double, b: Double) => a - b))
  val subBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BIGINT, ((a: BigInt, b: BigInt) => a - b))
  val subBigDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, DECIMAL, ((a: BigDecimal, b: BigDecimal) => a - b))

  val mulByte: Fun = fn("a", BYTE, "b", BYTE, BYTE, ((a: Byte, b: Byte) => (a * b).toByte))
  val mulShort: Fun = fn("a", INT, "b", SHORT, SHORT, ((a: Short, b: Short) => (a * b).toShort))
  val mulInt: Fun = fn("a", INT, "b", INT, INT, ((a: Int, b: Int) => a * b))
  val mulLong: Fun = fn("a", LONG, "b", LONG, LONG, ((a: Long, b: Long) => a * b))
  val mulFloat: Fun = fn("a", FLOAT, "b", FLOAT, FLOAT, ((a: Float, b: Float) => a * b))
  val mulDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, DOUBLE, ((a: Double, b: Double) => a * b))
  val mulBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BIGINT, ((a: BigInt, b: BigInt) => a * b))
  val mulBigDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, DECIMAL, ((a: BigDecimal, b: BigDecimal) => a * b))

  val divByte: Fun = fn("a", BYTE, "b", BYTE, BYTE, ((a: Byte, b: Byte) => (a / b).toByte))
  val divShort: Fun = fn("a", SHORT, "b", SHORT, SHORT, ((a: Short, b: Short) => (a / b).toShort))
  val divInt: Fun = fn("a", INT, "b", INT, INT, ((a: Int, b: Int) => a / b))
  val divLong: Fun = fn("a", LONG, "b", LONG, LONG, ((a: Long, b: Long) => a / b))
  val divFloat: Fun = fn("a", FLOAT, "b", FLOAT, FLOAT, ((a: Float, b: Float) => a / b))
  val divDouble: Fun = fn("a", DOUBLE, "b", DOUBLE, DOUBLE, ((a: Double, b: Double) => a / b))
  val divBigInt: Fun = fn("a", BIGINT, "b", BIGINT, BIGINT, ((a: BigInt, b: BigInt) => a / b))
  val divBigDecimal: Fun = fn("a", DECIMAL, "b", DECIMAL, DECIMAL, ((a: BigDecimal, b: BigDecimal) => a / b))

  //#endregion
}
