package xyz.cofe.stsl.types

/**
 * Функция с возможностью вызова
 */
class CallableFn( fgParams: GenericParams
                , fParams: Params
                , fReturn: Type
                , val call : Seq[_] => _
                ) extends Fn( fgParams, fParams, fReturn ) with Invoke {
  require(call!=null)

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
