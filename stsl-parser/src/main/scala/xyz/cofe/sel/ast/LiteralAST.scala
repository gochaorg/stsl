package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.PTR
import xyz.cofe.stsl.tok.LiteralTok

/**
 * Литерал-символ
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param tok символ
 */
class LiteralAST (begin:PTR, end:PTR, val tok: LiteralTok[_]) extends AST(begin,end) {
  require(tok!=null)
  override def toString: String = s"LiteralAST ${tok}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): LiteralAST = this
}

object LiteralAST {
  def apply(begin:PTR, tok:LiteralTok[_]):LiteralAST = {
    new LiteralAST(begin, begin.move(1), tok)
  }
}