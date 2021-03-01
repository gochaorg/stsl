package xyz.cofe.stsl.types

class TypeVarReplacer {
  private var rmap:Map[String,Type] = Map()
  def build():TypeVariable=>Option[Type] = {
    (tv:TypeVariable)=>rmap.get(tv.name)
  }
  def set(name:String, tip:Type):TypeVarReplacer = {
    require(name!=null)
    require(tip!=null)
    rmap = rmap + (name -> tip)
    this
  }
}
