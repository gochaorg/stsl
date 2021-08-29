package xyz.cofe.stsl.types

import java.util

/**
 * Функция с возможностью вызова
 *
 * @param fgParams Список типов параметров
 * @param fParams Параметры функции
 * @param fReturn Возвращаемый тип данных
 * @param call Реализация функции
 */
class CallableFn( fgParams: GenericParams
                , fParams: Params
                , fReturn: Type
                , val call : Seq[Any] => Any
                ) extends Fn( fgParams, fParams, fReturn ) with Invoke {
  require(call!=null)

  def call( args:java.lang.Iterable[_] ):Any = {
    require(args!=null)
    var ls : List[Any] = List()
    args.forEach( a => {ls = a :: ls} )
    this.call.apply(ls.reverse)
  }

  /**
   * Вызов функции
   *
   * @param args аргументы
   * @return результат
   */
  override def invoke(args: Seq[Any]): Any = call(args)

  /**
   * Клонирование
   *
   * @param fgParams Определение переменных типа - функции
   * @param fParams  Параметры функции
   * @param fReturn  Результат функции
   * @return клон
   */
  override protected def clone(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = {
    new CallableFn(fgParams,fParams,fReturn,call)
  }
}

object CallableFn {
  def create( fgParams: java.util.List[GenericParam]
            , fParams: java.util.List[Param]
            , fReturn: Type
            , call: java.util.function.Function[java.util.List[Any],Any]
            ): CallableFn = {
    require(fgParams!=null)
    require(fParams!=null)
    require(fReturn!=null)
    require(call!=null)
    val fgParams1 = GenericParams(fgParams)
    new CallableFn(fgParams1, Params(fParams), fReturn, { args =>
      val paramz = new util.ArrayList[Any]()
      args.foreach { arg =>
        paramz.add(arg)
      }
      var res : Any = call.apply(paramz)
      res
    })
  }

  def create( fParams: java.util.List[Param]
            , fReturn: Type
            , call: java.util.function.Function[java.util.List[Any],Any]
            ) : CallableFn = {
    require(fParams!=null)
    require(fReturn!=null)
    require(call!=null)
    val gen = new GenericParams()
    new CallableFn(gen, Params(fParams), fReturn, { args =>
      val paramz = new util.ArrayList[Any]()
      args.foreach { arg =>
        paramz.add(arg)
      }
      var res : Any = call.apply(paramz)
      res
    })
  }
}
