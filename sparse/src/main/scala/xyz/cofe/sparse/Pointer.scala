package xyz.cofe.sparse

/**
  * Указаетль на лексемы/ноды в списке
  * @tparam TOKEN тип лексемы
  * @tparam PTR тип указателя
  */
trait Pointer[TOKEN,PTR,SELF <: Pointer[TOKEN,PTR,SELF]] extends Ordered[Pointer[TOKEN,PTR,SELF]] {
  /**
    * Получение значения текущего указателя
    * @return указатель
    */
  def pointer():PTR

  /**
    * Перемещение указателя n позиций вперед/назад
    * @param n кол-во позиций
    * @return Новый указатель
    */
  def move(n:Int):SELF

  /**
    * Предпросмотр n-ой лексемы относительно текущего указателя
    * @param n Номер лексемы/символа
    * @return Лексема или символ
    */
  def lookup(n:Int):Option[TOKEN]

  /**
    * Проверка что достигнут конец
    * @return true - достигнут конец
    */
  def eof():Boolean
}
