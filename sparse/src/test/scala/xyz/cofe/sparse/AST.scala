package xyz.cofe.sparse

import ParserSample.PTR

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
}
