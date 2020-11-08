package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR
import xyz.cofe.stsl.tok.IdentifierTok

/**
 * Ссылка на переменную
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param tok символ
 */
class IdentifierAST(begin:PTR, end:PTR, val tok: IdentifierTok) extends AST(begin,end) {
  require(tok!=null)
  override def toString: String = s"IdentifierAST ${tok}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): IdentifierAST = this
}


