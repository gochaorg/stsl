package xyz.cofe.stsl.types

/**
 * Сводная информация по всем методам включая унаследованные
 */
trait InheritedMethods extends MethodsProperty {
  self =>
  
  /**
   * кеш вычисленных значений
   */
  private var inheritedMethodsCache:Option[List[Methods]] = None
  
  /**
   * Унаследованные методы в порядке наследования
   * @return методы
   */
  def inheritedMethods:List[Methods] = {
    if (inheritedMethodsCache.isDefined && self.isInstanceOf[Freezing] && self.asInstanceOf[Freezing].freezed) {
      inheritedMethodsCache.get
    } else {
      val computed = self match {
        case ext: Extendable =>
          ext.extendPath.map {
            case ownType@(fprop: MethodsProperty) =>
              Some(new Methods(fprop.methods.funs, ownType))
            case _ =>
              None
          }.filter(_.isDefined).map(_.get)
        case _ => List(self.methods)
      }
      inheritedMethodsCache = Some(computed)
      computed
    }
  }
  
  private var publicMethodsCache:Option[Methods] = None
  def publicMethods:Methods = {
    if( publicMethodsCache.isDefined && self.isInstanceOf[Freezing] && self.asInstanceOf[Freezing].freezed ) {
      publicMethodsCache.get
    } else {
      val computed = {
        val methods = new MutableMethods()
        inheritedMethods.foreach(srcMethods => {
          srcMethods.foreach { case (name, fn) => methods.append(name, fn) }
        })
        methods.asInstanceOf[Methods]
      }
      publicMethodsCache = Some(computed)
      computed
    }
  }
}
