package xyz.cofe.stsl.tok

/**
 * Строковой литерал
 * @param b1 начало последовательности
 * @param e1 конец последовательности
 * @param value значение
 */
class StringTok(b1: CharPointer, e1: CharPointer, val value:String ) extends CToken(b1,e1) with LiteralTok[String] {
  override def toString: String = s"StringTok ${value}"
};
