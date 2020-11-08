package xyz.cofe.stsl.tok

import xyz.cofe.sparse.CharPointer

/**
 * Целое число
 *
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param value значение
 */
class ByteNumberTok(b1: CharPointer, e1: CharPointer, value:Byte ) extends NumberTok(b1,e1,value){
  override def toString: String = s"ByteNumberTok ${value}"
}
