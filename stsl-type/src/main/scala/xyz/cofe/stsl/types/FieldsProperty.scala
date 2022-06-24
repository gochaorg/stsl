package xyz.cofe.stsl.types

/**
 * Свойство field которое может присутствовать у объектов и других структур
 */
trait FieldsProperty {
  /**
   * Тип контейнера полей
   */
  type FIELDS <: Fields
  
  /**
   * Список полей/атрибутов
   */
  val fields : FIELDS
}
