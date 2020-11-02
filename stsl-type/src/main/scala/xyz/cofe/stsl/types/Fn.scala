package xyz.cofe.stsl.types

case class Fn( fgParams: GenericParams
             , fParams: Params
             , fReturn: Type
             ) extends Fun {
  require(fgParams!=null)
  require(fParams!=null)
  require(fReturn!=null)

  // Переменные типа обявленные для owner = FN, должны указывать на  GenericParams
  private val inputGenericVariables =
    fParams.params.map( p => p.tip match {
      case gv:GenericVariable => gv
      case _ => null
    }).filter( _ != null )

  private val outGenericVariable =
    fReturn match {
      case gv:GenericVariable => Some(gv)
      case _ => None
    }

  private val genericVariables = inputGenericVariables ++ outGenericVariable

  genericVariables
    .filter( gv => gv.owner == Type.FN )
    .map( _.name )
    .foreach( vname =>
      if( !fgParams.params.map(_.name).contains(vname) ){
        throw TypeError(s"bind undeclared generic variable $vname into Fn")
      }
    )

  override lazy val generics: GenericParams = fgParams
  override lazy val parameters: Params = fParams
  override lazy val returns: Type = fReturn
}

object Fn {
  def apply(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = new Fn(fgParams, fParams, fReturn)
  def apply(fParams: Params, fReturn: Type): Fn = new Fn(GenericParams(), fParams, fReturn)
}
