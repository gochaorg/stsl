package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Имя типа
 * @param begin начало в исходник
 * @param end конец в исходнике
 * @param name имя типа
 */
class TypeNameAST(
                   begin:PTR,
                   end:PTR,
                   val name:String
                 ) extends AST( begin, end ) {
  require(name!=null)
  override def toString: String = s"TypeNameAST ${name}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): TypeNameAST = this
}
