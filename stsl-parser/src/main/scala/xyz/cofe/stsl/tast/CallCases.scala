package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, TObject, Type}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet

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

  val ctypes : List[CallType] = typeScope.callTypes(thiz, method, args)

  val cases : List[CallCase] = ctypes.map( c => new CallCase(c.fun, c.actual, c.expected, c.result, typeScope ))
  val minCost: Int = cases
    .filter( c => c.cost.isDefined )
    .map( c => c.cost.get )
    .min;

  val preferred : List[CallCase] = cases
    .filter( c => c.cost.isDefined && c.cost.get==minCost )
}
