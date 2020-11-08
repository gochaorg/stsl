package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer}

/**
 * Число
 *
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param value значение
 */
class NumberTok(b1: CharPointer, e1: CharPointer, val value:Number ) extends CToken(b1,e1) with LiteralTok[Number] {
  override def toString: String = s"NumberTok ${value}"
}
