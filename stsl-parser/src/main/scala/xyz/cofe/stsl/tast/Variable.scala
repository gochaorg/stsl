package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type

/**
 * Переменная
 */
trait Variable {
  /**
   * Чтение значения переменной
   * @return значение
   */
  def read():Any
  
  /**
   * Запись значения переменной
   * @param value значение
   */
  def write( value:Any )
  
  /**
   * Возвращает тип переменной
   * @return тип
   */
  def tip:Type
}

object Variable {
  def apply(varType: Type, init: Any): Variable = {
    require(varType!=null)
    new Variable {
      protected var value: Any = init
      override def read(): Any = value
      override def write(newValue: Any): Unit = value = newValue
      override def tip: Type = varType
    }
  }
}