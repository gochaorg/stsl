package xyz.cofe.sel.ast

import xyz.cofe.sparse.{CToken, LPointer, Tok}
import xyz.cofe.sel.Parser
import xyz.cofe.sel.Parser.PTR

/**
 * AST узел
 * @param beginPointer начало в исходнике
 * @param endPointer конец в исходнике
 * @param childrenFn список дочерних узлов или null
 */
abstract class AST (
                     private val beginPointer : PTR,
                     private val endPointer : PTR,
                     private val childrenFn : ()=>List[AST]
                   ) extends Tok[PTR] {
  require(beginPointer!=null)
  require(endPointer!=null)

  private val empty:List[AST] = List()

  /**
   * AST узел
   * @param begin начало в исходнике
   * @param end конец в исходнике
   */
  def this( begin:PTR, end:PTR ) {
    this( begin, end, ()=>List() )
  }

  /**
   * AST узел
   * @param begin начало в исходнике
   * @param end конец в исходнике
   */
  def this( begin:PTR, end:PTR, childList:List[AST] ) {
    this( begin, end, () => childList)
    require(childList!=null)
  }

  /**
   * Возвращает список дочерних узлов
   * @return дочерние узлы
   */
  def children(): List[AST] = {
    if( childrenFn!=null ){
      childrenFn()
    } else empty
  }

  /**
   * Возвращает указатель на начало лексемы
   * @return Указатель
   */
  def begin(): PTR = this.beginPointer

  /**
   * Возвращает указатель на конец лексемы
   * @return Указатель
   */
  override def end(): PTR = this.endPointer

  /**
   * Итератор по дереву AST
   * @return Дерево
   */
  def tree : ASTIterator = new ASTIterator(List(ASTPath(this)))

  /**
   * Клонирование и замена дочернего узла
   * @param what Что заеняется
   * @param to На что заменяется
   * @return клон с новым значением
   */
  def replace( what:AST, to:AST ): AST = this
}
