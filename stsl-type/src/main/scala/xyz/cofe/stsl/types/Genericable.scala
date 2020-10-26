package xyz.cofe.stsl.types

trait Genericable extends Assignable {
  lazy val generics : GenericParams = GenericParams()
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    val gAsgn = generics.assignable(t.generics)
    gAsgn && super.assignable(t)
  }
}
