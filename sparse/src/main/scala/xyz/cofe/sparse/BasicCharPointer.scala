package xyz.cofe.sparse

/**
  * Указатель на символы
 *
  * @param ptr указатель
  * @param text текст
  */
class BasicCharPointer(val ptr:Int, val text:String ) extends CharPointer {
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
  override def move(n: Int): BasicCharPointer = if( n==0 ) { this } else { new BasicCharPointer(ptr+n,text) }

  /**
    * Предпросмотр n-ой лексемы относительно текущего указателя
    *
    * @param n Номер лексемы/символа
    * @return Лексема или символ
    */
  override def lookup(n: Int): Option[Char] = if( eof() ){ None }else{
    if( n==0 ) {
      Some(text.charAt(ptr))
    }else{
      if( (ptr+n)<0 ){
        None
      }else if( (ptr+n)>=text.length ){
        None
      }else{
        Some(text.charAt(ptr+n))
      }
    }
  }

  /**
    * Проверка что достигнут конец
    *
    * @return true - достигнут конец
    */
  override def eof(): Boolean = text==null || ( ptr < 0 || ptr >= text.length )

  /**
    * Возвращает текст заданной длинны (или меньше) относительно указателя
    * @param len какой максимальной длины текст
    * @return текст
    */
  override def text(len: Int): String = {
    if( eof() ){
      ""
    }else{
      if( len<=0 ){
        ""
      }else{
        if( text==null ){
          ""
        }else{
          if( ptr<0 ){
            ""
          }else{
            if( ptr+len>text.length ){
              text.substring(ptr, text.length)
            }else{
              text.substring(ptr, ptr+len)
            }
          }
        }
      }
    }
  }

  /**
   * Сравнение расположения указателей
   * @param that сравниваемый указатель
   * @return
   */
  override def compare(that: Pointer[Char, Int, CharPointer]): Int = ptr.compareTo( that.pointer() )

  override def toString: String = {
    s"ptr=$ptr"
  }
}
