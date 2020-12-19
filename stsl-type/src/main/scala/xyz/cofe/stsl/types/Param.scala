package xyz.cofe.stsl.types

class Param(val name:String, val tip: Type) extends Named with TypeVarReplace[Param] {
  require(name!=null)
  require(tip!=null)
  override def toString: String = s"${name}:${tip}"
  override def typeVarReplace(recipe: TypeVariable => Option[Type]): Param = {
    require(recipe!=null)
    tip match {
      case tv:TypeVariable =>Param(name, recipe(tv).getOrElse(
        tv match {
          case tv2:TypeVarReplace[Type] => tv2.typeVarReplace(recipe)
          case _ => tv
        }
      ))
      case _ => Param(name, tip match {
        case tv2:TypeVarReplace[Type] => tv2.typeVarReplace(recipe)
        case _ => tip
      })
    }
    //this
  }
}

object Param {
  def apply(name: String, tip: Type): Param = new Param(name, tip)
}
