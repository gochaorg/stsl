package xyz.cofe.sel

import xyz.cofe.stsl.ast.AST

class ToasterError( message:String, cause:Throwable, val locations:List[AST]=List() ) extends Error(message,cause) {

}

object ToasterError {
  def apply(message: String, cause: Throwable=null, locations: List[AST]=List()): ToasterError = new ToasterError(message, cause, locations)

  def apply(message: String, locations: AST*): ToasterError = {
    require(message!=null)
    new ToasterError(message, null, locations.toList)
  }

  def apply(message: String, locations: List[AST]): ToasterError = {
    require(message!=null)
    require(locations!=null)
    new ToasterError(message, null, locations)
  }
}
