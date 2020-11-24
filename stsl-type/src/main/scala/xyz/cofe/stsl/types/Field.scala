package xyz.cofe.stsl.types

/**
 * Поле класса / Атрибут класса
 * @param name имя поля
 * @param tip тип поля
 */
class Field( val name:String, val tip:Type ) extends Named {
  require(name!=null)
  require(tip!=null)
  def writeable(reading:Any=>Any, writing:(Any,Any)=>Any) : WriteableField = {
    require(reading!=null)
    require(writing!=null)
    new WriteableField(name,tip,reading,writing)
  }
}
