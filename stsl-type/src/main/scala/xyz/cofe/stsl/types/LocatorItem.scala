package xyz.cofe.stsl.types

/**
 * Компонент пути локатора вложенного типа {@link TypeVarLocator}
 */
sealed trait LocatorItem {
  /**
   * Следующий компонент
   */
  val next:Option[LocatorItem]

  //region toList/asList

  def toList:List[LocatorItem] = {
    var lst:List[LocatorItem] = List(this)
    while( lst.head.next.isDefined ){
      lst = lst.head.next.get :: lst
    }
    lst.reverse
  }
  
  lazy val asList:List[LocatorItem] = toList
  //endregion
}

/**
 * Компонент указывает на GenericInstance
 * @param gi GenericInstance
 * @param param Параметр
 * @param next Следующий компонент
 */
case class LocatorItemGenericInstance[A <: xyz.cofe.stsl.types.Type with xyz.cofe.stsl.types.TypeVarReplace[A]]
  ( gi:GenericInstance[A]
  , param:String
  , next:Option[LocatorItem]
  ) extends LocatorItem

/**
 * Компонент указывает на параметр функции
 * @param fun Функции
 * @param param Параметр
 * @param next Следующий компонент
 */
case class LocatorItemFunParam
( fun:Fun
  , param:Param
  , next:Option[LocatorItem]
) extends LocatorItem {
  def resolveNext( from:Type, args:Seq[Type] ):Option[Type] = {
    require(from!=null)
    require(args!=null)
    val pi = fun.parameters.zipWithIndex.filter( {case(p,i) => p.name==param.name} ).map({case(p,i)=>i}).head
    from match {
      case f:Fun =>
        Some(args(pi))
      case _ => None
    }
  }
}

/**
 * Компонент указывает на результат функции
 * @param fun Функции
 * @param next Следующий компонент
 */
case class LocatorItemFunResult
( fun:Fun
  , next:Option[LocatorItem]
) extends LocatorItem

object LocatorItem {
  def genericInstance[A <: xyz.cofe.stsl.types.Type with xyz.cofe.stsl.types.TypeVarReplace[A]]
  ( gi:GenericInstance[A], param:String, next:Option[LocatorItem] = None ):LocatorItemGenericInstance[A] = {
    require(gi!=null)
    require(param!=null)
    require(next!=null)
    LocatorItemGenericInstance( gi, param, next )
  }

  def funParam( fn:Fun, param:Param, next:Option[LocatorItem] = None ):LocatorItemFunParam = {
    require(fn!=null)
    require(param!=null)
    require(next!=null)
    LocatorItemFunParam(fn,param,next)
  }

  def funResult( fn:Fun, next:Option[LocatorItem] = None ):LocatorItemFunResult = {
    require(fn!=null)
    require(next!=null)
    LocatorItemFunResult(fn,next)
  }

  def parse(path:List[Any]):Option[LocatorItem] = {
    require(path!=null)
    var rslt : LocatorItem = null
    var stop = false
    var pth = path
    while( !stop ){
      if( pth.isEmpty ){
        stop = true
      }else{
        if( pth.length>1 ){
          val trgt = pth.head
          val base = pth(1)
          base match {
            case gi:GenericInstance[_] =>
              trgt match {
                case param: String =>
                  pth = pth.drop(2)
                  rslt = genericInstance(gi,param,Option(rslt))
                case _ =>
                  stop = true
              }
            case fn: Fun =>
              trgt match {
                case prm: Param =>
                  pth = pth.drop(2)
                  rslt = funParam(fn,prm,Option(rslt))
                case "returns" =>
                  pth = pth.drop(2)
                  rslt = funResult(fn,Option(rslt))
                case _ =>
                  stop = true
              }
            case _ =>
              stop = true
          }
        }
      }
    }
    Option(rslt)
  }
}