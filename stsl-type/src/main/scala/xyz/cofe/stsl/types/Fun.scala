package xyz.cofe.stsl.types

trait Fun extends Type {
  lazy val parameters:Params = Params()
  lazy val returns:Type = Type.VOID
  override def assignable(t: Type): Boolean = super.assignable(t)
  override def toString: String = generics.toString+parameters.toString+":"+returns
}
