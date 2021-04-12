package xyz.cofe.stsl.ast

import xyz.cofe.stsl.ast.Parser.PTR

class PojoItemAST( begin:PTR, end:PTR, val key: IdentifierAST, val value: AST ) extends AST( begin, end, List(value) ) {
  require(key!=null)
  require(value!=null)
  override def toString: String = s"PojoItemAST ${key.tok.name}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): PojoItemAST = {
    require(what!=null)
    require(to!=null)
    if( key==what ) {
      require(to.isInstanceOf[IdentifierAST])
      new PojoItemAST(begin,end,to.asInstanceOf[IdentifierAST],value)
    }else if( value==what ){
      new PojoItemAST(begin,end,key,to)
    }else{
      new PojoItemAST(begin,end,key.replace(what,to),value.replace(what,to))
    }
  }
}
