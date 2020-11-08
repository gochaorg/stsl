package xyz.cofe.stsl.tok

/**
 * Целое 8 байтовое число
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param value значение
 */
class LongNumberTok(b1: CharPointer, e1: CharPointer, value:Long ) extends NumberTok(b1,e1,value){
  override def toString: String = s"LongNumberTok ${value}"
}
