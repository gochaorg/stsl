package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR

class ParamAST(
                begin:PTR,
                end:PTR,
                val name:IdentifierAST,
                val typeName: TypeNameAST
              ) extends AST( begin, end, List(name, typeName) ) {
  require( name!=null )
  require( typeName!=null )
  override def toString: String = s"ParamAST ${name}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): ParamAST = {
    require(what!=null)
    require(to!=null)
    if( what==name && to.isInstanceOf[IdentifierAST] )
      new ParamAST(begin,end,to.asInstanceOf[IdentifierAST],typeName)
    else if( what==typeName && to.isInstanceOf[TypeNameAST] )
      new ParamAST(begin, end, name, to.asInstanceOf[TypeNameAST])
    else
      new ParamAST(begin, end, name.replace(what,to), typeName.replace(what, to))
  }
}
