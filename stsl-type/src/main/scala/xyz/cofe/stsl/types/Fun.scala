package xyz.cofe.stsl.types

/**
 * Функция/метод
 *
 * <ul>
 *   <li>Функция сама по себе является типом
 *   <li>Функция может содержаеть параметрические типы (типы переменные)
 *   <li>Переменные типа (TypeVariable) могут ссылаться (owner):
 *     <ul>
 *       <li>На класс в случае метода: owner=THIS
 *       <li>На саму функцию: owner=FN
 *     </ul>
 *   <li>Метод в качестве первого аргмента должен принимать ссылку на объект, с типом THIS
 * </ul>
 *
 */
trait Fun extends Type with TypeVarReplace[Fun] with TypeVarFetch {
  lazy val parameters:Params = Params()
  lazy val returns:Type = Type.VOID
  override def assignable(t: Type): Boolean = super.assignable(t)
  override def toString: String = generics.toString+parameters.toString+":"+returns
  def sameTypes(f:Fun):Boolean = {
    require(f!=null)
    parameters.sameTypes(f.parameters) && generics.sameTypes(f.generics)
  }
  lazy val typeVariables: Seq[TypeVariable] = List()
}
