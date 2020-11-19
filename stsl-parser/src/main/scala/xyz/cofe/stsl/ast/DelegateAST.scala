package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Ссылка на AST, испольщуется в скобочных выражениях
 * <p>
 * Пример парсинг выражения
 * <pre>( 123 - 234 ) * 2</pre>
 *
 * Дерево AST:
 * <pre>
 * BinaryAST *
 * -| <b>DelegateAST</b>
 * -|-| BinaryAST -
 * -|-|-| LiteralAST IntNumberTok 123
 * -|-|-| LiteralAST IntNumberTok 234
 * -| LiteralAST IntNumberTok 2
 * </pre>
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


