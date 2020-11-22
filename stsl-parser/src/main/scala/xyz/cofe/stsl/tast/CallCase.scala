package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, Invoke, Type}
import xyz.cofe.stsl.types.pset.PartialSet

/**
 * Вариант вызова метода
 * @param fun вызываемый метод
 * @param actual актуальные параметры метода
 * @param expected ожидаемые параметры метода
 * @param result результат вызова метода
 * @param typeGraph граф преобразвания
 * @param implicitConversion список имплицитных функций для преобразования типа аргументов
 */
class CallCase( val fun:Fun
                , val actual:List[Type]
                , val expected:List[Type]
                , val result:Type
                , val typeGraph:PartialSet[Type]
                , val implicitConversion:Seq[Fun]=List() ) extends Invoke {
  val argCountMatched: Boolean = actual.size == expected.size

  //region passing

  val passing : Option[List[CallPassing]] = if( !argCountMatched ){
    None
  }else{
    if( actual.isEmpty ){
      Some(List())
    }else{
      val pssng1 = actual.zip(expected).map({
        case (actualParam, expectedParam)=>
          if( actualParam.assignable(expectedParam) ){
            if( actualParam==expectedParam ){
              new CallPassing(actualParam,expectedParam)
            }else{
              new CallPassing(actualParam,expectedParam,typeGraph.ascending(actualParam,expectedParam))
            }
          }else{
            val conv = implicitConversion
              .filter(f => f.parameters.length == 1)
              .filter(f => f.parameters.head.tip.assignable(expectedParam))
              .find(f => actualParam.assignable(f.returns))

            if( conv.isDefined ){
              new CallPassing(actualParam,expectedParam,List(),conv)
            }else{
              null
            }
          }
      })
      if( pssng1.contains(null) ){
        None
      }else {
        Some(pssng1)
      }
    }
  }
  //endregion

  val passable : Boolean = passing.isDefined
  val callable : Boolean = argCountMatched && passable
  val cost: Option[Int] = passing.map(chances => chances.map(ch => ch.cost(this) ).sum )

  override def toString: String =
    s"""|fun=$fun
        |  callable= $callable
        |  passable= $passable
        |  cost=     $cost
        |  passing=  ${passing}
        |  actual=   $actual
        |  expected= $expected
        |  result=   ${result}""".stripMargin

  /**
   * Вызов функции
   *
   * @param args аргументы
   * @return результат
   */
  override def invoke(args: Seq[Any]): Any = {
    if( passing.isDefined ){
      val psng = passing.get
      val passArg = psng.indices.zip(psng).map({case (pi,ps)=>
        if( ps.conversion.isDefined ){
          val fc = ps.conversion.get
          fc match {
            case invoke1: Invoke =>
              invoke1.invoke(List(args(pi)))
            case _ =>
              throw new RuntimeException(s"can't invoke implicit fun=${fc}")
          }
        }else{
          args(pi)
        }
      })

      fun match {
        case invoke1: Invoke =>
          invoke1.invoke(passArg)
        case _ =>
          throw new RuntimeException(s"can't invoke fun=${fun}")
      }
    }
  }
}
