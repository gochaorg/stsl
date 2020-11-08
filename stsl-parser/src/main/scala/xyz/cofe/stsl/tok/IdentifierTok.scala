package xyz.cofe.stsl.tok

/**
 * Идентификатор
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param name имя
 */
class IdentifierTok (b1: CharPointer, e1: CharPointer, val name:String ) extends CToken(b1,e1) {
  override def toString(): String = s"IdentifierTok $name"
}
