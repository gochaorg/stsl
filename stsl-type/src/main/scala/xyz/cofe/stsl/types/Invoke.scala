package xyz.cofe.stsl.types

/**
 * Вызов функции
 */
trait Invoke {
  /**
   * Вызов функции
   * @param args аргументы
   * @return результат
   */
  def invoke(args:Seq[Any]):Any
}
