package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer, GR, Tokenizer}

object Lexer {
  import xyz.cofe.sparse.Chars._
  import xyz.cofe.sparse.GOPS._

  //#region ws - Пробел

  /** Пробел */
  val ws: GR[CharPointer, WS] =
    (Whitespace * 1) ==> ((t) => new WS(t.head.begin, t.last.end));

  //#endregion
  //#region digits

  private val digitChars = "0123456789";
  private val binDigitChars = "01";
  private val hexDigitChars = "0123456789abcdefABCDEF";

  private val digit: GR[CharPointer, CToken] = charTok( digitChars.indexOf(_)>=0 );
  private val binDigit: GR[CharPointer, CToken] = charTok( binDigitChars.indexOf(_)>=0 );
  private val hexDigit: GR[CharPointer, CToken] = charTok( hexDigitChars.indexOf(_)>=0 );

  //#endregion
  //#region string StringLiteral

  private val strBegin1 = charTok( _=='\"' )
  private val strEncUnicodeChar = charTok(_=='\\') + charTok(_=='u') + hexDigit + hexDigit + hexDigit + hexDigit ==> {
    case(s1,s2,d1,d2,d3,d4) =>
      val n1 = UnTypedNumberTok.digitOf(d1.text.charAt(0))
      val n2 = UnTypedNumberTok.digitOf(d2.text.charAt(0))
      val n3 = UnTypedNumberTok.digitOf(d3.text.charAt(0))
      val n4 = UnTypedNumberTok.digitOf(d4.text.charAt(0))
      val n = (n1 << 12) | (n2 << 8) | (n3 << 4) | n4
      val ch = n.toChar
      new StringLiteralChar(
        s1.begin, d4.end, ch.toString
      )
  }
  private val strEncChar = charTok(_=='\\') + charTok(_ => true) ==> { (a,b)  => new StringLiteralChar(
    a.begin,
    b.end,b.text match {
      case "n" => "\n"
      case "r" => "\r"
      case "t" => "\t"
      case _ => b.text
    }) }
  private val strNonEncChar1 = charTok(c => c!='\\' && c!='\"') ==> { a => new StringLiteralChar(a.begin, a.end, a.text) }
  private val strInnerChar1 = ( strEncUnicodeChar | strEncChar  | strNonEncChar1 ) ==> { a => new StringLiteralChar(a.begin, a.end, a.decoded) }
  private val string1 = strBegin1 + strInnerChar1*0 + strBegin1 ==> { (b, c, e) =>
    new StringTok(b.begin, e.end,
      if( c.nonEmpty ) {
        c.map(_.decoded).reduce {(a,b) =>
          if(a!=null){
            a + b
          } else b
        }
      } else ""
    )
  }

  private val strBegin2 = charTok( _=='\'' )
  private val strNonEncChar2 = charTok(c => c!='\\' && c!='\'') ==> { a => new StringLiteralChar(a.begin, a.end, a.text) }
  private val strInnerChar2 = ( strEncUnicodeChar | strEncChar | strNonEncChar2 ) ==> { a => new StringLiteralChar(a.begin, a.end, a.decoded) }
  private val string2 = strBegin2 + strInnerChar2*0 + strBegin2 ==> { (b, c, e) =>
    new StringTok(b.begin, e.end,
      if( c.nonEmpty ) {
        c.map(_.decoded).reduce {(a,b) =>
          if(a!=null){
            a + b
          } else b
        }
      } else ""
    )
  }

  /**
   * Строковой литерал
   */
  val string: GR[CharPointer, StringTok] = (string1 | string2) ==> { t => t }

  //#endregion
  //#region NumberLiteral

  private val hexNum = charTok(_=='0') + charTok(_=='x') + (hexDigit * 1) ==> {
    case(b,s,d) => new UnTypedNumberTok(b.begin, d.last.end, d.map(_.text).reduce((d1,d2)=>d1+d2), 16)
  }
  private val binNum = charTok(_=='0') + charTok(_=='b') + (binDigit * 1) ==> {
    case(b,s,d) => new UnTypedNumberTok(b.begin, d.last.end, d.map(_.text).reduce((d1,d2)=>d1+d2), 2)
  }
  private val decNum = ( digit * 1 ) ==> {
    dgts => new UnTypedNumberTok(dgts.head.begin, dgts.last.end,
      dgts.map(_.text).reduce((d1,d2)=>d1+d2),
      10
    )
  }
  private val untypedNum: GR[CharPointer, UnTypedNumberTok] = ( hexNum | binNum | decNum ) ==> { t => t }

