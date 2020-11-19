package xyz.cofe.stsl.ast

import Parser.PTR

/**
 * Ссылка на свойство объекта.
 *
 * <p>
 * Пример парсинг выражения
 * <pre>x.a + x.b.c</pre>
 *
 * Дерево AST:
 * <pre>
 * BinaryAST +
 * -| PropertyAST a
 * -|-| IdentifierAST IdentifierTok x
 * -| PropertyAST c
 * -|-| PropertyAST b
 * -|-|-| IdentifierAST IdentifierTok x
 * </pre>
 *
 * @param begin начало в тексте
 * @param end конец в тексте
 * @param obj объект
 * @param name имя свойства
 */
class PropertyAST( begin:PTR,
                   end:PTR,
                   val obj:AST,
                   val name:IdentifierAST
                 ) extends AST(begin,end, List(obj)) {
  require(obj!=null)
  require(name!=null)
  override def toString: String = s"PropertyAST ${name.tok.text}"

  /**
   * Клонирование и замена дочернего узла
   *
   * @param what Что заеняется
   * @param to   На что заменяется
   * @return клон с новым значением
   */
  override def replace(what: AST, to: AST): PropertyAST = {
    require(what!=null)
    require(to!=null)

    if( what==obj )
      new PropertyAST(begin,end, to, name)
    else if( what==name && to.isInstanceOf[IdentifierAST] )
      new PropertyAST(begin,end,obj,to.asInstanceOf[IdentifierAST])
    else
      this
  }
}
