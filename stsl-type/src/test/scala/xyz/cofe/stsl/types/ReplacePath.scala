package xyz.cofe.stsl.types

class ReplacePath( val items:Seq[Any] ) extends Seq[Any] {
  require(items!=null)
  override def length: Int = items.length
  override def apply(idx: Int): Any = items(idx)
  override def iterator: Iterator[Any] = items.iterator
  def append(ch:Any):ReplacePath = {
    require(ch!=null)
    new ReplacePath(items ++ List(ch))
  }
}

object ReplacePath {
  def apply(item:Any): ReplacePath = {
    require(item!=null)
    new ReplacePath(List(item))
  }
}