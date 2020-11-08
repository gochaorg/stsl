package xyz.cofe.sel.types

sealed trait GenericParam extends Assignable  {
  val name : String
  //val genericType : Type
  def assignable( gp:GenericParam ):Boolean = {
    require(gp!=null)
    gp match {
      case inv:InVariant => inv.genericType==inv.genericType
      case cov:CoVariant => cov.genericType.assignable(cov.genericType)
      case cnt:ContraVariant => cnt.genericType.assignable(cnt.genericType)
    }
  }
  def assignable( t:Type ):Boolean = {
    require(t!=null)
    this match {
      case inv:InVariant => inv.genericType==t
      case cov:CoVariant => cov.genericType.assignable(t)
      case cnt:ContraVariant => t.assignable(cnt.genericType)
      case anY:AnyVariant => true
    }
  }
}

case class AnyVariant( val name:String ) extends GenericParam {
  override def toString: String = {
    s"${name}:*"
  }
}
case class InVariant( val name:String, val genericType : Type ) extends GenericParam {
  override def toString: String = {
    s"${name}:${genericType}"
  }
}
case class CoVariant( val name:String, val genericType : Type ) extends GenericParam {
  override def toString: String = {
    s"${name}:${genericType}+"
  }
}
case class ContraVariant( val name:String, val genericType : Type ) extends GenericParam {
  override def toString: String = {
    s"${name}:${genericType}-"
  }
}

object GenericParam {
  lazy val empty : List[GenericParam] = List()
}
