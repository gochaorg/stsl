package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.Type

/**
 * Коллектор анонимных типов
 *
 * @tparam A аккумулятор разных типов
 */
trait AnonCollector[A] {
  /** Начальное значением аккумулятора */
  def initial:A

  /**
   * аккумуляция
   * @param acum аккумулятор
   * @param obj анонимный тип
   * @return аккумулятор
   */
  def collect(acum:A,obj:Type):A
}

