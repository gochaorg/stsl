package xyz.cofe.sel.types

/**
 * Параметр
 */
trait Param {
  /**
   * Имя параметра
   */
  val name : String

  /**
   * Тип параметра
   */
  val paramType : Type
}

object Param {
  def apply( name1:String, paramType1:Type ): Param = new Param {

    /**
     * Имя параметра
     */
    override val name: String = name1

    /**
     * Тип параметра
     */
    override val paramType: Type = paramType1
  }
}
