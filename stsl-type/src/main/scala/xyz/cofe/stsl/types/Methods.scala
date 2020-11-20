package xyz.cofe.stsl.types

/**
 * Методы объекта
 * @param functions карта методов
 * @param owner владец методов, по умолчанию VOID
 */
class Methods( private val functions:Map[String,Funs]=Map()
             , val owner:Type=Type.VOID
             ) extends Seq[(String,Fun)] {
  require(functions!=null)
  require(owner!=null)
  functions.foreach({case(name,funs)=>
    require(name!=null, "method name is null")
    require(funs!=null, s"method $name => null")
    require(funs.nonEmpty, s"method $name => empty")
  })

  def funs:Map[String,Funs]=functions

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
