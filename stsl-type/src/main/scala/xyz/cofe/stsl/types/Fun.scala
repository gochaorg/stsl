package xyz.cofe.stsl.types

/**
 * Функция/метод
 *
 * <ul>
 *   <li>Функция сама по себе является типом
 *   <li>Функция может содержать параметрические типы (типы переменные)
 *   <li>Переменные типа (TypeVariable) могут ссылаться (owner):
 *     <ul>
 *       <li>На класс в случае метода: owner=THIS
 *       <li>На саму функцию: owner=FN
 *     </ul>
 *   <li>Метод в качестве первого аргумента должен принимать ссылку на объект, с типом THIS
 * </ul>
 *
 */
trait Fun extends Type with TypeVarReplace[Fun] with TypeVarFetch {
  /**
   * Параметры функции
   */
  lazy val parameters:Params = Params()

  /**
   * Результат вызова функции
   */
  lazy val returns:Type = Type.VOID

  /**
   * Проверяет что для текущего типа-функции (this) возможна операция присвоения типа функции t
   * @param t присваемый тип данных
   * @return true - операция допускается, false - не допускается
   */
  override def assignable(t: Type)(implicit tracer:AssignableTracer): Boolean = super.assignable(t)

  /**
   * Возвращает сигнатуру функции
   * @return сигнатура функции
   */
  override def toString: String = generics.toString+parameters.toString+":"+returns

  /**
   * Проверяет что параметры функции и параметры типа совпадают с указанной функцией
   * @param f функция
   * @return true - совпадают
   */
  def sameTypes(f:Fun):Boolean = {
    require(f!=null)
    parameters.sameTypes(f.parameters) && generics.sameTypes(f.generics)
  }

  //TODO Заменить на TypeVarFetch
  /**
   * Переменные типа
   */
  lazy val typeVariables: Seq[TypeVariable] = List()
}
