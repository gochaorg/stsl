package xyz.cofe.stsl.types

/**
 * Тип данных - экземпляр переменной параметрезированного типа
 *
 * То есть есть например тип `Either[A,B]` которое определено переменной `t`
 * как - то так:
 *
 * ```
 * t = TObject( GenericParams( AnyVal("A"), AnyVal("B") ), .... )
 * ```
 *
 * Мы хотим задать конкретные значения `A,B`, где `A=String, B=Int` тогда
 *
 * ```
 * inst = GenericInstance( Map("A"->STRING, "B"->INT), t )
 * ```
 *
 *
 * @param recipe правило параметризации
 * @param source исходный параметризированный тип
 * @tparam A Объект реализующий TypeVarReplace
 * @see TypeVarReplace
 */
class GenericInstance[A <: Type with TypeVarReplace[A]](
                                                         val recipe: Map[String, Type],
                                                         val source: A
                                                       )
  extends Type with TypeVarReplace[GenericInstance[A]] with TypeVarFetch {
  override type GENERICS = GenericParams
  val generics = GenericParams()

  require(recipe != null)
  require(source != null)
  recipe.foreach({ case (name, trgt) =>
    if (source.generics.get(name).isEmpty) {
      throw TypeError(s"type variable ${name} not defined in ${source}")
    }
  })

  /**
   * Возвращает сигнатуру типа
   *
   * @return сигнатура
   */
  override def toString: String = {
    val sb = new StringBuilder()
    if (recipe.isEmpty) {
      sb.append(source)
    } else {
      sb.append(source match {
        case n: Named => n.name
        case _ => source.toString
      })
      sb.append("[")
      sb.append(source.generics.map(gp => {
        if (recipe.contains(gp.name)) {
          gp.toString + "=" + recipe(gp.name)
        } else {
          gp.toString
        }
      }).reduce((a, b) => a + "," + b))
      sb.append("]")
    }
    sb.toString()
  }

  override def typeVarReplace(trecipe: TypeVariable => Option[Type])(implicit trace: TypeVarTracer): GenericInstance[A] = {
    require(trecipe != null)
    trace("new GenericInstance")(new GenericInstance(
      recipe.map({ case (key, src) =>
        key -> trace(s"map key $key type $src, src is TypeVariable ?")(
          src match {
            case tv: TypeVariable =>
              trace(s"src is TypeVariable")(
                trace(s"trecipe(src)")(trecipe(tv)).getOrElse(
                  trace("src is TypeVarReplace[_] ?")(
                    src match {
                      case tvr: TypeVarReplace[_] =>
                        trace(s"src is TypeVarReplace[_]")(tvr.typeVarReplace(trecipe).asInstanceOf[Type])
                      case _ => trace(s"src is other")(src)
                    })))
            case _ => trace(s"src other")(
              trace("src is TypeVarReplace[_] ?")(
                src match {
                  case tvr: TypeVarReplace[_] =>
                    trace(s"src is TypeVarReplace[_]")(tvr.typeVarReplace(trecipe).asInstanceOf[Type])
                  case _ =>
                    trace(s"src is other")(src)
                }))
          })
      }),
      trace("source=")(source)
    ))
  }

  /**
   * Извлечение переменных
   *
   * @return список переменных
   */
  override def typeVarFetch(from: List[Any] = List()): List[TypeVarLocator] = {
    recipe.flatMap { case (param, trgt) =>
      trgt match {
        case tv: TypeVariable => List(new TypeVarLocator(tv, param :: this :: from))
        case tvf: TypeVarFetch => tvf.typeVarFetch(param :: this :: from)
        case _ => List()
      }
    }.toList
  }
}

object GenericInstance {
  class Builder(private var recipe: Map[String, Type] = Map()) {
    def build[A <: Type with TypeVarReplace[A]](source: A): GenericInstance[A] = {
      new GenericInstance[A](recipe, source)
    }

    def clear(): Builder = {
      recipe = Map()
      this
    }

    def set(name: String, tip: Type): Builder = {
      require(name != null)
      require(tip != null)
      recipe = recipe + (name -> tip)
      this
    }
  }

  def create(): Builder = new Builder()

  def set(name: String, tip: Type): Builder = {
    require(name != null)
    require(tip != null)
    create().set(name, tip)
  }
}