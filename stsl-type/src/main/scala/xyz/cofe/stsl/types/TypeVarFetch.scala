package xyz.cofe.stsl.types

/**
 * Извлечение переменных
 */
trait TypeVarFetch {
  /**
   * Извлечение переменных
   * @return список переменных
   */
  def typeVarFetch : List[TypeVarLocator]
}
