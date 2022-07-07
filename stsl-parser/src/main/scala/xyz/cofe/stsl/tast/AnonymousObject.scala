package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.TAnon

object AnonymousObject {
  val TypeDefinition: Object = new Object {
    override def toString: String = "TypeDefinition"
  }
  
  def definitionOf( anonObj:java.util.Map[Any,Any] ):Option[TAnon] = {
    require(anonObj!=null)
    anonObj.get(TypeDefinition) match {
      case d: TAnon => Some(d)
      case _ => None
    }
  }
}
