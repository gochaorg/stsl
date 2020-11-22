package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, TObject, Type}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet

/**
 * Варианты вызова метода объекта для занных аргументов
 * @param thiz тип объекта
 * @param method имя метода
 * @param args типы аргументов
 * @param implicitConversion неявное преобразование
 */
class CallCases(val thiz:TObject, val method: String, val args:List[Type], val implicitConversion:Seq[Fun]=List() ){
  lazy val ctypes : List[CallType] = thiz.methods.get(method).map(
    funs => funs.funs.map( fun =>
      new CallType( fun,
        fun.parameters.map( p => p.tip match {
          case THIS => thiz
          case _ => p.tip
        }).toList,
        args,
        fun.returns match {
          case THIS => thiz
          case _ => fun.returns
        }
      )
    )
  ).getOrElse( List() )

  lazy val usedTypes:Set[Type] = (
    ctypes.flatMap(_.actual) ++ ctypes.flatMap(_.expected) ++ ctypes.map(_.result)
    ).toSet

  lazy val typeGraph : PartialSet[Type] = PartialSet[Type](
    usedTypes,
    (a,b) => a == b,
    (a,b) => a.assignable(b)
  )

  lazy val cases : List[CallCase] = ctypes.map( c => new CallCase(c.fun, c.actual, c.expected, c.result, typeGraph, implicitConversion))
  lazy val minCost: Int = cases
    .filter( c => c.cost.isDefined )
    .map( c => c.cost.get )
    .min;

  lazy val preferred : List[CallCase] = cases
    .filter( c => c.cost.isDefined && c.cost.get==minCost )
}
