package xyz.cofe.stsl.types

trait TypeVarReplace[A] {
  def typeVarReplace(recipe:(TypeVariable)=>Option[Type]):A
}