  private val longNumber: GR[CharPointer, NumberTok] = untypedNum + charTok(c => c=='L' || c=='l' ) ==> {
    case( t, e ) => new LongNumberTok(t.begin, e.end, t.toLong)
  }
  private val bigIntNumber: GR[CharPointer, NumberTok] = untypedNum + charTok(c => c=='N' || c=='n' ) ==> {
    case( t, e ) => new BigIntNumberTok(t.begin, e.end, t.toBigInt)
  }
  private val shortNumber: GR[CharPointer, NumberTok] = untypedNum + charTok(c => c=='S' || c=='s' ) ==> {
    case( t, e ) => new ShortNumberTok(t.begin, e.end, t.toInt.toShort)
  }
  private val byteNumber: GR[CharPointer, NumberTok] = untypedNum + charTok(c => c=='B' || c=='b' ) ==> {
    case( t, e ) => new ByteNumberTok(t.begin, e.end, t.toInt.toByte)
  }
  private val intNumber: GR[CharPointer, NumberTok] = untypedNum ==> {
    case( t ) => new IntNumberTok(t.begin, t.end, t.toInt)
  }

  private val digitPoint = charTok(_=='.')
  private val doubleSuf = charTok(c=>c=='d' || c=='D')
  private val floatSuf = charTok(c=>c=='f' || c=='F')
  private val decimalSuf = charTok(c=>c=='w' || c=='W')

  private val doubleNumber1: GR[CharPointer, NumberTok] = untypedNum + digitPoint + untypedNum ==> {
    case( int,d,flt ) => new DoubleNumberTok(int.begin, flt.end, int.toLong.toDouble + flt.toFloatPart)
  }
  private val doubleNumber2: GR[CharPointer, NumberTok] = untypedNum + digitPoint ==> {
    case( int,d ) => new DoubleNumberTok(int.begin, d.end, int.toLong.toDouble)
  }
  private val doubleNumber3: GR[CharPointer, NumberTok] = digitPoint + untypedNum ==> {
    case( d,flt ) => new DoubleNumberTok(d.begin, flt.end, flt.toFloatPart)
  }
  private val doubleNumber4: GR[CharPointer, NumberTok] = untypedNum + doubleSuf ==> {
    case( int,suf ) => new DoubleNumberTok(int.begin, suf.end, int.toLong.toDouble)
  }
  private val doubleNumber5: GR[CharPointer, NumberTok] = untypedNum + digitPoint + untypedNum + doubleSuf ==> {
    case( int,d,flt,end ) => new DoubleNumberTok(int.begin, end.end, int.toLong.toDouble + flt.toFloatPart)
  }
  private val doubleNumber6: GR[CharPointer, NumberTok] = untypedNum + digitPoint + doubleSuf ==> {
    case( int,d,end ) => new DoubleNumberTok(int.begin, end.end, int.toLong.toDouble)
  }
  private val doubleNumber7: GR[CharPointer, NumberTok] = digitPoint + untypedNum + doubleSuf ==> {
    case( d,flt,end ) => new DoubleNumberTok(d.begin, end.end, flt.toFloatPart)
  }

  private val floatNumber4: GR[CharPointer, NumberTok] = untypedNum + floatSuf ==> {
    case( int,suf ) => new FloatNumberTok(int.begin, suf.end, int.toLong.toFloat)
  }
  private val floatNumber5: GR[CharPointer, NumberTok] = untypedNum + digitPoint + untypedNum + floatSuf ==> {
    case( int,d,flt,end ) => new FloatNumberTok(int.begin, end.end, int.toLong.toFloat + flt.toFloatPart.toFloat)
  }
  private val floatNumber6: GR[CharPointer, NumberTok] = untypedNum + digitPoint + floatSuf ==> {
    case( int,d,end ) => new FloatNumberTok(int.begin, end.end, int.toLong.toFloat)
  }
  private val floatNumber7: GR[CharPointer, NumberTok] = digitPoint + untypedNum + floatSuf ==> {
    case( d,flt,end ) => new FloatNumberTok(d.begin, end.end, flt.toFloatPart.toFloat)
  }

  private val decimalNumber4: GR[CharPointer, NumberTok] = untypedNum + decimalSuf ==> {
    case( int,suf ) => new DecimalNumberTok(int.begin, suf.end, BigDecimal(int.toBigInt))
  }
  private val decimalNumber5: GR[CharPointer, NumberTok] = untypedNum + digitPoint + untypedNum + decimalSuf ==> {
    case( int,d,flt,end ) => new DecimalNumberTok(int.begin, end.end, BigDecimal(int.toBigInt) + flt.toFloatPartDecimal)
  }
  private val decimalNumber6: GR[CharPointer, NumberTok] = untypedNum + digitPoint + decimalSuf ==> {
    case( int,d,end ) => new DecimalNumberTok(int.begin, end.end, BigDecimal(int.toBigInt))
  }
  private val decimalNumber7: GR[CharPointer, NumberTok] = digitPoint + untypedNum + decimalSuf ==> {
    case( d,flt,end ) => new DecimalNumberTok(d.begin, end.end, flt.toFloatPartDecimal)
  }

