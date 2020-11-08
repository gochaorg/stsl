package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Унарный оператор
 * @param begin начало в исходнике
 * @param end конец в исходнике
 * @param operator оператор
 * @param expression операнд
 */
class UnaryAST (
                 begin:PTR,
                 end:PTR,
                 val operator: OperatorAST,
                 val expression: AST
               ) extends AST(begin,end, List(expression)) {
  require(operator!=null)
  require(expression!=null)
  override def toString: String = s"UnaryAST ${operator.tok.text}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): UnaryAST = {
    require(what!=null)
    require(to!=null)
    if( operator==what && to.isInstanceOf[OperatorAST] )
      new UnaryAST(begin,end,to.asInstanceOf[OperatorAST],expression)
    else if( expression==what )
      new UnaryAST(begin,end,operator,to)
    else
      new UnaryAST(begin,end,
        operator.replace(what,to),
        to.replace(what,to)
      )
  }
}
