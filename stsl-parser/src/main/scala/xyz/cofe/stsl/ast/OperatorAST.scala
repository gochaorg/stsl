package xyz.cofe.stsl.ast

import Parser.PTR
import xyz.cofe.stsl.tok.OperatorTok

/**
 * Оператор-символ.
 * В дереве AST в явном виде отсуствует,
 * входит в состав операторов: UnaryAST, BinaryAST, TernaryAST
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param tok символ
 */
class OperatorAST(begin:PTR, end:PTR, val tok: OperatorTok) extends AST(begin,end) {
  require(tok!=null)

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): OperatorAST = this
}


