package xyz.cofe.stsl.tok

import xyz.cofe.sparse.CToken

/**
 * Литерал
 *
 * @tparam A тип литератльного значения
 */
trait LiteralTok[A] extends CToken {
  /**
   * Значение литерала
   */
  val value:A
}
