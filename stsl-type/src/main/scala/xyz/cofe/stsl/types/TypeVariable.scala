package xyz.cofe.stsl.types

/**
 * Переменная типа - т.е переменная для подстановки типов
 * @param name Имя переменной
 * @param owner Владелей переменной, допускается владаец FN, THIS
 */
class TypeVariable( val name:String, val owner: Type ) extends Type with Named {
  require(owner!=null,"owner not defined")
  require(name!=null, "name not defined")
  if( !(owner==Type.FN || owner==Type.THIS) ){
    throw TypeError("owner must be FN | THIS")
  }
  override def toString: String = name
}

object TypeVariable {
  def apply(name: String, owner: Type): TypeVariable = new TypeVariable(name, owner)
}