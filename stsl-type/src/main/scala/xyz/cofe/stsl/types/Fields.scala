package xyz.cofe.stsl.types

class Fields( val fields:List[Field]=List(), val owner:Type=Type.VOID ) extends Seq[Field] {
  require(fields!=null)
  require(owner!=null)
  fields.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"field name ${p._1} duplicate")
  )

  override def length: Int = fields.length
  override def iterator: Iterator[Field] = fields.iterator
  def apply(idx:Int):Field = fields(idx)
  def apply(fieldName:String):Field = {
    require(fieldName!=null)
    filter(_.name == fieldName).head
  }
  def get(fieldName:String):Option[Field] = {
    require(fieldName!=null)
    filter(_.name == fieldName).headOption
  }
}

object Fields {
  def apply(fields: List[Field], owner: Type=Type.VOID): Fields = new Fields(fields, owner)
  def apply(fields: (String,Type)*): Fields = new Fields(fields.map(f=>Field(f._1,f._2)).toList)
}
