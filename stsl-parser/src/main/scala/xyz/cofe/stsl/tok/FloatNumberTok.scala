package xyz.cofe.stsl.tok

/**
 * Дробное десятичное число
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param value значение
 */
class FloatNumberTok(b1: CharPointer, e1: CharPointer, value:Float ) extends NumberTok(b1,e1,value){
  override def toString: String = s"FloatNumberTok ${value}"
}
