package xyz.cofe.stsl.types

/**
 * Производные типы данных - "наследование"
 */
trait Extendable extends Assignable {
  /**
   * Возвращает родительский тип данных
   * @return родительский тип
   */
  def extend : Option[Type] = None

  /**
   * Проверяет что для текущего типа (this) возможна операция присвоения типа данных t с учетом наследования типов
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    if( this==t ){
      true
    }else{
      var x = t
      var matched = false
      while( x!=null && !matched ){
        x = x match {
          case cov:CoVariant => cov.tip
          case _ => x
        }
        matched = this==x
        x = x.extend.orNull
      }
      matched
    }
  }
}
