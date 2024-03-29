package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.AST

/**
 * Ошибка компиляции
 * @param message сообщение
 * @param cause причина
 * @param locations расположение в коде
 */
class ToasterError( message:String, cause:Throwable, val locations:List[AST]=List() ) extends Error(message,cause) {
  override def toString: String = {
    val sb = new StringBuilder()
    sb ++= message
    if( locations.nonEmpty ){
      locations.foreach( loc => {
        sb ++= "\n"
        sb ++= s"at ${loc.begin()} to ${loc.end()}"
      })
    }
    sb.toString()
  }
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
