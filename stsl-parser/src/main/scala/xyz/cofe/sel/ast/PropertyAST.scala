package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR

class PropertyAST( begin:PTR,
                   end:PTR,
                   val obj:AST,
                   val name:IdentifierAST
                 ) extends AST(begin,end, List(obj)) {
  require(obj!=null)
  require(name!=null)
  override def toString: String = s"PropertyAST ${name.tok.text}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): PropertyAST = {
    require(what!=null)
    require(to!=null)

    if( what==obj )
      new PropertyAST(begin,end, to, name)
    else if( what==name && to.isInstanceOf[IdentifierAST] )
      new PropertyAST(begin,end,obj,to.asInstanceOf[IdentifierAST])
    else
      this
  }
}
