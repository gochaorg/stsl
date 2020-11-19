package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Вызов метода/функции
 *
 * <p>
 * Пример парсинг выражения
 * <pre>a( 10, 12 ) + b.a( 1, 2, 3 )</pre>
 *
 * Дерево AST:
 * <pre>
 * BinaryAST +
 * -| CallAST
 * -|-| IdentifierAST IdentifierTok a
 * -|-| LiteralAST IntNumberTok 10
 * -|-| LiteralAST IntNumberTok 12
 * -| CallAST
 * -|-| PropertyAST a
 * -|-|-| IdentifierAST IdentifierTok b
 * -|-| LiteralAST IntNumberTok 1
 * -|-| LiteralAST IntNumberTok 2
 * -|-| LiteralAST IntNumberTok 3
 * </pre>
 *
 * @param begin начало в исходнике
 * @param end конец в исходнике
 * @param callable вызываемая функция/метод
 * @param arguments аргументы вызова
 */
class CallAST( begin:PTR,
               end:PTR,
               val callable:AST,
               val arguments:List[AST]
             ) extends AST(begin,end, List(callable) ++ arguments )
{
  require(callable!=null)
  require(arguments!=null)
  override def toString: String = s"CallAST"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): CallAST = {
    require(what!=null)
    require(to!=null)
    if( what==callable )
      new CallAST(begin,end,to,arguments)
    else {
      if( arguments.contains(what) ){
        val ls : List[AST] = arguments.map( arg => if(arg==what) to else arg )
        new CallAST(begin,end,callable,ls)
      }else{
        new CallAST(
          begin,end,
          callable.replace(what,to),
          arguments.map( arg => arg.replace(what,to) )
        )
      }
    }
  }
}
