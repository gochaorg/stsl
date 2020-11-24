package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast._
import xyz.cofe.stsl.tok._
import JvmType._
import xyz.cofe.stsl.types.{TObject, Type}

/**
 * "Тостер" - Компиляция AST выражений
 */
class Toaster( val typeScope: TypeScope ) {
  require(typeScope!=null)

  def compile( ast:AST ):TAST = {
    require(ast!=null)
    ast match {
//      case a:StackedArgumentAST => compile(a)
      case a:LiteralAST => compile(a)
//      case a:IdentifierAST => compile(a)
      case a:BinaryAST => compile(a)
//      case a:DelegateAST => compile(a)
//      case a:TernaryAST => compile(a)
//      case a:PropertyAST => compile(a)
//      case a:CallAST => compile(a)
//      case a:LambdaAST => compile(a)
      case _ => ???
    }
  }

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

  protected def call(thiz:TObject, method:String, args:List[Type] ):CallCase = {
    val cases = typeScope.callCases(thiz,method,args)
    val implArgNames = if( args.isEmpty ){
      ""
    } else if( args.size==1 ){
      s"${args.head}"
    } else {
      s"${args.map(a=>a.toString).reduce((a,b)=>a+","+"b")}"
    }
    val implName = s"${thiz} ${method}(${implArgNames})"

    if( cases.preferred.isEmpty ){
      throw ToasterError(s"implementation of $implName not found")
    }else if( cases.preferred.size>1 ){
      throw ToasterError(s"ambiguous implementation of $implName not found")
    }

    cases.preferred.head
  }

  def compile( binaryAST: BinaryAST ):TAST = {
    require(binaryAST!=null)
    val left = compile(binaryAST.left)
    val right = compile(binaryAST.right)

    val opName = binaryAST.operator.tok.name
    val callCase = call(
      left.supplierType.asInstanceOf[TObject],
      opName,
      List(left.supplierType, right.supplierType))

    val invoke = callCase.invoking()

    TAST(
      binaryAST,
      invoke._2,
      ()=>{ invoke._1.invoke( List(left.supplier.get(), right.supplier.get()) ) },
      List(left,right))
  }
}
