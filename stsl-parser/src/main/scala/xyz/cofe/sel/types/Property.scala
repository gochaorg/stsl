package xyz.cofe.sel.types

/**
 * Поле
 */
trait Property {
  /**
   * Имя поля
   */
  val name : String

  /**
   * Тип поля
   */
  val propertyType : Type

  /**
   * Чтение свойства
   */
  val read: Any=>Any
}

object Property {
  def apply(fname : String, ftype : Type, fread:Any=>Any): Property = new Property {
    require(fname != null)
    require(ftype != null)
    require(fread != null)

    /**
     * Имя поля
     */
    override val name: String = fname
    /**
     * Тип свойства
     */
    override val propertyType: Type = ftype
    /**
     * Чтение свойства
     */
    override val read: Any => Any = fread
  }
}
