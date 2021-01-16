package xyz.cofe.sparse

/**
 * Лексема числа
 * @param begin начало лексемы
 * @param end конец лексемы
 * @param value значение
 */
class IntergerTok(begin:CharPointer
                  , end:CharPointer
                  , val value:Int = 0 )
  extends CToken(begin,end) {
  override def toString: String = s"IntergerTok(begin=$begin,end=$end,value=$value)"
}

/**
 * Создание лексемы
 */
object IntergerTok {
  /**
   * Создание лексемы
   * @param begin начало лексемы
   * @param end конец лексемы
   * @param digits цифры
   * @param base основание числа (10 - десятичная система)
   * @return Лексема числа
   */
  def apply( begin:CharPointer, end:CharPointer, digits:Seq[DigitToken], base:Int = 10 ):IntergerTok = {
    val dgts = digits.reverse.zipWithIndex.map( { case (d,idx) =>
      if( idx==0 ) d.value else {
        var b = base
        (0 until idx-1).foreach( _ => {
          b = b * base
        })
        d.value * b
      }
    })
    new IntergerTok(begin,end,dgts.sum)
  }
}