package xyz.cofe.stsl.ast

import Parser.PTR

class PojoAST( begin:PTR, end:PTR, val items:List[PojoItemAST]=List() ) extends AST( begin,end,items ) {
  require(items!=null)
  override def toString: String = s"PojoAST"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): AST = {
    new PojoAST(begin,end,items.map(i=>i.replace(what, to)))
  }
}
