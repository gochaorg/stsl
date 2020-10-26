package xyz.cofe.stsl.types

case class Params( params:List[Param]=List() ) {
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
}

object Params {
  def apply(params: Param*): Params = new Params(params.toList)
}
