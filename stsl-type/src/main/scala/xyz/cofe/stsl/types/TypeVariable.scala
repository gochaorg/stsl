package xyz.cofe.stsl.types

case class TypeVariable(name:String, owner: Type ) extends Type with Named {
  require(owner!=null,"owner not defined")
  require(name!=null, "name not defined")
  override def toString: String = name
}
