package xyz.cofe.sel.types

trait TypeReplace {
  def typeReplace(replacement:Type=>Option[Type]):Type
}
