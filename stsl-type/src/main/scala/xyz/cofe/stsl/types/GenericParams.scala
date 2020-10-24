package xyz.cofe.stsl.types

case class GenericParams( val params: List[GenericParam]=List() ) {
  require(params!=null)
  params.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"generic parameter ${p._1} duplicate")
  )
}
