package xyz.cofe.stsl.types

class MutableMethods( private var functions:Map[String,Funs]=Map() ) extends Methods() with Freezing {
  require(functions!=null)

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue

  def freeze:Unit = {
    freezedValue = true
    funs.values.filter(_.isInstanceOf[Freezing]).foreach(_.asInstanceOf[Freezing].freeze)
  }

  override def funs: Map[String, Funs] = functions

  def append( name:String, fun:Fun ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(name!=null)
    require(fun!=null)

    val funz : Funs = functions.getOrElse(name, new MutableFuns())
    val mfunz : MutableFuns = funz match {
      case m:MutableFuns => m
      case _ =>
        val mf = new MutableFuns( funz.funs )
        mf
    }

    mfunz.append(fun)
    functions = functions + (name -> mfunz)
  }

  def += ( method:(String,Fun) ):Unit = {
    require(method!=null)
    append(method._1, method._2)
  }

  def += ( methods:(String,Fun)* ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(methods!=null)
    methods.foreach( m => append(m._1, m._2) )
  }

  def filter( filter:(String,Fun)=>Boolean ):Unit = {
    if( freezed )throw TypeError("freezed")
    functions = functions.map({case(name,funz)=>
      val mfunz : MutableFuns = funz match {
        case m:MutableFuns => m
        case _ =>
          val mf = new MutableFuns( funz.funs )
          mf
      }
      mfunz.filter(filter(name,_))
      name -> mfunz
    })
    functions = functions.filter({case(name,funz)=>funz.funs.nonEmpty})
  }
}
