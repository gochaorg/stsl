package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type.{ANY, THIS}
import xyz.cofe.stsl.types.{Fn, Params, TObject, Type}

/**
 * Предопределенные типы
 */
object JvmType {
  //region declare types
  //region BOOLEAN
  /**
   * Соответствует jvm типу boolean
   */
  val BOOLEAN : Type = TObject("bool")
    .extend(ANY)
    .methods(
      "&&" -> Fn(
        Params(
          "self" -> THIS,
          "value" -> THIS
        ),
        THIS
      ).invoke[Boolean,Boolean,Boolean]((self,value)=>self && value),
      "||" -> Fn(
        Params(
          "self" -> Type.THIS,
          "value" -> Type.THIS
        ),
        Type.THIS
      ).invoke[Boolean,Boolean,Boolean]((self,value)=>self || value),
      "!" -> Fn(
        Params(
          "self" -> Type.THIS
        ),
        Type.THIS
      ).invoke[Boolean,Boolean](self => !self)
    )
    .build
  //endregion
  //region CHAR
  /**
   * Соответствует jvm типу char
   */
  val CHAR : Type = TObject("char")
    .extend(ANY)
    .build
  //endregion
  //region NUMBER
  /**
   * Соответствует jvm типу number
   */
  val NUMBER : Type = TObject("number")
    .extend(ANY)
    .build
  //endregion
  //region BYTE
  /**
   * Соответствует jvm типу byte
   */
  val BYTE : Type = TObject("byte")
    .extend(NUMBER)
    .build
  //endregion
  //region SHORT
  /**
   * Соответствует jvm типу short
   */
  val SHORT : Type = TObject("short")
    .extend(NUMBER)
    .build
  //endregion
  //region INT
  /**
   * Соответствует jvm типу int
   */
  val INT : TObject = TObject("int")
    .extend(NUMBER).build
  //endregion
  //region LONG
  /**
   * Соответствует jvm типу long
   */
  val LONG : TObject = TObject("long")
    .extend(NUMBER)
    .build
  //endregion
  //region FLOAT
  /**
   * Соответствует jvm типу float
   */
  val FLOAT : Type = TObject("float")
    .extend(NUMBER)
    .build
  //endregion
  //region DOUBLE
  /**
   * Соответствует jvm типу double
   */
  val DOUBLE : Type = TObject("double")
    .extend(NUMBER)
    .build
  //endregion
  //region BIGINT
  /**
   * Соответствует jvm типу BigInteger
   */
  val BIGINT : Type = TObject("BigInt")
    .extend(NUMBER)
    .build
  //endregion
  //region DECIMAL
  /**
   * Соответствует jvm типу BigDecimal
   */
  val DECIMAL : Type = TObject("decimal")
    .extend(NUMBER)
    .build
  //endregion
  //region STRING
  /**
   * Соответствует jvm типу string
   */
  val STRING : Type = TObject("string")
    .extend(ANY)
    .build
  //endregion
  //endregion

  //region INT implementation
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Int,Short,Int]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Int,Int,Int]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Int,Long,Long]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Int,Float,Float]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Int,Double,Double]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Int,BigInt,BigInt]((self,value)=>self + value)
  INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Int,BigDecimal,BigDecimal]((self,value)=>self + value)

  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Int,Short,Int]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Int,Int,Int]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Int,Long,Long]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Int,Float,Float]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Int,Double,Double]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Int,BigInt,BigInt]((self,value)=>self - value)
  INT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Int,BigDecimal,BigDecimal]((self,value)=>self - value)

  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Int,Short,Int]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Int,Int,Int]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Int,Long,Long]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Int,Float,Float]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Int,Double,Double]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Int,BigInt,BigInt]((self,value)=>self * value)
  INT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Int,BigDecimal,BigDecimal]((self,value)=>self * value)

  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Int,Short,Int]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Int,Int,Int]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Int,Long,Long]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Int,Float,Float]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Int,Double,Double]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Int,BigInt,BigInt]((self,value)=>self / value)
  INT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Int,BigDecimal,BigDecimal]((self,value)=>self / value)

  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Int,Short,Int]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Int,Int,Int]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Int,Long,Long]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Int,Float,Float]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Int,Double,Double]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Int,BigInt,BigInt]((self,value)=>self % value)
  INT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Int,BigDecimal,BigDecimal]((self,value)=>self % value)

  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self==value)
  INT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self==value)

  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self!=value)
  INT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self!=value)

  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self<value)
  INT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self<value)

  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self>value)
  INT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self>value)

  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self<=value)
  INT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self<=value)

  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Int,Byte,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Int,Short,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Int,Int,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Int,Long,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Int,Float,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Int,Double,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Int,BigInt,Boolean]((self,value) => self>=value)
  INT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Int,BigDecimal,Boolean]((self,value) => self>=value)

  INT.freeze
  //endregion
}
