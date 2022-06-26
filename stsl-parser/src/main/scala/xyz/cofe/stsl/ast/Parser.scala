package xyz.cofe.stsl.ast

import xyz.cofe.sparse.{CToken, GR, LPointer}
import xyz.cofe.stsl.tok._

case class Parser() {
  import xyz.cofe.sparse.GOPS._
  import Parser._
  
  /**
   * "Начальное" правило
   */
  val expression : ProxyGR = new ProxyGR
  
  /**
   * Правило распознования LiteralAST
   */
  val literal : AstGR = new AstGR {
    override def apply(ptr: PTR): Option[AST] = {
      val t = ptr.lookup(0)
      if( t.exists( _.isInstanceOf[LiteralTok[_]] )){
        val tk = t.get.asInstanceOf[LiteralTok[_]]
        Some(LiteralAST(ptr,tk))
      }else {
        None
      }
    }
  }
  
  /**
   * Правило распознования IdentifierAST
   */
  val identifier : AstGR = new AstGR {
    override def apply(ptr: PTR): Option[AST] = {
      val t = ptr.lookup(0)
      if( t.exists( _.isInstanceOf[IdentifierTok] )){
        val tk : IdentifierTok = t.get.asInstanceOf[IdentifierTok]
        Some(new IdentifierAST(ptr,ptr.move(1),tk))
      }else {
        None
      }
    }
  }
  
  /**
   * Создание правила для OperatorAST
   * @param operators Список понимаемых операторов
   * @return правило для распознования OperatorAST
   */
  def operator(operators: String *): OpLiteral = {
    require(operators!=null)
    require(operators.nonEmpty)
    new GR[PTR,OperatorAST] {
      override def apply(ptr: PTR): Option[OperatorAST] = {
        val tok = ptr.lookup(0)
        if( tok.exists( tk => operators.contains(tk.text) && tk.isInstanceOf[OperatorTok]) ){
          Some( new OperatorAST(ptr,ptr.move(1),tok.get.asInstanceOf[OperatorTok]) )
        }else{
          None
        }
      }
    }
  }
  
  /**
   * Постраение правил бинарных операторов <br>
   *   <code> ::= init { operatorLit follow } </code>
   * @param init начальное правило
   * @param operatorLit операторы
   * @param follow последующие правила
   * @param join объединение узлов
   * @return правило
   */
  def binary( init:AstGR, operatorLit: OpLiteral, follow:AstGR, join:(AST,OperatorAST,AST)=>AST ):AstGR = {
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
  
  /**
   * Постраение правил бинарных операторов <br>
   *   <code> ::= init { operatorLit follow } </code>
   * @param init начальное правило
   * @param operatorLit операторы
   * @param follow последующие правила
   * @return правило
   */
  def binary( init:AstGR, operatorLit: OpLiteral, follow:AstGR ):AstGR = {
    require(init!=null)
    require(operatorLit!=null)
    require(follow!=null)
    binary(init, operatorLit, follow, (init,op,flw) =>
      new BinaryAST(init.begin(), flw.end(), op, init, flw) )
  }
  
  /**
   * Скобочное выражение <code> ::= '(' expression ')' </code>
   */
  val parenthes : GR[PTR,DelegateAST] = operator("(") + expression + operator(")") ==> ( (l,e,r) => new DelegateAST(l.begin(), r.end(), e) )
  
  /**
   * Унарный оператор -, ! <br>
   *   <code> ::= ( '-' | '!' ) expression </code>
   */
  val unary : GR[PTR,UnaryAST] = operator("-", "!") + expression ==> ( (op,e) => new UnaryAST(op.begin(), e.end(), op, e) )
  
  val typeName : GR[PTR, TypeNameAST] = identifier ==>
    ( id => new TypeNameAST(id.begin(), id.end(), id.asInstanceOf[IdentifierAST].tok.text) )
  
  //#region Лямбда
  
  /**
   * Параметр лямды <code>::= identifier ':' typeName</code>
   */
  val lambdaParam : GR[PTR, ParamAST] = identifier + operator(":") + typeName ==>
    ( (paramName, op1, typeName) =>
      new ParamAST(paramName.begin(), typeName.end(), paramName.asInstanceOf[IdentifierAST], typeName)
      )
  
  /**
   * Параметр-рекурсия лямды <code>::= identifier '::' typeName</code>
   */
  val lambdaRecusionParam : GR[PTR, ParamAST] = identifier + operator("::") + typeName ==>
    ( (paramName, op1, typeName) =>
      new ParamAST(paramName.begin(), typeName.end(), paramName.asInstanceOf[IdentifierAST], typeName)
      )
  
  private val lambdaArrow = operator("=>")
  private val lambdaArgDelim = operator(",")
  
  private val lambdaEmptyParams1 : AstGR =
    operator("(") + operator(")") + operator("=>") ==> ( (a,b,c) => new AST(a.begin(),c.end()) {})
  
  private val lambdaEmptyParams2 : AstGR =
    operator("()") + operator("=>") ==> ( (a,b) => new AST(a.begin(),b.end()) {})
  
  private val lambdaEmptyParams3 : GR[PTR,OperatorAST] =
    operator("()=>") ==> ( t => t )
  
  private val lambdaEmptyParams4 : AstGR =
    operator("( ")+ operator(")=>") ==> ( (a,b) => new AST(a.begin(), b.end()) {} )
  
  private val lambdaEmptyParamsN : AstGR =
    ( lambdaEmptyParams1 | lambdaEmptyParams2 | lambdaEmptyParams3 | lambdaEmptyParams4) ==> (t => t)
  
  /**
   * Лямбда без параметров <br>
   * <code> ::= '(' ')' '=>' expression </code> <br>
   * <code> ::= '()' '=>' expression </code> <br>
   * <code> ::= '()=>' expression </code> <br>
   */
  val lambdaWithoutParams: GR[PTR, LambdaAST] = lambdaEmptyParamsN + expression ==>
    ( (empt,body) => new LambdaAST(empt.begin(), body.end(), List(), body) )
  
  /**
   * Лямбда с параметрами <code> ::= lambdaParam {','  lambdaParam} [ ',' lambdaRecusionParam ] '=>' expression</code>
   * <br>
   * Примеры
   *
   * <pre>
   * x:int, y:int => x + y
   * x:int, y:int, r::int => x + y
   * x:int, r::int => x<0 ? 0 : r(x-1) + x
   * </pre>
   */
  val lambdaWithParams: GR[PTR, LambdaAST] = (beginPtr:PTR) => {
    require(beginPtr!=null)
    var params : List[ParamAST] = List()
    var recursiveParam : ParamAST = null
    val firstParam = lambdaParam(beginPtr)
    if( firstParam.isDefined ){
      params = firstParam.get :: params
      var ptr = firstParam.get.end()
      var stop = false
      var failMatch = false
      while( !stop ){
        val delim = lambdaArgDelim(ptr)
        if( delim.isDefined ){
          val nextParam = lambdaParam(delim.get.end())
          if( nextParam.isDefined ) {
            ptr = nextParam.get.end()
            params = nextParam.get :: params
          }else{
            val recusParam = lambdaRecusionParam(delim.get.end())
            if( recusParam.isDefined ){
              val endParam = lambdaArrow(recusParam.get.end())
              if( endParam.isEmpty ) throw ParseError("expected arrow")
              stop = true
              ptr = endParam.get.end()
              recursiveParam = recusParam.get
            }else {
              throw ParseError("expected param", delim.get.end())
            }
          }
        }else{
          val endParam = lambdaArrow(ptr)
          if( endParam.isDefined ){
            stop = true
            ptr = endParam.get.end()
          }else{
            if( params.size <= 1 ){
              None
              stop = true
              failMatch = true
            }else {
              throw ParseError("expected param or arrow", endParam.get.end())
            }
          }
        }
      }
      
      if( !failMatch ) {
        val expr = expression(ptr)
        if (expr.isEmpty) {
          throw ParseError("expected expression", ptr)
        }
        
        Some(new LambdaAST(
          beginPtr, expr.get.end(),
          params.reverse,
          expr.get,
          Option(recursiveParam)
        ))
      } else {
        None
      }
    }else{
      None
    }
  }
  
  //endregion
  
  val emptyObj1 : GR[PTR,PojoAST] = operator("{}") ==> (o => new PojoAST(o.begin(), o.end()))
  val emptyObj2 : GR[PTR,PojoAST] = operator("{") + operator("}") ==> ( (b,e) => new PojoAST(b.begin(), e.end()))
  val objKeyVal : GR[PTR,PojoItemAST] = identifier + operator(":") + expression ==> ( (k,_t,v) => new PojoItemAST(k.begin(), v.end(), k.asInstanceOf[IdentifierAST], v) )
  val objNonEmpty : GR[PTR,PojoAST] =
    operator("{") +
      objKeyVal + (
      operator(",") + objKeyVal ==> ((d,i)=>new PojoItemAST(d.begin(),i.end(),i.key,i.value))
      )*0 + operator("}") ==> ((b,f,s,e) => new PojoAST(b.begin(), e.end(), List(f) ++ s))
  val objDef : GR[PTR,PojoAST] = (emptyObj1 | emptyObj2 | objNonEmpty) ==> ( t => t )
  
  /**
   * Атомарное значение <br>
   * <code>
   * ::= lambdaWithoutParams <br>
   * &nbsp;  | lambdaWithParams <br>
   * &nbsp;  | parenthes <br>
   * &nbsp;  | unary <br>
   * &nbsp;  | literal <br>
   * &nbsp;  | identifier <br>
   * </code>
   */
  val atom : AstGR =
    ( lambdaWithoutParams.asInstanceOf[AstGR]
      | lambdaWithParams.asInstanceOf[AstGR]
      | objDef
      | parenthes
      | unary
      | literal
      | identifier
      ) ==> (t => t )
  
  private val fieldAccessOp: OpLiteral = operator(".")
  private val callStart: OpLiteral = operator("(")
  private val callEnd: OpLiteral = operator(")")
  private val callStartEnd: OpLiteral = operator("()")
  private val argDelim: OpLiteral = operator(",")
  
  /**
   * Правило postFix: <br>
   * <code> ::= atom { </code> <br>
   *
   * <i>// Доступ к свойству</i> <br>
   * <code> '.' identifier </code> <br>
   *
   * <i>// Вызов метода/функции</i> <br>
   * <code> | '(' [ expression { ',' expression } ] ')' </code> <br>
   * <code> } </code>
   */
  val postFix: AstGR = new AstGR {
    //noinspection EmptyParenMethodAccessedAsParameterless
    val propertyFollow : (AST,PTR)=>AST = (base:AST, ptr:PTR) => {
      val dotFld = fieldAccessOp(ptr)
      if (dotFld.isDefined) {
        val ident = identifier(dotFld.get.end)
        if(ident.isDefined) {
          val propertyAST = new PropertyAST(
            base.begin,
            ident.get.end,
            base,
            ident.get.asInstanceOf[IdentifierAST])
          val calling = call(propertyAST, propertyAST.end())
          if( calling!=null ){
            calling
          }else {
            propertyAST
          }
        } else null
      } else null
    }
    
    val call : (AST,PTR)=>AST = (base:AST,ptr:PTR) => {
      val cStartEnd = callStartEnd(ptr)
      if( cStartEnd.isDefined ){
        new CallAST(ptr,cStartEnd.get.end(),base,List())
      }else {
        val cStart = callStart(ptr)
        if (cStart.isDefined) {
          var stop = false
          var prev: AST = cStart.get
          var end: AST = null
          var expectArg = false
          var args: List[AST] = List()
          
          while (!stop) {
            val cStop = if (expectArg) None else callEnd(prev.end())
            if (cStop.isDefined) {
              end = cStop.get
              stop = true
            } else {
              val exp = expression(prev.end())
              if (exp.isDefined) {
                if( args.exists(arg => arg.begin().compare(exp.get.begin()) == 0) ){
                  throw ParseError("bug at parser!, consume duplicate arg", exp.get.begin());
                }
                args = exp.get :: args
                val cStop2 = callEnd(exp.get.end())
                if (cStop2.isDefined) {
                  end = cStop2.get
                  stop = true
                } else {
                  val cArgDelim = argDelim(exp.get.end())
                  if (cArgDelim.isDefined) {
                    prev = cArgDelim.get
                    expectArg = true
                  } else {
                    throw ParseError("Parse error, expect delimiter, of finish call", exp.get.end())
                  }
                }
              } else {
                if (expectArg) {
                  throw ParseError("Parse error, expect argument expression", prev.end())
                } else {
                  throw ParseError("Parse error, expect expression, argument delimeter, arrow", prev.end())
                }
              }
            }
          }
          
          new CallAST(ptr, end.end(), base, args.reverse)
        } else null
      }
    }
    
    val follows = List(propertyFollow, call)
    
    //noinspection EmptyParenMethodAccessedAsParameterless
    override def apply(ptr: PTR): Option[AST] = {
      val base = atom(ptr)
      if( base.isEmpty ){
        None
      }else{
        var result:AST = base.get
        var stop = false
        while( !stop ) {
          var matched:AST = null
          follows.map( flw => if( matched==null){
            matched = flw(result,result.end())
            matched
          } )
          if( matched==null ){
            stop = true
          }else{
            result = matched
          }
        }
        Some(result)
      }
    }
  }
  
  /**
   * Правило *, /, % <br>
   * <code> ::= binary( postFix,  operator("*","/","%"), postFix ) </code>
   */
  val mul: AstGR = binary(postFix, operator("*","/","%"), postFix)
  
  /**
   * Правило +, -
   */
  val add: AstGR = binary(mul,operator("+","-"),mul)
  
  /**
   * Правило ==, !=, <, >, <=, >=
   */
  val cmp: AstGR = binary(add,operator("==","!=","<",">","<=",">="),add)
  
  /**
   * Правило &, |
   */
  val bool: AstGR = binary(cmp,operator("&","|"),cmp)
  
  def ternary( condition:AstGR, question:OpLiteral, success:AstGR, elseOp:OpLiteral, failure:AstGR ): AstGR = new AstGR {
    require(condition!=null)
    require(success!=null)
    require(failure!=null)
    require(question!=null)
    require(elseOp!=null)
    override def apply(ptr: PTR): Option[AST] = {
      val condAst = condition(ptr)
      if( condAst.isDefined ){
        val questLit = question(condAst.get.end())
        if( questLit.isDefined ){
          val succAst = success(questLit.get.end())
          if( succAst.isDefined ){
            val elseLit = elseOp(succAst.get.end())
            if( elseLit.isDefined ){
              val failAst = failure(elseLit.get.end())
              if( failAst.isDefined ){
                Some( new TernaryAST(
                  condAst.get.begin(), failAst.get.end(),
                  questLit.get, elseLit.get,
                  condAst.get,
                  succAst.get,
                  failAst.get
                ))
              } else { condAst }
            } else { condAst }
          } else { condAst }
        }else{ condAst }
      }else{ None }
    }
  }
  
  /**
   * Условный оператор
   */
  val ifOp = ternary( bool, operator("?"), bool, operator(":"), bool )
  
  /**
   * Парсинг исходного текста в AST
   * @param source исходный текст
   * @return AST
   */
  def parse( source:String ): Option[AST] ={
    require(source!=null)
    val toks = Lexer.tokenizer(source).filter( t => !t.isInstanceOf[WS] && !t.isInstanceOf[CommentTok] ).toList
    val ptr = new PTR(0,toks)
    val res = expression(ptr)
    if( res.isDefined ){
      val p = res.get.end().pointer()
      if( p < toks.size-1 ){
        System.err.println("tokens:")
        toks.foreach(System.err.println)
        System.err.println("ast:")
        ASTDump.dump(System.err,res.get)
        throw ParseError("parse not full tokens", res.get.end())
      }
    }
    res
  }
  
  expression.target = ifOp
}

/**
 * Парсер
 */
object Parser {
  import xyz.cofe.sparse.GOPS._

  type PTR = LPointer[CToken]
  type AstGR = GR[PTR,AST]
  type OpLiteral = GR[PTR,OperatorAST]
  
  val defaultParser = new Parser()
  def parse(source:String): Option[AST] = defaultParser.parse(source)
}
