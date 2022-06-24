package xyz.cofe.stsl.types

/**
 * Свойство - список методов
 */
trait MethodsProperty {
  /**
   * Тип контейнера методов
   */
  type METHODS <: Methods
  
  /**
   * Список методов полей/атрибутов
   */
  val methods : METHODS
}
