package xyz.cofe.stsl.tok

/**
 * Целое число
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param value значение
 */
class ShortNumberTok(b1: CharPointer, e1: CharPointer, value:Short ) extends NumberTok(b1,e1,value){
  override def toString: String = s"ShortNumberTok ${value}"
}
