package xyz.cofe.stsl.types

trait TypeVarReplace[A] {
  def typeVarReplace(recipe:(TypeVariable)=>Option[Type]):A
  def typeVarReplace(recipe:(String,Type)*):A = {
    val rmap:Map[String,Type] = recipe.toMap
    typeVarReplace((tv:TypeVariable)=>rmap.get(tv.name))
  }
  object typeVarBake {
    def fn(recipe:(String,Type)*):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe.toMap
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.FN ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }
    def thiz(recipe:Map[String,Type]):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.THIS ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }
    def thiz(recipe:(String,Type)*):A = {
      require(recipe!=null)
      val rmap:Map[String,Type] = recipe.toMap
      typeVarReplace((tv:TypeVariable)=>{
        if( tv.owner==Type.THIS ) {
          rmap.get(tv.name)
        }else{
          None
        }
      })
    }
  }
}
