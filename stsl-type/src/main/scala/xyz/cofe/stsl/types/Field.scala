package xyz.cofe.stsl.types

/**
 * Поле класса / Атрибут класса
 * @param name имя поля
 * @param tip тип поля
 */
case class Field( val name:String, val tip:Type ) extends Named {
}
