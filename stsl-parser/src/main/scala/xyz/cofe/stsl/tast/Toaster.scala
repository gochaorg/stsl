package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.LiteralAST
import xyz.cofe.stsl.tok.{BigIntNumberTok, ByteNumberTok, DecimalNumberTok, DoubleNumberTok, FloatNumberTok, IntNumberTok, LongNumberTok, NumberTok, ShortNumberTok, StringTok}

import JvmType._

object Toaster {
  def compile( literalAST: LiteralAST ):TAST = {
    require(literalAST!=null)
    literalAST.tok match {
      case num: FloatNumberTok => TAST( literalAST, FLOAT, ()=>num.value.floatValue() )
      case num: DoubleNumberTok => TAST( literalAST, DOUBLE, ()=>num.value.doubleValue() )
      case num: DecimalNumberTok => TAST( literalAST, DECIMAL, ()=>num.value.asInstanceOf[BigDecimal] )
      case num: ByteNumberTok => TAST( literalAST, BYTE, ()=>num.value.byteValue() )
      case num: ShortNumberTok => TAST( literalAST, SHORT, ()=>num.value.shortValue() )
      case num: IntNumberTok => TAST( literalAST, INT, ()=>num.value.intValue() )
      case num: LongNumberTok => TAST( literalAST, LONG, ()=>num.value.longValue() )
      case num: BigIntNumberTok => TAST( literalAST, BIGINT, ()=>num.value.asInstanceOf[BigInt] )
      case num: NumberTok => TAST( literalAST, NUMBER, ()=>num.value )
      case str: StringTok => TAST( literalAST, STRING, ()=>str.value )
    }
  }


}
