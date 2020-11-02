package xyz.cofe.stsl.types

case class GenericParams( val params: List[GenericParam]=List() ) extends Seq[GenericParam] {
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
        true
      }else{
        params.indices.map( pi =>
          params(pi).assignable(genericParams.params(pi))
        ).reduce((a,b)=>a && b)
      }
    }
  }
  def sameTypes(paramz:GenericParams):Boolean = {
    require(paramz!=null)
    if( paramz.params.length!=params.length ){
      false
    }else{
      if(params.isEmpty){
        true
      }else{
        params.indices.map(pi=> {
          val gp1:GenericParam = params(pi)
          val gp2:GenericParam = paramz.params(pi)
          gp1.sameType(gp2)
        }).reduce((a,b)=>a&&b)
      }
    }
  }

  override def toString: String = if( params.isEmpty ) "" else "["+params.map(_.toString).reduce((a,b)=>a+","+b)+"]"

  override def length: Int = params.length
  override def iterator: Iterator[GenericParam] = params.iterator
  override def apply(idx: Int): GenericParam = params(idx)

  def apply(paramName:String):GenericParam = {
    require(paramName!=null)
    filter(_.name == paramName).head
  }

  def get(paramName:String):Option[GenericParam] = {
    require(paramName!=null)
    filter(_.name == paramName).headOption
  }
}

object GenericParams {
  def apply(params: GenericParam*): GenericParams = new GenericParams(params.toList)
}