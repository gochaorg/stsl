package xyz.cofe.stsl.types

/**
 * Параметры метода/функции
 * @param params список параметров
 */
class Params( val params:List[Param]=List() ) extends Seq[Param] with TypeVarReplace[Params] with TypeVarFetch {
  require(params!=null)

  params.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"parameter ${p._1} duplicate")
  )

  override def toString: String = if( params.isEmpty ) "()" else "("+params.map(_.toString).reduce((a,b)=>a+","+b)+")"

  def sameTypes(paramz:Params):Boolean = {
    require(paramz!=null)
    if( paramz.params.length!=params.length ){
      false
    }else{
      if(params.isEmpty){
        true
      }else{
        params.indices.map(pi=>
          params(pi).tip.equals(paramz.params(pi).tip)
        ).reduce((a,b)=>a&&b)
      }
    }
  }

  override def length: Int = params.length
  override def iterator: Iterator[Param] = params.iterator
  def apply(index:Int):Param = params(index)

  /**
   * Получение параметра по имени
   * @param paramName имя параметра
   * @return Параметр
   */
  def apply(paramName:String): Param = {
    require(paramName!=null)
    filter(_.name == paramName).head
  }

  /**
   * Получение параметра по имени
   * @param paramName имя параметра
   * @return Параметр
   */
  def get(paramName:String):Option[Param] = {
    require(paramName!=null)
    filter(_.name == paramName).headOption
  }

  override def typeVarReplace(recipe: TypeVariable => Option[Type]): Params = {
    require(recipe!=null)
    Params( params.map(p => p.typeVarReplace(recipe)) )
  }

  /**
   * Извлечение переменных
   * @return список переменных
   */
  override def typeVarFetch(from:List[Any] = List()): List[TypeVarLocator] = {
    val self = this
    params.flatMap { param =>
      param.tip match {
        case tv:TypeVariable => List(new TypeVarLocator(tv,param::self::from))
        case tvf:TypeVarFetch => tvf.typeVarFetch(param::self::from)
        case _ => List()
      }
    }
  }
}

object Params {
  def apply(params: (String,Type)*): Params = new Params(params.map(p=>Param(p._1, p._2)).toList)
  def apply(params: List[Param]): Params = new Params(params)

  def empty(): Params = new Params()

  class Builder( private val first:Param ) {
    private var params : List[Param] = List(first)
    def add( name:String, tip:Type ):Builder = {
      require(name!=null)
      require(tip!=null)
      params = params ++ List( Param(name,tip) )
      this
    }
    def build():Params = {
      Params(params)
    }
  }

  def create(name:String, tip:Type):Builder = {
    require(name!=null)
    require(tip!=null)
    new Builder(Param(name,tip))
  }
}
