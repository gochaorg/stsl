package xyz.cofe.stsl.types

case class Params( params:List[Param]=List() ) {
  params.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"parameter ${p._1} duplicate")
  )

  override def toString: String = if( params.isEmpty ) "()" else "("+params.map(_.toString).reduce((a,b)=>a+","+b)+")"
}

object Params {
  def apply(params: Param*): Params = new Params(params.toList)
}
