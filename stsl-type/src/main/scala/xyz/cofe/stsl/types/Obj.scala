package xyz.cofe.stsl.types

/**
 * Интерфейс класса,
 * любой клас обладает:
 * <ul>
 *   <li> Списком полей/атрибутов
 *   <li> Список методов
 * </ul>
 */
trait Obj extends Type with Named {
  /**
   * Список полей/атрибутов
   */
  lazy val fields : Fields = Fields()

  /**
   * Список методов полей/атрибутов
   */
  lazy val methods : Methods = new Methods()
}
