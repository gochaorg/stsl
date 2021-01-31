package xyz.cofe.sparse

/**
 * Лексема цифры
 * @param begin начало лексемы
 * @param end конец лексемы
 * @param value значение цифры
 */
class DigitToken( begin:CharPointer
                  , end:CharPointer
                  , val value:Int )
  extends CToken(begin,end)

/**
 * Создание лексемы
 */
object DigitToken {
  /**
   * Создание лексемы
   * @param begin начало лексемы
   * @param end конец лексемы
   * @param value значение цифры
   */
  def apply( begin:CharPointer, end:CharPointer, value:Int ) = new DigitToken(begin,end,value)
}