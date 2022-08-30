package xyz.cofe.stsl.types

/**
 * Производные типы данных - "наследование"
 */
trait Extendable extends Assignable {
  self =>

  /**
   * Возвращает родительский тип данных
   *
   * @return родительский тип
   */
  def extend: Option[Type] = None

  /**
   * Путь от родительского типа до текущего
   *
   * @return путь
   */
  def extendPath: List[Type] = {
    self match {
      case t: Type =>
        Iterator.iterate(t)(f => f.extend.orNull).takeWhile(f => f != null).toList.reverse
      case _ => List()
    }
  }

  /**
   * Проверяет что для текущего типа (this) возможна операция присвоения типа данных t с учетом наследования типов
   *
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  override def assignable(t: Type)(implicit tracer: AssignableTracer): Boolean = {
    require(t != null)
    tracer("Extendable", this, t)(
      if (this == t) {
        true
      } else {
        var x = t
        var matched = false
        while (x != null && !matched) {
          x = x match {
            case cov: CoVariant => cov.tip
            case _ => x
          }
          matched = this == x
          x = x.extend.orNull
        }
        matched
      }
    )
  }
}
