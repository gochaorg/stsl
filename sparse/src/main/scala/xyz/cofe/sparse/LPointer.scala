package xyz.cofe.sparse

/**
 * Указатель над списоком вхоных символов
 * @param ptr начальное значение указателя (индекс в списке)
 * @param list список входных символов
 * @tparam TOKEN тип символов
 */
class LPointer[TOKEN]( ptr:Int, val list: List[TOKEN] )
extends Pointer[TOKEN,Int,LPointer[TOKEN]] {
  require(list!=null)

  /**
    * Получение значения текущего указателя
    *
    * @return указатель
    */
  override def pointer(): Int = ptr

  /**
    * Перемещение указателя n позиций вперед/назад
    *
    * @param n кол-во позиций
    * @return Новый указатель
    */
  override def move(n: Int): LPointer[TOKEN] = new LPointer[TOKEN]( ptr+n, list )

  /**
    * Предпросмотр n-ой лексемы относительно текущего указателя
    *
    * @param n Номер лексемы/символа
    * @return Лексема или символ
    */
  override def lookup(n: Int): Option[TOKEN] = {
    val t = pointer() + n
    if( t>=0 && t<list.size ){
      Some(list(t))
    }else{
      None
    }
  }

  /**
    * Проверка что достигнут конец
    *
    * @return true - достигнут конец
    */
  override def eof(): Boolean = ptr < 0 || ptr >= list.size

  override def compare(that: Pointer[TOKEN, Int, LPointer[TOKEN]]): Int = pointer().compareTo( that.pointer() )
}
