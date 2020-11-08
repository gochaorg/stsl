package xyz.cofe.stsl.ast

/**
 * Итератор по дереву AST
 * @param workSet рабочий набор узлов
 */
class ASTIterator ( private var workSet : List[ASTPath] ) extends Iterator[ASTPath] {
  require(workSet!=null)
  override def hasNext: Boolean = workSet.nonEmpty
  override def next(): ASTPath = {
    if( workSet.nonEmpty ){
      val ret : ASTPath = workSet.head
      workSet = workSet.drop(1)
      workSet = ret.last.children().map( ch => ret + ch ) ++ workSet
      ret
    } else {
      throw new NoSuchElementException
    }
  }
}
