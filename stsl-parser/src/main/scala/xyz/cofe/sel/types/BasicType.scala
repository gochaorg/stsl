package xyz.cofe.sel.types

/**
 * Базовый тип
 * @param name имя типа
 * @param extend какой тип расширяет
 */
class BasicType( val name:String, val extend:Option[Type] ) extends Type {
  /**
   * Конструктор
   * @param name имя типа
   */
  def this( name:String ) = {
    this(name, None)
  }
}
