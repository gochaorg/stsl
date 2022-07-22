package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.TAnon

/**
 * Работа с TAnon
 */
object AnonymousObject {
  /**
   * Ключ в Map
   */
  val TypeDefinition: Object = new Object {
    override def toString: String = "TypeDefinition"
  }
  
  /**
   * Получает тип для Map
   * @param anonObj экземпляр Map
   * @return тип
   */
  def definitionOf( anonObj:Any ):Option[TAnon] = {
    require(anonObj!=null)
    anonObj match {
      case m:java.util.Map[Any,Any] =>
        m.get(TypeDefinition) match {
          case d: TAnon => Some(d)
          case _ => None
        }
      case _ => None
    }
  }
}
