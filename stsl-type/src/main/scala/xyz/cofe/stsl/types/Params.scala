package xyz.cofe.stsl.types

case class Params( params:List[Param]=List() ) extends Seq[Param] with TypeVarReplace[Params] {
  require(params!=null)

  params.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"parameter ${p._1} duplicate")
  )

  override def toString: String = if( params.isEmpty ) "()" else "("+params.map(_.toString).reduce((a,b)=>a+","+b)+")"

  def sameTypes(paramz:Params):Boolean = {
    require(paramz!=null)
    if( paramz.params.length!=params.length ){
      false
    }else{
      if(params.isEmpty){
        true
      }else{
        params.indices.map(pi=>
          params(pi).tip.equals(paramz.params(pi).tip)
        ).reduce((a,b)=>a&&b)
      }
    }
  }

  override def length: Int = params.length
  override def iterator: Iterator[Param] = params.iterator
  def apply(index:Int):Param = params(index)

  def apply(paramName:String): Param = {
    require(paramName!=null)
    filter(_.name == paramName).head
  }
  def get(paramName:String):Option[Param] = {
    require(paramName!=null)
    filter(_.name == paramName).headOption
  }

  override def typeVarReplace(recipe: TypeVariable => Option[Type]): Params = {
    require(recipe!=null)
    Params( params.map(p => p.typeVarReplace(recipe)) )
  }
}

object Params {
  def apply(params: Param*): Params = new Params(params.toList)
}
