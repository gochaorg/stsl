package xyz.cofe.sel.types

/**
 * Подстановочный тип
 * @param name имя подстановочного типа
 * @param owner владелец подстановочнго
 */
class GenericPlaceholder(val name:String, val owner:Type) extends Type with TypeReplace {
  require(name!=null)
  require(owner!=null)

  /**
   * Расширяет тип
   */
  override val extend: Option[Type] = None

  override def typeReplace(replacement: Type => Option[Type]): Type = {
    require(replacement!=null)
    GenericPlaceholder(name,
      owner match {
        //case tr:TypeReplace => tr.typeReplace(replacement)
        case _ => replacement(owner).getOrElse(owner)
      }
    )
  }
}

object GenericPlaceholder {
  def apply(name: String, owner: Type): GenericPlaceholder = new GenericPlaceholder(name, owner)
}
