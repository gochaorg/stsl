package xyz.cofe.stsl.types

case class Param(name:String, tip: Type) extends Named {
  require(name!=null)
  require(tip!=null)

  override def toString: String = s"${name}:${tip}"
}
