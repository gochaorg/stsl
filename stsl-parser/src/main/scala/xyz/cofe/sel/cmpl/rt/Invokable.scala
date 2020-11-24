package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.Fun
import xyz.cofe.sel.types.Type

/**
 * Вариант вызова
 * @param fn вызываемая функция
 * @param expectedParams ожидаемае параметры
 * @param expectedReturn ожидаемый результат
 * @param implicitParams имплицитное преобразование параметров
 */
@Deprecated
class Invokable(
                 val fn:Fun,
                 val expectedParams:List[Type],
                 val expectedReturn:Type = null,
                 val implicitParams:List[Fun] = List()
               )
{

  /**
   * "Перекрытие" мера схожести между типами данных функции (типами параметрами и возможно результатом)
   * @param exists фактический тип данных
   * @param expected ожидаемый тип данных
   */
  case class Overlap(val exists:Type, val expected:Type, val implConvFn:Fun=null) {
    lazy val assignableDistance: Option[Int] = expected.assignableDistance(exists)
  }

  /**
   * мера схожести между типами данных функции
   */
  object overlap {
    lazy val overlaps: List[Overlap] = {
      var ls : List[Overlap] = List()
      if( expectedReturn!=null ){
        ls = Overlap( fn.returnType, expectedReturn ) :: ls
      }
      val pls = (0 until Math.min(fn.params.length, expectedParams.length)).map(pi => {
        val existsType = fn.params(pi).paramType
        val expectType = expectedParams(pi)
        val implConvFn = if( pi<implicitParams.length )implicitParams(pi) else null;
        Overlap( existsType, expectType, implConvFn )
      })
      (pls ++ ls).toList
    }

    /** Минимальная дистанция */
    lazy val minDistance: Int = overlaps.map(_.assignableDistance).filter(_.isDefined).flatten.min

    /** Максимальная дистанция */
    lazy val maxDistance: Int = overlaps.map(_.assignableDistance).filter(_.isDefined).flatten.max

    /** Дистанции */
    lazy val distances:Seq[Int] = overlaps.map(_.assignableDistance).filter(_.isDefined).flatten

    lazy val implicitTypeConvCount : Int = overlaps.map( i => if(i.implConvFn!=null) 1 else 0 ).sum

    /**
     * Разница
     */
    lazy val difference: Int = distances.map(Math.abs).sum + implicitTypeConvCount
  }

  override def toString: String = {
    val expected = s"expected params: ${expectedParams.map(_.name).reduce((a, b)=>a+","+b)}"+
      (if(expectedReturn!=null) s" returns: ${expectedReturn.name}" else "")

    val implParams =
    if( implicitParams.nonEmpty ){
      s"impl.param=(${implicitParams.map(f=>if(f==null) "null" else f.toString)})"
    }else{
      ""
    }

    s"${fn}; ${expected}; overlaps{ difference=${overlap.difference}; distances=${overlap.distances}; impl.cnt=${overlap.implicitTypeConvCount}; $implParams }"
  }
}
