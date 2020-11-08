package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR
import xyz.cofe.stsl.tok.IdentifierTok

/**
 * Ссылка на AST
 *
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param target целевойк AST
 */
class DelegateAST(begin:PTR, end:PTR, val target: AST) extends AST(begin,end,List(target)) {
  require(target!=null)
  override def toString: String = s"DelegateAST"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): DelegateAST = {
    require(what!=null)
    require(to!=null)
    if( target==what )
      new DelegateAST(begin,end,to)
    else
      this
  }
}


