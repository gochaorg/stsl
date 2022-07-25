package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type.{ANY, THIS}
import xyz.cofe.stsl.types.{Fn, Params, TObject, Type, TypeVariable}

/**
 * Предопределенные типы
 */
object JvmType {
  //region declare types
  //region BOOLEAN
  /**
   * Соответствует jvm типу boolean
   */
  val BOOLEAN: TObject = new TObject("bool")
  BOOLEAN.extend(ANY)
  BOOLEAN.methods += "&&" -> Fn(
    Params(
      "self" -> THIS,
      "value" -> THIS
    ),
    THIS
  ).invoke[Boolean,Boolean,Boolean]((self,value)=>self && value)
  BOOLEAN.methods += "&" -> Fn(
    Params(
      "self" -> THIS,
      "value" -> THIS
    ),
    THIS
  ).invoke[Boolean,Boolean,Boolean]((self,value)=>self && value)
  BOOLEAN.methods += "||" -> Fn(
    Params(
      "self" -> Type.THIS,
      "value" -> Type.THIS
    ),
    Type.THIS
  ).invoke[Boolean,Boolean,Boolean]((self,value)=>self || value)
  BOOLEAN.methods += "|" -> Fn(
    Params(
      "self" -> Type.THIS,
      "value" -> Type.THIS
    ),
    Type.THIS
  ).invoke[Boolean,Boolean,Boolean]((self,value)=>self || value)
  BOOLEAN.methods += "!" -> Fn(
    Params(
      "self" -> Type.THIS
    ),
    Type.THIS
  ).invoke[Boolean,Boolean](self => !self)
  //BOOLEAN.freeze

  //endregion
  //region CHAR
  /**
   * Соответствует jvm типу char
   */
  val CHAR : TObject = TObject("char")
    .extend(ANY)
    .build
  //endregion
  //region NUMBER
  /**
   * Соответствует jvm типу number
   */
  val NUMBER : TObject = TObject("number")
    .extend(ANY)
    .build
  //endregion
  //region BYTE
  /**
   * Соответствует jvm типу byte
   */
  val BYTE : TObject = TObject("byte")
    .extend(NUMBER)
    .build
  //endregion
  //region SHORT
  /**
   * Соответствует jvm типу short
   */
  val SHORT : TObject = TObject("short")
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
  val FLOAT : TObject = TObject("float")
    .extend(NUMBER)
    .build
  //endregion
  //region DOUBLE
  /**
   * Соответствует jvm типу double
   */
  val DOUBLE : TObject = TObject("double")
    .extend(NUMBER)
    .build
  //endregion
  //region BIGINT
  /**
   * Соответствует jvm типу BigInteger
   */
  val BIGINT : TObject = TObject("BigInt")
    .extend(NUMBER)
    .build
  //endregion
  //region DECIMAL
  /**
   * Соответствует jvm типу BigDecimal
   */
  val DECIMAL : TObject = TObject("decimal")
    .extend(NUMBER)
    .build
  //endregion
  //region STRING
  /**
   * Соответствует jvm типу string
   */
  val STRING : TObject = TObject("string")
    .extend(ANY)
    .build
  //endregion
  //endregion

