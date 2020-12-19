package xyz.cofe.stsl.types

/**
 * Поле объекта с поддержкой записи
 * @param name имя поля
 * @param tip тип поля
 * @param reading чтение поля
 * @param writing запись поля
 */
class WriteableField( name:String
                    , tip:Type
                    , val reading:Any=>Any
                    , val writing:(Any,Any)=>Any
                    ) extends Field( name, tip ) {
  require(reading!=null)
  require(writing!=null)
}
