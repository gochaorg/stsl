package xyz.cofe.sparse

import xyz.cofe.sparse.Chars.Whitespace

/**
 * Пример лексического анализа
 */
object LexerSample {
  // Цифры
  val digits = "0123456789"

  // Правило распознавания цифры
  val digit : GR[CharPointer,DigitToken] = ptr => ptr.lookup(0).map( chr => {
    val i = digits.indexOf(chr)
    if( i<0 ){
      None
    } else {
      Some( DigitToken(ptr,ptr.move(1), i) )
    }
  }).get

  // Импорт дополнительных операторов
  import xyz.cofe.sparse.GOPS._

  // Правило распознавания числа
  val intNumber: GR[CharPointer, IntergerTok] = digit + digit*0 ==> { (a, b) =>
    IntergerTok(a.begin, b.end, List(a) ++ b.toList )
  }

  /** Пробел */
  val ws: GR[CharPointer, WS] =
    (Whitespace * 1) ==> ((t) => new WS(t.head.begin, t.last.end));
}