  //region BYTE implementation
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Byte,Byte,Int]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> SHORT),INT).invoke[Byte,Short,Int]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Byte,Int,Int]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Byte,Long,Long]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Byte,Float,Float]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Byte,Double,Double]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Byte,BigInt,BigInt]((self,value)=>self + value)
  BYTE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Byte,BigDecimal,BigDecimal]((self,value)=>self + value)

  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Byte,Byte,Int]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> SHORT),INT).invoke[Byte,Short,Int]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Byte,Int,Int]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Byte,Long,Long]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Byte,Float,Float]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Byte,Double,Double]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Byte,BigInt,BigInt]((self,value)=>self - value)
  BYTE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Byte,BigDecimal,BigDecimal]((self,value)=>self - value)

  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Byte,Byte,Int]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> SHORT),INT).invoke[Byte,Short,Int]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Byte,Int,Int]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Byte,Long,Long]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Byte,Float,Float]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Byte,Double,Double]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Byte,BigInt,BigInt]((self,value)=>self * value)
  BYTE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Byte,BigDecimal,BigDecimal]((self,value)=>self * value)

  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Byte,Byte,Int]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> SHORT),INT).invoke[Byte,Short,Int]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Byte,Int,Int]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Byte,Long,Long]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Byte,Float,Float]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Byte,Double,Double]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Byte,BigInt,BigInt]((self,value)=>self / value)
  BYTE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Byte,BigDecimal,BigDecimal]((self,value)=>self / value)

  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Byte,Byte,Int]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> SHORT),INT).invoke[Byte,Short,Int]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Byte,Int,Int]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Byte,Long,Long]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Byte,Float,Float]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Byte,Double,Double]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Byte,BigInt,BigInt]((self,value)=>self % value)
  BYTE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Byte,BigDecimal,BigDecimal]((self,value)=>self % value)

  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self==value)
  BYTE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self==value)

  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self!=value)
  BYTE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self!=value)

  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self.toInt<value)
  BYTE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self.toInt<value)

  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self.toInt>value)
  BYTE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self.toInt>value)

  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self.toInt<=value)
  BYTE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self.toInt<=value)

  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Byte,Byte,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Byte,Short,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Byte,Int,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Byte,Long,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Byte,Float,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Byte,Double,Boolean]((self,value) => self>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Byte,BigInt,Boolean]((self,value) => self.toInt>=value)
  BYTE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Byte,BigDecimal,Boolean]((self,value) => self.toInt>=value)

  //BYTE.freeze
  //endregion
  //region SHORT implementation
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BYTE),INT).invoke[Short,Byte,Int]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Short,Short,Int]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Short,Int,Int]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Short,Long,Long]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Short,Float,Float]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Short,Double,Double]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Short,BigInt,BigInt]((self,value)=>self + value)
  SHORT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Short,BigDecimal,BigDecimal]((self,value)=>self + value)

  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BYTE),INT).invoke[Short,Byte,Int]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Short,Short,Int]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Short,Int,Int]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Short,Long,Long]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Short,Float,Float]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Short,Double,Double]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Short,BigInt,BigInt]((self,value)=>self - value)
  SHORT.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Short,BigDecimal,BigDecimal]((self,value)=>self - value)

  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BYTE),INT).invoke[Short,Byte,Int]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Short,Short,Int]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Short,Int,Int]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Short,Long,Long]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Short,Float,Float]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Short,Double,Double]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Short,BigInt,BigInt]((self,value)=>self * value)
  SHORT.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Short,BigDecimal,BigDecimal]((self,value)=>self * value)

  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BYTE),INT).invoke[Short,Byte,Int]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Short,Short,Int]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Short,Int,Int]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Short,Long,Long]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Short,Float,Float]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Short,Double,Double]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Short,BigInt,BigInt]((self,value)=>self / value)
  SHORT.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Short,BigDecimal,BigDecimal]((self,value)=>self / value)

  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BYTE),INT).invoke[Short,Byte,Int]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> THIS),INT).invoke[Short,Short,Int]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> INT),INT).invoke[Short,Int,Int]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Short,Long,Long]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Short,Float,Float]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Short,Double,Double]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Short,BigInt,BigInt]((self,value)=>self % value)
  SHORT.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Short,BigDecimal,BigDecimal]((self,value)=>self % value)

  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self==value)
  SHORT.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self==value)

  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self!=value)
  SHORT.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self!=value)

  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self.toInt<value)
  SHORT.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self.toInt<value)

  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self.toInt>value)
  SHORT.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self.toInt>value)

  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self.toInt<=value)
  SHORT.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self.toInt<=value)

  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Short,Byte,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Short,Short,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Short,Int,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Short,Long,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Short,Float,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Short,Double,Boolean]((self,value) => self>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Short,BigInt,Boolean]((self,value) => self.toInt>=value)
  SHORT.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Short,BigDecimal,Boolean]((self,value) => self.toInt>=value)

  //SHORT.freeze
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

  //INT.freeze
  //endregion
  //region LONG implementation
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Long,Byte,Long]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Long,Short,Long]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Long,Int,Long]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Long,Long]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Long,Double,Double]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self + value)
  LONG.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self + value)

  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Long,Byte,Long]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Long,Short,Long]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Long,Int,Long]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Long,Long]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Long,Double,Double]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self - value)
  LONG.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self - value)

  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Long,Byte,Long]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Long,Short,Long]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Long,Int,Long]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Long,Long]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Long,Double,Double]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self * value)
  LONG.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self * value)

  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Long,Byte,Long]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Long,Short,Long]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Long,Int,Long]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Long,Long]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Long,Double,Double]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self / value)
  LONG.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self / value)

  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Long,Byte,Long]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Long,Short,Long]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Long,Int,Long]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Long,Long]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),DOUBLE).invoke[Long,Double,Double]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self % value)
  LONG.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self % value)

  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self==value)
  LONG.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self==value)

  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self!=value)
  LONG.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self!=value)

  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self<value)
  LONG.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self<value)

  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self>value)
  LONG.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self>value)

  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self<=value)
  LONG.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self<=value)

  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Long,Byte,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Long,Short,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Long,Int,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Long,Long,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Long,Float,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DOUBLE),BOOLEAN).invoke[Long,Double,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Long,BigInt,Boolean]((self,value) => self>=value)
  LONG.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Long,BigDecimal,Boolean]((self,value) => self>=value)

  //LONG.freeze
  //endregion
  //region DOUBLE implementation
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Double,Byte,Double]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Double,Short,Double]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Double,Int,Double]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Double,Long,Double]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Double,Double]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self + value)
  DOUBLE.methods += "+" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self + value)

  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Double,Byte,Double]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Double,Short,Double]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Double,Int,Double]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Double,Long,Double]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Double,Double]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self - value)
  DOUBLE.methods += "-" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self - value)

  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Double,Byte,Double]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Double,Short,Double]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Double,Int,Double]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Double,Long,Double]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Double,Double]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self * value)
  DOUBLE.methods += "*" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self * value)

  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Double,Byte,Double]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Double,Short,Double]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Double,Int,Double]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Double,Long,Double]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Double,Double]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self / value)
  DOUBLE.methods += "/" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self / value)

  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Double,Byte,Double]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> SHORT),THIS).invoke[Double,Short,Double]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> INT),THIS).invoke[Double,Int,Double]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> LONG),LONG).invoke[Double,Long,Double]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> FLOAT),FLOAT).invoke[Long,Float,Float]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> THIS),THIS).invoke[Long,Double,Double]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BIGINT).invoke[Long,BigInt,BigInt]((self,value)=>self % value)
  DOUBLE.methods += "%" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),DECIMAL).invoke[Long,BigDecimal,BigDecimal]((self,value)=>self % value)

  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => self==value)
  DOUBLE.methods += "==" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self==value)

  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => self!=value)
  DOUBLE.methods += "!=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self!=value)

  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self<value)
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => BigDecimal(self)<BigDecimal(value))
  DOUBLE.methods += "<" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self<value)

  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self>value)
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => BigDecimal(self)>BigDecimal(value))
  DOUBLE.methods += ">" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self>value)

  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self<=value)
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => BigDecimal(self)<=BigDecimal(value))
  DOUBLE.methods += "<=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self<=value)

  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BYTE),BOOLEAN).invoke[Double,Byte,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> SHORT),BOOLEAN).invoke[Double,Short,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> INT),BOOLEAN).invoke[Double,Int,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> LONG),BOOLEAN).invoke[Double,Long,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> FLOAT),BOOLEAN).invoke[Double,Float,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> THIS),BOOLEAN).invoke[Double,Double,Boolean]((self,value) => self>=value)
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> BIGINT),BOOLEAN).invoke[Double,BigInt,Boolean]((self,value) => BigDecimal(self)>=BigDecimal(value))
  DOUBLE.methods += ">=" -> Fn(Params("self" -> THIS, "value" -> DECIMAL),BOOLEAN).invoke[Double,BigDecimal,Boolean]((self,value) => self>=value)

  //DOUBLE.freeze
  //endregion
  //region STRING implementation
  STRING.fields ++= "length"-> INT -> ((str:Any)=>str.asInstanceOf[String].length) -> ((str:Any, l:Any)=>throw new RuntimeException("immutable"))
  STRING.methods += "substring" -> Fn(Params("self"->THIS, "beginIndex"->INT),THIS).invoke[String,Int,String]((self,idx)=>self.substring(idx))
  STRING.methods += "substring" -> Fn(Params("self"->THIS, "beginIndex"->INT, "endIndex"->INT),THIS).invoke[String,Int,Int,String]((self,idx,eidx)=>self.substring(idx,eidx))
  STRING.methods += "+" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,String]((self,value)=>self + value)
  STRING.methods += "==" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self == value)
  STRING.methods += "!=" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self != value)
  STRING.methods += "<" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self < value)
  STRING.methods += ">" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self > value)
  STRING.methods += "<=" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self <= value)
  STRING.methods += ">=" -> Fn(Params("self"->THIS, "value"->THIS),THIS).invoke[String,String,Boolean]((self,value)=>self >= value)
  //STRING.freeze
  //endregion

  //region implicitConversion

  val implicitConversion = List(
    Fn( Params( "value" -> BYTE ), SHORT ).invoke[Byte,Short]( value => value.toShort ),
    Fn( Params( "value" -> BYTE ), INT ).invoke[Byte,Int]( value => value.toInt ),
    Fn( Params( "value" -> BYTE ), LONG ).invoke[Byte,Long]( value => value.toLong ),
    Fn( Params( "value" -> BYTE ), FLOAT ).invoke[Byte,Float]( value => value.toFloat ),
    Fn( Params( "value" -> BYTE ), DOUBLE ).invoke[Byte,Double]( value => value.toDouble ),
    Fn( Params( "value" -> BYTE ), BIGINT ).invoke[Byte,BigInt]( value => BigInt(value) ),
    Fn( Params( "value" -> BYTE ), DECIMAL ).invoke[Byte,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> SHORT ), INT ).invoke[Short,Int]( value => value.toInt ),
    Fn( Params( "value" -> SHORT ), LONG ).invoke[Short,Long]( value => value.toLong ),
    Fn( Params( "value" -> SHORT ), FLOAT ).invoke[Short,Float]( value => value.toFloat ),
    Fn( Params( "value" -> SHORT ), DOUBLE ).invoke[Short,Double]( value => value.toDouble ),
    Fn( Params( "value" -> SHORT ), BIGINT ).invoke[Short,BigInt]( value => BigInt(value) ),
    Fn( Params( "value" -> SHORT ), DECIMAL ).invoke[Short,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> INT ), LONG ).invoke[Int,Long]( value => value.toLong ),
    Fn( Params( "value" -> INT ), FLOAT ).invoke[Int,Float]( value => value.toFloat ),
    Fn( Params( "value" -> INT ), DOUBLE ).invoke[Int,Double]( value => value.toDouble ),
    Fn( Params( "value" -> INT ), BIGINT ).invoke[Int,BigInt]( value => BigInt(value) ),
    Fn( Params( "value" -> INT ), DECIMAL ).invoke[Int,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> LONG ), FLOAT ).invoke[Long,Float]( value => value.toFloat ),
    Fn( Params( "value" -> LONG ), DOUBLE ).invoke[Long,Double]( value => value.toDouble ),
    Fn( Params( "value" -> LONG ), BIGINT ).invoke[Long,BigInt]( value => BigInt(value) ),
    Fn( Params( "value" -> LONG ), DECIMAL ).invoke[Long,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> FLOAT ), DOUBLE ).invoke[Float,Double]( value => value.toDouble ),
    Fn( Params( "value" -> FLOAT ), BIGINT ).invoke[Float,BigInt]( value => BigInt(value.toLong) ),
    Fn( Params( "value" -> FLOAT ), DECIMAL ).invoke[Float,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> DOUBLE ), BIGINT ).invoke[Double,BigInt]( value => BigInt(value.toLong) ),
    Fn( Params( "value" -> DOUBLE ), DECIMAL ).invoke[Double,BigDecimal]( value => BigDecimal(value) ),

    Fn( Params( "value" -> BIGINT ), DECIMAL ).invoke[BigInt,BigDecimal]( value => BigDecimal(value) ),
  )
  //endregion
  
  val types = List(
    STRING, BOOLEAN, CHAR, NUMBER, BYTE, SHORT, INT, LONG, FLOAT, DOUBLE, BIGINT, DECIMAL
  )
}
