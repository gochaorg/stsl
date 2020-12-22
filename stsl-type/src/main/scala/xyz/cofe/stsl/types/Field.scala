package xyz.cofe.stsl.types

/**
 * Поле класса / Атрибут класса
 * @param name имя поля
 * @param tip тип поля
 */
class Field( val name:String, val tip:Type ) extends Named {
  require(name!=null)
  require(tip!=null)

  /**
   * Создает поле класса с поддержкой чтения/записи
   * @param reading функция чтения, аргумент - объект, результат - значение поля
   * @param writing функция записи, аргументы:
   *                <ol>
   *                  <li> объект
   *                  <li> новозе значение для поля объекта/класса
   *                </ol>
   * @return Поле класса с поддержкой чтения/записи
   */
  def writeable(reading:Any=>Any, writing:(Any,Any)=>Any) : WriteableField = {
    require(reading!=null)
    require(writing!=null)
    new WriteableField(name,tip,reading,writing)
  }
}
