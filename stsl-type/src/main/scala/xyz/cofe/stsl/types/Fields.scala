package xyz.cofe.stsl.types

case class Fields( val fields:List[Field]=List(), val owner:Type=Type.VOID ) {
  require(fields!=null)
  require(owner!=null)
  fields.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"field name ${p._1} duplicate")
  )
}
