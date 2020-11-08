package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer}

/**
 * Оператор
 *
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param name имя оператора
 */
class OperatorTok (b1: CharPointer, e1: CharPointer, val name:String ) extends CToken(b1,e1) {
  override def toString(): String = s"OperatorTok $name"
}
