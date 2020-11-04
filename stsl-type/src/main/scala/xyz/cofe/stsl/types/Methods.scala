package xyz.cofe.stsl.types

case class Methods( val funs:Map[String,Funs]=Map()
                  , val owner:Type=Type.VOID
                  ) extends Seq[(String,Fun)] {
  require(funs!=null)
  require(owner!=null)
  funs.foreach({case(name,funs)=>
    require(name!=null, "method name is null")
    require(funs!=null, s"method $name => null")
    require(funs.nonEmpty, s"method $name => empty")
  })

  private lazy val flattenFuns : List[(String,Fun)] = funs.map({ case (name, funs) => funs.map( f => name -> f )}).flatten.toList
  override def length: Int = flattenFuns.length
  override def iterator: Iterator[(String, Fun)] = flattenFuns.iterator
  override def apply(idx: Int): (String, Fun) = flattenFuns(idx)

  def apply(name:String):Funs = {
    require(name!=null)
    funs(name)
  }
  def get(name:String):Option[Funs] = {
    require(name!=null)
    funs.get(name)
  }
}
