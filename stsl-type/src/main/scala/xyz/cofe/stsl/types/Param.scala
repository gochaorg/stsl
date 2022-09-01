package xyz.cofe.stsl.types

/**
 * Параметр метода / функции
 *
 * @param name имя функции
 * @param tip  тип функции
 */
class Param(val name: String, val tip: Type) extends Named with TypeVarReplace[Param] {
  require(name != null)
  require(tip != null)

  override def toString: String = s"${name}:${tip}"

  override def typeVarReplace(recipe: TypeVariable => Option[Type])(implicit trace: TypeVarTracer): Param = {
    require(recipe != null)
    trace(s"param $name : $tip")(
      tip match {
        case tv: TypeVariable =>
          trace(s"$tip is TypeVariable")(
            Param(name, recipe(tv).getOrElse(
              tv match {
                case tv2: TypeVarReplace[Type] =>
                  trace(s"$tv is TypeVarReplace")(tv2.typeVarReplace(recipe))
                case _ =>
                  trace(s"$tv is not TypeVarReplace")(tv)
              }
            )))
        case _ =>
          trace(s"$tip is not TypeVariable")(
            Param(name, tip match {
              case tv2: TypeVarReplace[Type] =>
                trace(s"$tv2 is TypeVarReplace")(tv2.typeVarReplace(recipe))
              case _ =>
                trace(s"$tip is not TypeVarReplace")(tip)
            }))
      })
  }
}

object Param {
  def apply(name: String, tip: Type): Param = new Param(name, tip)
}
