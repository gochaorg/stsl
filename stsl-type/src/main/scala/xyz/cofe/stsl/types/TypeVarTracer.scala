package xyz.cofe.stsl.types

/**
 * Трессировка операции замены переменной типа
 */
trait TypeVarTracer {
  def apply[R](message: String)(code: => R): R
}

object TypeVarTracer {
  implicit val defaultTracer: TypeVarTracer = new TypeVarTracer {
    override def apply[R](message: String)(code: => R): R = code
  }
}