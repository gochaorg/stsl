package xyz.cofe.stsl.types

/**
 * "Заморозка" изменений в мутабельном объекте,
 * Разморозки не предполагается - только клонирование
 */
trait Freezing {
  /**
   * Проверка что объект уже заморожен
   * @return true - объект уже заморожен, его нельзя изменять
   */
  def freezed:Boolean

  /**
   * Заморозка объекта
   */
  //noinspection UnitMethodIsParameterless
  def freeze:Unit
}
