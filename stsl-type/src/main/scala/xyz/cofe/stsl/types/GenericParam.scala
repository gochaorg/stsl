package xyz.cofe.stsl.types

/**
 * Тип параметра - модели ковариантности
 */
sealed trait GenericParam extends Type with Named {
  override type GENERICS = GenericParams
  val generics: GenericParams = GenericParams()

  override lazy val extend: Option[Type] = None
  def sameType(t:GenericParam):Boolean
}

/**
 * Тип переменная {@link TypeVariable} может указывать на любой тип.
 * @param name имя переменной
 */
case class AnyVariant(name:String) extends GenericParam {
  require(name!=null)
  override def assignable(t: Type)(implicit tracer:AssignableTracer): Boolean = {
    require(t!=null)
    tracer("AnyVariant",this,t)(true)
  }
  override def toString: String = s"${name}"
  override def sameType(t: GenericParam): Boolean = {
    require(t!=null)
    t match {
      case _:AnyVariant => true
      case _ => false
    }
  }
}

/**
 * Тип переменная {@link TypeVariable} указывает на ковариантный тип:
 * <i style="color: #4a1a9e">selfType</i>.
 * <i>assignable</i>(
 * <i style="color: #9e1953">someType</i>
 * ).
 * @param name имя переменной
 * @param tip тип переменной
 */
case class CoVariant(name:String, tip:Type) extends GenericParam {
  require(name!=null)
  require(tip!=null)
  override def assignable(t: Type)(implicit tracer:AssignableTracer): Boolean = {
    require(t!=null)
    tracer("CoVariant",this,t){
    t match {
      case cov:CoVariant => tip.assignable(cov.tip)
      case _:ContraVariant => false
      case _:AnyVariant => false
      case _ => tip.assignable(t)
    }}
  }
  override def toString: String = s"${name}:${tip}+"
  override def sameType(t: GenericParam): Boolean = {
    require(t!=null)
    t match {
      case c:CoVariant => tip == c.tip
      case _ => false
    }
  }
}

/**
 * Тип переменная {@link TypeVariable} указывает на контрвариантный тип:
 * <i style="color: #9e1953">someType</i>.
 * <i>assignable</i>(
 *  <i style="color: #4a1a9e">selfType</i>
 * ).
 * @param name имя переменной
 * @param tip тип переменной
 */
case class ContraVariant(name:String, tip:Type) extends GenericParam {
  require(name!=null)
  require(tip!=null)
  override def assignable(t: Type)(implicit tracer:AssignableTracer): Boolean = {
    require(t!=null)
    tracer("ContraVariant",this,t)(
    t match {
      case ctr:ContraVariant => ctr.tip.assignable(tip)
      case _:CoVariant => false
      case _:AnyVariant => false
      case _ => tip.assignable(t)
    })
  }
  override def sameType(t: GenericParam): Boolean = {
    require(t!=null)
    t match {
      case c:ContraVariant => tip == c.tip
      case _ => false
    }
  }
  override def toString: String = s"${name}:${tip}-"
}