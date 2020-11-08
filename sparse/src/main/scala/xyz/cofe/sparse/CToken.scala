package xyz.cofe.sparse

/**
 * Текстовая лексема
 * @param begin начало лексемы
 * @param end конец лексемы
 */
case class CToken( val begin : CharPointer, val end : CharPointer ) extends Tok[CharPointer] {
  lazy val text = {
    begin.text( end.pointer() - begin.pointer() )
  }

  override def toString: String = s"CToken(begin=${begin.pointer()},end=${end.pointer()},text=$text)"
}

/**
 * Конструирование текстовых лексем
 */
object CToken {
  /**
   * Создание лексемы из списка лексем
   * @param toks список лексем, последовательность важна
   * @tparam A тип лексем
   * @return Лексема
   */
  def apply[A <: CToken](toks:Seq[A]):CToken = new CToken(toks.head.begin, toks.last.end)
}