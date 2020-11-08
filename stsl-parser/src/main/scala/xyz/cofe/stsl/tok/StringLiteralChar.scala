package xyz.cofe.stsl.tok

/**
 * Пробельная последовательность символов
 *
 * @param b1 начало последовательности
 * @param e1 конец последовательности
 */
class StringLiteralChar(b1: CharPointer, e1: CharPointer, val decoded:String) extends CToken(b1,e1) {
  override def toString: String = "StrLiteralChar"
};
