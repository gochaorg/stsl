package xyz.cofe.stsl.types

/**
 * Проверка возможности выполнеия операции присвоения (или передачи значения) над типами данных
 *
 * <p>
 * Допустим есть две переменных разных типов:
 * <ul>
 *   <li> a : number
 *   <li> b : int
 * </ul>
 *
 * тогда:
 * <ul>
 *   <li> a.<i>assignable</i>( b ) = true
 *   <li> b.<i>assignable</i>( a ) = false
 * </ul>
 */
trait Assignable {
  /**
   * Проверяет что для текущего типа (this) возможна операция присвоения типа данных t
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  def assignable( t:Type )(implicit tracer:AssignableTracer):Boolean = {
    tracer(s"Assignable from ${t}")(this==t)
  }
}
