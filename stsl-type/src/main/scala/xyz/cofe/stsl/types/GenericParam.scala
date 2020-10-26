package xyz.cofe.stsl.types

sealed trait GenericParam extends Type with Named {
  override lazy val extend: Option[Type] = None
  override lazy val generics: GenericParams = GenericParams()
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
    t match {
      case cov:CoVariant => tip.assignable(cov.tip)
      case _:ContraVariant => false
      case _:AnyVariant => false
      case _ => tip.assignable(t)
    }
  }
  override def toString: String = s"${name}:${tip}+"
}

case class ContraVariant(name:String, tip:Type) extends GenericParam {
  require(name!=null)
  require(tip!=null)
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    t match {
      case ctr:ContraVariant => ctr.tip.assignable(tip)
      case _:CoVariant => false
      case _:AnyVariant => false
      case _ => t.assignable(tip)
    }
  }
  override def toString: String = s"${name}:${tip}-"
}