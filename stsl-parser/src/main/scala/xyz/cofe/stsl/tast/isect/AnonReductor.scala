package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.TAnon

/**
 * Редукция аккумулятора к анонимному типу
 *
 * @tparam A аккумулятор разных типов
 */
trait AnonReductor[A] {
  /**
   * Редукция аккумулятора к анонимному типу
   * @param acum аккумулятор разных типов
   * @return анонимный тип
   */
  def reduce(acum:A):TAnon
}
