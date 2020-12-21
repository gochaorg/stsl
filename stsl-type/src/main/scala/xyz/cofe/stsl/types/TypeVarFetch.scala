package xyz.cofe.stsl.types

/**
 * Извлечение переменных {@link xyz.cofe.stsl.types.TypeVariable}
 */
trait TypeVarFetch {
  /**
   * Извлечение переменных
   * @param from Путь относительно которого происходит вызов
   * @return список переменных
   */
  def typeVarFetch(from:List[Any] = List()) : List[TypeVarLocator]
}
