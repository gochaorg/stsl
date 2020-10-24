package xyz.cofe.stsl.types

class TypeError(message:String, cause:Throwable) extends Error(message,cause)

object TypeError {
  def apply(message: String, cause: Throwable): TypeError = new TypeError(message, cause)
  def apply(message: String): TypeError = new TypeError(message, null)
  def apply(cause: Throwable): TypeError = new TypeError("no message", cause)
}
