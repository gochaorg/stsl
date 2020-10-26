package xyz.cofe.stsl.types

trait TypeReplace {
  def typeReplace(recipe:(Type)=>Option[Type]):Type
}
