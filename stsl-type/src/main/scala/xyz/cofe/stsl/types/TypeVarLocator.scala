package xyz.cofe.stsl.types

/**
 * Описывает расположение переменной
 * @param typeVar Переменная
 * @param path Путь
 */
class TypeVarLocator(
  val typeVar : TypeVariable,
  val path : List[Any] = List()
) {
  override def toString: String = {
    val sb = new StringBuilder
    sb.append("TVarLocator[")
    path.reverse.zipWithIndex.foreach { case(pe,pi) =>
      if(pi>0)sb.append("|")
      pe match {
        case prm: Param => sb.append(s"param ${prm.name}:${prm.tip}")
        case fn: Fun => sb.append(s"fn $fn")
        case gi: GenericInstance[_] => sb.append(s"gi $gi")
        case _ => sb.append(pe)
      }
    }
    sb.append("]")
    sb.toString()
  }

  def resolve(from:Type):Option[Type] = {
    require(from!=null)
    var frm = from
    var pth = path.reverse
    var stop = false
    var result : Option[Type] = None
    while( !stop ){
      if( path.isEmpty ){
        stop = true
      }else{
        frm match {
          case f0:Fun =>
            pth.head match {
              case f1:Fun if pth.size>1 && pth(1).isInstanceOf[Param] =>
                val prm = pth(1).asInstanceOf[Param]
                val f0Prm = f0.parameters.get(prm.name)
                if( f0Prm.isDefined ){
                  frm = f0Prm.get.tip //pth(1).asInstanceOf[Param].tip
                  pth = pth.drop(2)
                }else{
                  stop = true
                }
              case f2:Fun if pth.size>1 && pth(1).isInstanceOf[String] && pth(1).toString.startsWith("return") =>
                frm = f0.returns
                pth = pth.drop(2)
              case _ =>
                stop = true
            }
          case gi0: GenericInstance[_] =>
            pth.head match {
              case gi:GenericInstance[_] if pth(1).isInstanceOf[String] =>
                val rcpName = pth(1).asInstanceOf[String]
                if( gi0.recipe.contains(rcpName) ){
                  frm = gi0.recipe(rcpName)
                  pth = pth.drop(2)
                }else{
                  stop = true
                }
              case _ =>
                stop = true
            }
          case _ =>
            stop = true
        }
      }
    }
    if( pth.isEmpty && frm!=null ){
      Some(frm)
    }else{
      None
    }
  }
}
