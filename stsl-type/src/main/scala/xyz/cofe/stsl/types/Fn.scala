package xyz.cofe.stsl.types

case class Fn( fgParams: GenericParams
             , fParams: Params
             , fReturn: Type
             ) extends Fun {
  require(fgParams!=null)
  require(fParams!=null)
  require(fReturn!=null)
  override lazy val generics: GenericParams = fgParams
  override lazy val parameters: Params = fParams
  override lazy val returns: Type = fReturn
}

object Fn {
  def apply(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = new Fn(fgParams, fParams, fReturn)
  def apply(fParams: Params, fReturn: Type): Fn = new Fn(GenericParams(), fParams, fReturn)
}
