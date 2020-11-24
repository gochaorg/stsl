package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type

trait Variable {
  def read():Any
  def write( value:Any )
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