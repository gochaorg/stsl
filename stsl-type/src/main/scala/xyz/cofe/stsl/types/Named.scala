package xyz.cofe.stsl.types

/**
 * Именнованое значение, например тип данных или поле класса
 */
trait Named {
  /**
   * Возвращает наименование
   * @return наименование
   */
  def name:String
}
