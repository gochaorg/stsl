package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Бинарный оператор
 * @param begin начало в исходнике
 * @param end конец в исходнике
 * @param operator оператор
 * @param left левый операнд
 * @param right правый операнд
 */
class BinaryAST ( begin:PTR,
                  end:PTR,
                  val operator: OperatorAST,
                  val left:AST,
                  val right: AST
                ) extends AST(begin,end, List(left,right)) {
  require(left!=null)
  require(right!=null)
  require(operator!=null)
  override def toString: String = s"BinaryAST ${operator.tok.text}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): BinaryAST = {
    require(what!=null)
    require(to!=null)
    if( what==operator && to.isInstanceOf[OperatorAST] )
      new BinaryAST(begin,end,to.asInstanceOf[OperatorAST],left,right)
    else if( what==left )
      new BinaryAST(begin,end,operator,to,right)
    else if( what==right )
      new BinaryAST(begin,end,operator,left,to)
    else
      new BinaryAST(
        begin, end,
        operator.replace(what,to),
        left.replace(what,to),
        right.replace(what,to)
      )
  }
}
