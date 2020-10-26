package xyz.cofe.stsl.types

case class GenericParams( val params: List[GenericParam]=List() ) {
  require(params!=null)
  params.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"generic parameter ${p._1} duplicate")
  )

  def assignable( genericParams: GenericParams ):Boolean = {
    require(genericParams!=null)
    if( genericParams.params.length!=params.length ){
      false
    }else{
      if( params.length<=0  ){
        false
      }else{
        params.indices.map( pi =>
          params(pi).assignable(genericParams.params(pi))
        ).reduce((a,b)=>a && b)
      }
    }
  }

  override def toString: String = if( params.isEmpty ) "" else "["+params.map(_.toString).reduce((a,b)=>a+","+b)+"]"
}

object GenericParams {
  def apply(params: GenericParam*): GenericParams = new GenericParams(params.toList)
}