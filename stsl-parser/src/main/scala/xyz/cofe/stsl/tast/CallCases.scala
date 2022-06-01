package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, TObject, Type}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet

import scala.collection.immutable

/**
 * Варианты вызова метода объекта для заданных аргументов
 * @param ctypes Типы данных для возможных вариантов вызова
 * @param typeScope Область типов
 */
class CallCases(val ctypes : List[CallType], val typeScope: TypeScope ){
  require(ctypes!=null)

  /**
   * Типы вариантов вызовов
   */
  val cases : List[CallCase] = ctypes.map( c => new CallCase(c.fun, c.actual, c.expected, c.result, typeScope ))

  protected val costVariants: Seq[Int] = cases
    .filter( c => c.cost.isDefined )
    .map( c => c.cost.get )

  /**
   * Поиск минимальной "цены" из различных вариантов вызовов
   */
  val minCost: Option[Int] = if( costVariants.nonEmpty ) Some(costVariants.min) else None;

  /**
   * Возвращает предпочтительные варианты вызовов (с минимальной ценой)
   */
  val preferred : List[CallCase] = if( minCost.isDefined) {
    cases.filter(c => c.cost.isDefined && c.cost.get == minCost.get)
  } else List()
}
