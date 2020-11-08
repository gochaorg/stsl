package xyz.cofe.sel.types

class TypeError(message:String, cause:Throwable) extends Error(message,cause) {
}

object TypeError {
  def apply(message: String, cause: Throwable=null): TypeError = new TypeError(message, cause)
}
