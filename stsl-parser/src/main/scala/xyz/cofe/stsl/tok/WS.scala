package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer}

/**
 * Пробельная последовательность символов
 *
 * @param b1 начало последовательности
 * @param e1 конец последовательности
 */
class WS(b1: CharPointer, e1: CharPointer ) extends CToken(b1,e1){
  override def toString: String = "WS"
};
