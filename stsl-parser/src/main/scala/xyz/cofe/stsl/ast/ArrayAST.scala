package xyz.cofe.stsl.ast

import xyz.cofe.stsl.ast.Parser.PTR

/**
 * Массив
 *
 * Синтаксис
 *
 * {{{
 * arrayDef ::= `[` item { `,` item } [`,`] `]`
 * }}}
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param items элементы массивы
 */
class ArrayAST( begin:PTR, end:PTR, val items:List[AST]=List() ) extends AST( begin,end,items ) {
  require(items!=null)
  override def toString: String = s"ArrayAST"
  
  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): ArrayAST = {
    new ArrayAST(begin,end,items.map(i=>i.replace(what, to)))
  }
}
