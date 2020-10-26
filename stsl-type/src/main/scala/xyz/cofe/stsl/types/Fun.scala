package xyz.cofe.stsl.types

trait Fun extends Type {
  lazy val parameters:Params = Params()
  lazy val returns:Type = Type.VOID
  override def assignable(t: Type): Boolean = super.assignable(t)
  override def toString: String = generics.toString+parameters.toString+":"+returns
  def sameTypes(f:Fun):Boolean = {
    require(f!=null)
    parameters.sameTypes(f.parameters) && generics.sameTypes(f.generics)
  }
}