  private val double: GR[CharPointer, NumberTok] = (doubleNumber4|doubleNumber5|doubleNumber6|doubleNumber7|doubleNumber1|doubleNumber2|doubleNumber3) ==> { t => t }
  private val float: GR[CharPointer, NumberTok] = (floatNumber4|floatNumber5|floatNumber6|floatNumber7) ==> { t => t }
  private val decimal: GR[CharPointer, NumberTok] = (decimalNumber4|decimalNumber5|decimalNumber6|decimalNumber7) ==> { t => t }

  val number: GR[CharPointer, NumberTok] = (decimal|double|float|byteNumber|shortNumber|longNumber|bigIntNumber|intNumber) ==> { t => t }

  //#endregion
  //#region identifier

  val identifier: GR[CharPointer, IdentifierTok] = charTok(Character.isLetter) + ( charTok(c=>Character.isLetterOrDigit(c) || c=='_') * 0 ) ==> {
    case( start,follow ) => new IdentifierTok(start.begin, if(follow.isEmpty) start.end else follow.last.end, {
      if(follow.isEmpty){
        start.text
      }else{
        start.text + follow.map(_.text).reduce((a,b)=>a+b)
      }
    })
  }

  //#endregion
  //#region operator

  private val operatorChar = charTok( c =>
    !Character.isLetterOrDigit(c) && !Character.isWhitespace(c) &&
    c!='\'' && c!='"'
  )
  
  val predefinedOperators: Seq[String] = List(
    "{","}","(",")","[","]","<",">",
    ",",".",
    ">=","<=","=>","=<","->","<-",
    "*","**","+","-","/","%",
    "==","!=",
    "?",
    ";",":","::",":::",
    "!").sortBy(_.length).reverse
  
  val predefOperator: GR[CharPointer, OperatorTok] =
    ptr => {
      predefinedOperators.foldLeft( None:Option[OperatorTok] )((res, str) => {
        res match {
          case Some(x) => res
          case None =>
            val cap = ptr.text(str.length)
            if( cap==str ){
              Some(new OperatorTok(ptr, ptr.move(str.length),cap))
            }else{
              None
            }
        }
      })
    }
    //charTok(c => "{},".indexOf(c)>=0 ) ==> { case(start) => new OperatorTok(start.begin, start.end, start.text) }

  val operator: GR[CharPointer, OperatorTok] = operatorChar + ( operatorChar * 0 ) ==> {
    case( start,follow ) => new OperatorTok(start.begin, if(follow.isEmpty) start.end else follow.last.end, {
      if(follow.isEmpty){
        start.text
      }else{
        start.text + follow.map(_.text).reduce((a,b)=>a+b)
      }
    })
  }

  //#endregion
  //#region comment

  /**
   * Комментарий
   * <p>
   * <code> ::= '//' любой_символ_кроме_пееревода_строк * [символ_пееревода_строк] </code>
   */
  private val singleLineComment :GR[CharPointer, CommentTok] = new GR[CharPointer,CommentTok] {
    override def apply(ptr: CharPointer): Option[CommentTok] = {
      if( ptr.lookup(0).contains('/') ){
        if( ptr.lookup(1).contains('/') ){
          var pt = ptr.move(2)
          var end = pt
          var stop = false
          while(!stop){
            val ch = pt.lookup(0)
            if( ch.isEmpty ){
              stop = true
              end = pt
            }else{
              ch.get match {
                case '\n' =>
                  val ch2 = pt.lookup(1)
                  if( ch2.contains('\r') ){
                    end = pt.move(2)
                    stop = true
                  } else {
                    end = pt.move(1)
                    stop = true
                  }
                case '\r' =>
                  val ch2 = pt.lookup(1)
                  if( ch2.contains('\n') ){
                    end = pt.move(2)
                    stop = true
                  } else {
                    end = pt.move(1)
                    stop = true
                  }
                case _ =>
                  stop = false
                  pt = pt.move(1)
              }
            }
          }
          Some(new CommentTok(ptr,end))
        }else None
      }else None
    }
  }

  /**
   * Комментарий
   * <p>
   * <code> ::= '/' '*' любой_символ * '*' '/' </code>
   */
  private val multilLineComment :GR[CharPointer, CommentTok] = new GR[CharPointer,CommentTok] {
    override def apply(ptr: CharPointer): Option[CommentTok] = {
      val begin : String = ptr.text(2)
      if( begin=="/*" ){
        var pt = ptr.move(2)
        var stop = false
        var end = pt
        while (!stop) {
          if( pt.lookup(0).isEmpty ){
            stop = false
            end = pt
          }else{
            val nxt : String = pt.text(2)
            if( nxt=="*/" ){
              end = pt.move(2)
              stop = true
            }else{
              pt = pt.move(1)
              stop = false
            }
          }
        }
        Some(new CommentTok(ptr,end))
      } else None
    }
  }

  val comment: GR[CharPointer, CommentTok] = (singleLineComment | multilLineComment) ==> { t => t }

  //#endregion

  def tokenizer(source:String): Tokenizer[CharPointer, CToken] =
    Tokenizer.tokens(source,List(ws,string,number,comment,identifier,predefOperator,operator),null)
}
