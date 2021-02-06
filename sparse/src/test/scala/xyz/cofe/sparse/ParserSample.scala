package xyz.cofe.sparse

/**
 * Пример парсера
 */
object ParserSample {
  type PTR = LPointer[CToken]
  type AstGR = GR[PTR,AST]

  class LiteralAst(beginPointer : PTR, endPointer : PTR, val number:IntergerTok ) extends AST(beginPointer, endPointer) {
    override def toString: String = s"LiteralAst(${number.value})"
  }
  class OpLiteralAst(beginPointer : PTR, endPointer : PTR, val operator:OperatorName.EnumVal ) extends AST(beginPointer, endPointer){
    override def toString: String = s"OpLiteralAst($operator)"
  }
  class BinaryAst(beginPointer : PTR, endPointer : PTR, val operator:OperatorName.EnumVal, val left:AST, val right:AST ) extends AST(beginPointer, endPointer, ()=>List(left,right)) {
    override def toString: String = s"BinaryOpAst($operator)"
  }
  class ProxyGR extends AstGR {
    var target : AstGR = null;
    override def apply(ptr: PTR): Option[AST] = {
      val trgt = target
      if( trgt!=null && ptr!=null ){
        trgt(ptr)
      }else{
        None
      }
    }
  }

  import xyz.cofe.sparse.GOPS._

  val expression : ProxyGR = new ProxyGR

  val literal :AstGR = ptr => ptr.lookup(0).flatMap {
    case l: IntergerTok => Some(new LiteralAst(ptr, ptr.move(1), l))
    case _ => None
  }

  def operator(expect:OperatorName.EnumVal*):GR[PTR,OpLiteralAst] = {
    ptr => ptr.lookup(0) flatMap {
      case t:OperatorTok =>
        println(expect)
        println(t.name)
        println(expect.contains(t.name))
        if( expect.contains(t.name) ) {
          Some(new OpLiteralAst(ptr, ptr.move(1), t.name))
        } else {
          None
        }
      case _ => None
    }
  }

  def binary( init:AstGR, operatorLit: GR[PTR,OpLiteralAst], follow:AstGR, join:(AST,OpLiteralAst,AST)=>AST ):AstGR = {
    require(init!=null)
    require(operatorLit!=null)
    require(follow!=null)
    require(join!=null)

    new AstGR {
      override def apply(ptr: PTR): Option[AST] = {
        val start = init(ptr)
        if( start.isDefined ){
          var res = start.get
          var stop = false
          while (!stop) {
            val op = operatorLit(res.end())
            if( op.isDefined ){
              val flw = follow(op.get.end())
              if( flw.isDefined ){
                res = join(res,op.get,flw.get)
              }else{
                stop = true
              }
            }else{
              stop = true
            }
          }
          Some(res)
        }else{
          None
        }
      }
    }
  }

  def binary( init:AstGR, operatorLit: GR[PTR,OpLiteralAst], follow:AstGR ):AstGR = {
    require(init != null)
    require(operatorLit != null)
    require(follow != null)
    binary(init, operatorLit, follow, (init, op, flw) =>
      new BinaryAst(init.begin(), flw.end(), op.operator, init, flw))
  }

  val mulDivOp : AstGR = binary(literal,operator(OperatorName.Mul, OperatorName.Div),literal)

  val addSubOp : AstGR = binary(mulDivOp,operator(OperatorName.Add, OperatorName.Sub),mulDivOp)

  expression.target = (addSubOp | literal) ==> {t => t}

  def parse( source:String ): Option[AST] ={
    require(source!=null)
    val toks = LexerSample.tokenizer(source).filter( t => !t.isInstanceOf[WS] ).toList
    val ptr = new PTR(0,toks)
    val res = expression(ptr)
    if( res.isDefined ){
      val p = res.get.end().pointer()
      if( p < toks.size-1 ){
        System.err.println("tokens:")
        toks.foreach(System.err.println)
        System.err.println("ast:")
        ASTDump.dump(System.err,res.get)
        throw new Error("parse not full tokens")
      }
    }
    res
  }
}
