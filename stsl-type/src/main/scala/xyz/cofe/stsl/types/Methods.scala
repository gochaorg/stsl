package xyz.cofe.stsl.types

case class Methods( val funs:Map[String,Funs]=Map(), val owner:Type=Type.VOID ) {
  require(funs!=null)
  require(owner!=null)
}
