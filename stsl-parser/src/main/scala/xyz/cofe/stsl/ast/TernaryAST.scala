package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Тренарный оператор
 *
 * <p>
 * Пример парсинг выражения
 * <pre>a ? b : c</pre>
 *
 * Дерево AST:
 * <pre>
 * TernaryAST ? :
 * -| IdentifierAST IdentifierTok a
 * -| IdentifierAST IdentifierTok b
 * -| IdentifierAST IdentifierTok c
 * </pre>
 *
 * @param begin начало в исходнике
 * @param end конец в исходнике
 * @param firstOperator оператор
 * @param secondOperator оператор
 * @param first левый операнд
 * @param second правый операнд
 * @param third правый операнд
 */
class TernaryAST(begin:PTR,
                 end:PTR,
                 val firstOperator: OperatorAST,
                 val secondOperator: OperatorAST,
                 val first:AST,
                 val second: AST,
                 val third: AST,
                ) extends AST(begin,end, List(first,second,third)) {
  require(first!=null)
  require(second!=null)
  require(third!=null)
  require(firstOperator!=null)
  require(secondOperator!=null)
  override def toString: String = s"TernaryAST ${firstOperator.tok.text} ${secondOperator.tok.text}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): TernaryAST = {
    require(what!=null)
    require(to!=null)
    if( firstOperator==what && to.isInstanceOf[OperatorAST] )
      new TernaryAST(begin,end,to.asInstanceOf[OperatorAST],secondOperator,first,second,third)
    else if( secondOperator==what && to.isInstanceOf[OperatorAST] )
      new TernaryAST(begin,end,firstOperator,to.asInstanceOf[OperatorAST],first,second,third)
    else if( first==what )
      new TernaryAST(begin,end,firstOperator,secondOperator,to,second,third)
    if( second==what )
      new TernaryAST(begin,end,firstOperator,secondOperator,first,to,third)
    if( third==what )
      new TernaryAST(begin,end,firstOperator,secondOperator,first,second,to)
    else
      new TernaryAST(
        begin,end,
        firstOperator.replace(what,to),
        secondOperator.replace(what,to),
        first.replace(what,to),
        second.replace(what,to),
        third.replace(what,to)
      )
  }
}
