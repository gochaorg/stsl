package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, TObject, Type}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet

import scala.collection.immutable

/**
 * Варианты вызова метода объекта для занных аргументов
 * @param thiz тип объекта
 * @param method имя метода
 * @param args типы аргументов
 * @param typeScope Область типов
 */
class CallCases(val thiz:TObject, val method: String, val args:List[Type], val typeScope: TypeScope ){
  require(thiz!=null)
  require(args!=null)
  require(method!=null)

  /**
   * Типы данных для возможных вариантов вызова
   */
  val ctypes : List[CallType] = typeScope.callTypes(thiz, method, args)

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
