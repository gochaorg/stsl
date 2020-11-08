package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR

/**
 * Определение лямбды
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param params параметры
 * @param body тело
 * @param recursion параметр для рекурсии
 */
class LambdaAST(
                 begin:PTR,
                 end:PTR,
                 val params:List[ParamAST] = List(),
                 val body:AST,
                 val recursion:Option[ParamAST] = None
               ) extends AST( begin, end, params ++ ( if(recursion.isDefined) List(recursion.get) else List() ) ++ List(body) ) {
  require(params!=null)
  require(body!=null)
  override def toString: String = "LambdaAST" + (
    if(recursion.isDefined) s" recursion: ${recursion.get}" else ""
  )

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): LambdaAST = {
    require(what!=null)
    require(to!=null)
    if( params.contains(what) ){
      new LambdaAST(
        begin,end,
        params.map(p=>if(p==what && to.isInstanceOf[ParamAST]) to.asInstanceOf[ParamAST] else p),
        body, recursion)
    }else if( body==what ) {
      new LambdaAST(begin, end, params, to, recursion)
    } else if( recursion.isDefined && recursion.get==what && to.isInstanceOf[ParamAST] ) {
      new LambdaAST(begin, end, params, body, Some(to.asInstanceOf[ParamAST]))
    }else{
      new LambdaAST(begin, end,
        params.map(p => p.replace(what,to)),
        body.replace(what, to),
        recursion.map( r => r.replace(what,to) )
      )
    }
  }
}
