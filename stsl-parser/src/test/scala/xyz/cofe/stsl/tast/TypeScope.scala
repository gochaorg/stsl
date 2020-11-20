package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type

/**
 * Область "видимых" типов данных
 */
trait TypeScope {
  def types:List[Type]
}
