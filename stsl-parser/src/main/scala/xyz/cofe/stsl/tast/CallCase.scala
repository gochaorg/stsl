package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, Invoke, Type}
import xyz.cofe.stsl.types.pset.PartialSet

/**
 * Вариант вызова метода
 * @param fun вызываемый метод
 * @param actual актуальные параметры метода
 * @param expected ожидаемые параметры метода
 * @param result результат вызова метода
 * @param typeScope Область типов
 */
class CallCase( val fun:Fun
                , val actual:List[Type]
                , val expected:List[Type]
                , val result:Type
                , val typeScope: TypeScope ) {
  require(fun!=null)
  require(actual!=null)
  require(expected!=null)
  require(result!=null)
  require(typeScope!=null)

  protected def log(message:String):Unit = {
    //println("CallCase "+message)
  }

  log(s"fun=${fun} actual=${actual} expected=${expected}")

  /**
   * Совпало-ли кол-во ожидаемых и актульаных аргументов
   */
  val argCountMatched: Boolean = actual.size == expected.size
  log(s"argCountMatched=$argCountMatched")

  //region passing

  log("compute passing")
  /**
   * Описывает правила передачи аргументов в функцию
   */
  val passing : Option[List[CallPassing]] = if( !argCountMatched ){
    log("!! argCountMatched")
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
              new CallPassing(actualParam,expectedParam, typeScope.graph.ascending(actualParam,expectedParam))
            }
          }else{
            val conv = typeScope.implicits
              .filter(f => f.parameters.length == 1)
              .filter(f => f.parameters.head.tip.assignable(expectedParam))
              .find(f => actualParam.assignable(f.returns))

            if( conv.isDefined ){
              new CallPassing(actualParam,expectedParam,List(),conv)
            }else{
              log(s"!! not found implicit between actualParam=$actualParam expectedParam=$expectedParam")
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
  log(s"passing=${passing}")
  //endregion

  /**
   * Имеется ли возможность передачи аргументов в функцию
   */
  val passable : Boolean = passing.isDefined
  log(s"passable=$passable")

  /**
   * Функцию можно вызвать с указанными аргументами
   */
  val callable : Boolean = argCountMatched && passable
  log(s"callable=$callable")

  /**
   * "Цена" вызова из расчета преобразований типов
   */
  val cost: Option[Int] = passing.map(chances => chances.map(ch => ch.cost(this) ).sum )
  log(s"cost=$cost")

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
   * Возвращает интерфейс вызова и тип возвращаемого значения
   * @return интерфейс вызова и тип
   */
  def invoking(): (Invoke,Type) = {
    if( !callable ){
      throw ToasterError("not callable")
    }

    val passingList = passing.get
    val passingFuns : List[Seq[Any]=>Any] =
      passingList.indices.zip(passingList).map({ case(pi,ps) =>
        if( ps.conversion.isDefined ){
          val fc = ps.conversion.get
          fc match {
            case invoke1: Invoke =>
              (args:Seq[Any]) => invoke1.invoke(List(args(pi)))
            case _ =>
              throw ToasterError(s"can't invoke implicit fun=${fc}")
          }
        }else{
          (args:Seq[Any]) => args(pi)
        }
      }).toList

    val invkTrgt : Invoke = fun match {
      case i:Invoke => i
      case _ => throw ToasterError(s"can't invoke fun=${fun}")
    }

    val invk : Invoke = new Invoke {
      override def invoke(args: Seq[Any]): Any = invkTrgt.invoke(passingFuns.map( pf => pf(args)))
    }

    (invk, result)
  }
}
