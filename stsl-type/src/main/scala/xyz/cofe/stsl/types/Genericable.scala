package xyz.cofe.stsl.types

/**
 * Патаметры типа
 */
trait Genericable extends Assignable {
  type GENERICS <: GenericParams
  
  /**
   * Список параметров типа
   */
  def generics : GENERICS // = GenericParams()

  /**
   * Проверка возможности присвоение с учетом параметров типа
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    val gAsgn = generics.assignable(t.generics)
    gAsgn && super.assignable(t)
  }
}
