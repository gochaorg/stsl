package xyz.cofe.sparse

/**
  * Указатель на символьную последовательность
  */
trait CharPointer extends Pointer[Char,Int,CharPointer] {
  /**
    * Возвращает текст заданной длинны (или меньше) относительно указателя
    * @param len какой максимальной длины текст
    * @return текст
    */
  def text(len:Int):String
}

/**
  * Указатель на символьную последовательность
  */
object CharPointer {
  /**
    * Создание указателя
    * @param str текст
    * @param ptr начальная позиция
    * @return указатель
    */
  def of( str:String, ptr:Int=0 ):CharPointer = new BasicCharPointer(ptr,str)
}