package xyz.cofe.stsl.types

class MutableFuns(
                   private var functions1: List[Fun] = List()
                 ) extends Funs(List()) with Freezing {
  require(functions1!=null)

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue
  def freeze:Unit = {
    freezedValue = true
  }

  private var functions : List[Fun] = List()
  override def funs: List[Fun] = functions

  def append( fun:Fun ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(fun!=null)
    functions = functions.filter( f => !f.sameTypes(fun) ) ++ List(fun)
  }

  functions1.filter( f => f!=null ).foreach( append )

  def += ( fun:Fun ):MutableFuns = {
    append(fun)
    this
  }

  def += ( funs:Seq[Fun] ):MutableFuns = {
    require(funs!=null)
    funs.foreach(append)
    this
  }

  override def filter(filter:(Fun)=>Boolean ):MutableFuns = {
    require(filter!=null)
    if( freezed )throw new IllegalStateException("function freezed")
    functions = functions.filter( filter )
    this
  }
}
