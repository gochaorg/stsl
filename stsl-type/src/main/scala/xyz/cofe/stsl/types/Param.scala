package xyz.cofe.stsl.types

case class Param(name:String, tip: Type) extends Named with TypeVarReplace[Param] {
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
