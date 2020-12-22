package xyz.cofe.stsl.types

/**
 * Функция с возможностью вызова
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
