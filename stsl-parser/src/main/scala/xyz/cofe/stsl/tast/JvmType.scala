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
}
