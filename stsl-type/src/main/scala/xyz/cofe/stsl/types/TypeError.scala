package xyz.cofe.stsl.types

/**
 * Ошибка при работе с типами данных, может быть как ошибка компилятора, так и пользователя компилятора
 * @param message сообщение
 * @param cause причина или null
 */
class TypeError(message:String, cause:Throwable) extends Error(message,cause)

object TypeError {
  def apply(message: String, cause: Throwable): TypeError = new TypeError(message, cause)
  def apply(message: String): TypeError = new TypeError(message, null)
  def apply(cause: Throwable): TypeError = new TypeError("no message", cause)
}
