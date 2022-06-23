package xyz.cofe.stsl.types

/**
 * Интерфейс класса,
 * любой клас обладает:
 * <ul>
 *   <li> Списком полей/атрибутов
 *   <li> Список методов
 * </ul>
 */
trait Obj extends Type {
  type FIELDS <: Fields
  type METHODS <: Methods
  
  /**
   * Список полей/атрибутов
   */
  val fields : FIELDS

  /**
   * Список методов полей/атрибутов
   */
  val methods : METHODS
}
