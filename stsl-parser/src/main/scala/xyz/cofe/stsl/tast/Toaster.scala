package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast._
import xyz.cofe.stsl.tok._
import JvmType._
import xyz.cofe.stsl.types.{TObject, Type, WriteableField}

/**
 * "Тостер" - Компиляция AST выражений
 */
class Toaster( val typeScope: TypeScope, val varScope: VarScope=new VarScope() ) {
  require(typeScope!=null)
  require(varScope!=null)

  def compile( ast:AST ):TAST = {
    require(ast!=null)
    ast match {
//      case a:StackedArgumentAST => compile(a)
      case a:LiteralAST => compile(a)
      case a:IdentifierAST => compile(a)
      case a:BinaryAST => compile(a)
      case a:DelegateAST => compile(a)
      case a:TernaryAST => compile(a)
      case a:PropertyAST => compile(a)
//      case a:CallAST => compile(a)
//      case a:LambdaAST => compile(a)
      case _ => ???
    }
  }

  /**
   * Компиляция литерального выражения
   * @param literalAST выражение
   * @return типизрованный узел AST
   */
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

  /**
   * Компиляция вызова метода
   * @param thiz вызываемый объект (класс)
   * @param method вызываемый метод
   * @param args аргументы вызова
   * @return вариант вызова
   */
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

  /**
   * Компиляция бинарного выражения
   * @param binaryAST бинарное выражение
   * @return типизрованный узел AST
   */
  def compile( binaryAST: BinaryAST ):TAST = {
    require(binaryAST!=null)
    val left = compile(binaryAST.left)
    val right = compile(binaryAST.right)

    val opName = binaryAST.operator.tok.name
    if( !left.supplierType.isInstanceOf[TObject] ){
      throw ToasterError(s"left operand of $opName not object", binaryAST)
    }

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

  def compile( delegateAST: DelegateAST ):TAST = compile( delegateAST.target )

  def compile( ternaryAST: TernaryAST ):TAST = {
    require(ternaryAST!=null)
    val cond = compile(ternaryAST.first)
    if( !BOOLEAN.assignable(cond.supplierType) )throw ToasterError("condition not "+BOOLEAN.name, ternaryAST.first);

    val succ = compile(ternaryAST.second)
    val fail = compile(ternaryAST.third)
    if( !succ.supplierType.assignable(fail.supplierType) )throw ToasterError(
      "different type return in success block and failure block", ternaryAST.second, ternaryAST.third)

    TAST(ternaryAST, succ.supplierType, ()=>{
      val quest = cond.supplier.get()
      if( quest!=null ){
        quest match {
          case bval: Boolean =>
            if (bval) {
              succ.supplier.get()
            } else {
              fail.supplier.get()
            }
          case _ =>
            throw new RuntimeException("condition return not " + BOOLEAN.name)
        }
      }else{
        fail.supplier.get()
      }
    }, List(cond,succ,fail))
  }

  def compile( identifierAST: IdentifierAST ):TAST = {
    require(identifierAST!=null)
    //val variable = scope.vars.map.get(identifierAST.tok.name)
    val variable = varScope.get(identifierAST.tok.name)
    if( variable.isEmpty )throw ToasterError(s"undefined variable ${identifierAST.tok.name}")
    TAST( identifierAST, variable.get.tip, ()=>variable.get.read() )
  }

  def compile( propertyAST: PropertyAST ):TAST = {
    require(propertyAST!=null)

    val obj = compile(propertyAST.obj)
    val pname = propertyAST.name.tok.text
    val objType = obj.supplierType match {
      case t:TObject => t
      case _ => throw ToasterError("property owner not object", propertyAST.obj)
    }

    val propOpt = objType.fields.get(pname)
    if( propOpt.isEmpty ){
      throw ToasterError(s"property ${pname} not found in ${objType}", propertyAST.name)
    }

    val prop = propOpt.get match {
      case w: WriteableField => w
      case _ => throw ToasterError(s"property ${pname} of ${objType} not readable", propertyAST.name)
    }

    TAST(propertyAST, propOpt.get.tip, ()=>{
      val objInst = obj.supplier.get()
      prop.reading(objInst)
    },List(obj))
  }
}
