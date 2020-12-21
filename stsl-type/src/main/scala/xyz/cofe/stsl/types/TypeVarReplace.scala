package xyz.cofe.stsl.types

/**
 * Замена переменной Типа.
 *
 * <p>
 *   Актуально когда есть параметризированный тип,
 *   например List с параметром A.
 *
 * <pre>
 *   List[A] {
 *     add( item : A )
 *   }
 * </pre>
 *
 * Для правильной проверке типов и соответ вывода типов, необходима процедура
 * замены переменных на реальные значения.
 *
 * <p>
 * Для примера возьмем тип User:
 *
 * <pre>
 * User {
 *   name : String
 *   enabled : Boolean
 * }
 * </pre>
 *
 * Создадим переменную типа List, которая может создержать только элементы типа User.
 * Для java кода это так: <code>var userList = new List&lt;User&gt;</code>
 *
 * <p>
 * В нашем случае это такой код:
 * <code>val userList = new GenericInstance( Map("A"->userType), listType )</code>
 *
 * <p>
 * Тогда при вызове <code>userList.add( something )</code>
 *
 * будет выведен новый тип для userList:
 * <pre>
 *   List {
 *     add( item: User )
 *   }
 * </pre>
 *
 * Этот вывод новго типа осуществляется за счет TypeVarReplace.
 *
 * @tparam A Объект реализующий TypeVarReplace
 */
trait TypeVarReplace[A] {
  /**
   * Замена переменных
   * @param recipe правило замены
   * @return новый тип
   */
  def typeVarReplace(recipe:(TypeVariable)=>Option[Type]):A

  /**
   * Замена переменных
   * @param recipe правило замены
   * @return новый тип
   */
  def typeVarReplace(recipe:(String,Type)*):A = {
    val rmap:Map[String,Type] = recipe.toMap
    typeVarReplace((tv:TypeVariable)=>rmap.get(tv.name))
  }

  /**
   * Замена переменных
   */
  object typeVarBake {

    /**
     * Замена переменых чей владелец (owner) FN
     * @param recipe правило замены
     * @return новый тип
     */
    def fn(recipe:(String,Type)*):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe.toMap
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.FN ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }

    /**
     * Замена переменых чей владелец (owner) THIS
     * @param recipe правило замены
     * @return новый тип
     */
    def thiz(recipe:Map[String,Type]):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.THIS ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }

    /**
     * Замена переменых чей владелец (owner) THIS
     * @param recipe правило замены
     * @return новый тип
     */
    def thiz(recipe:(String,Type)*):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe.toMap
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.THIS ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }
  }
}
