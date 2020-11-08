package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer}

/**
 * Комментарий
 *
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 */
class CommentTok(b1: CharPointer, e1: CharPointer ) extends CToken(b1,e1) {
  override def toString(): String = s"CommentTok $text"
}
