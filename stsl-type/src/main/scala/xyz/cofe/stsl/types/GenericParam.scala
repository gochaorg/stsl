package xyz.cofe.stsl.types

sealed trait GenericParam extends Type with Named {
  override lazy val extend: Option[Type] = None
  override lazy val generics: List[GenericParam] = List()
}

case class AnyVariant(name:String) extends GenericParam {
  require(name!=null)
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    true
  }
  override def toString: String = s"${name}:*"
}

case class CoVariant(name:String, tip:Type) extends GenericParam {
  require(name!=null)
  require(tip!=null)
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    tip.assignable(t)
  }
  override def toString: String = s"${name}:${tip}+"
}

case class ContraVariant(name:String, tip:Type) extends GenericParam {
  require(name!=null)
  require(tip!=null)
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    t.assignable(tip)
  }
  override def toString: String = s"${name}:${tip}-"
}