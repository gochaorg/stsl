package xyz.cofe.stsl.types

import java.util

/**
 * Функция.
 * <p>
 * Для создания вызываемой функции используйте invoke / invoking
 *
 * <p>
 * Пример создания функции
 * <ul>
 * <li> <code> Fn(Params("a" -> INT),INT) </code>
 * <ul>
 * <li> Создает функцию с одним аргментом
 * <li> имя аргумента <b>a</b>,
 * <li> тип аргмента <b>INT</b>
 * <li> возвращает тип <b>INT<b>
 * </ul>
 * <li> <code>
 * Fn( <br>
 * &nbsp;  GenericParams( <br>
 * &nbsp; &nbsp;    AnyVariant("A"), AnyVariant("B") ), <br>
 * &nbsp;  Params( <br>
 * &nbsp; &nbsp;    "a" -> TypeVariable("A", Type.FN)
 * ), <br>
 * &nbsp;  TypeVariable("B", Type.FN) <br>
 * )
 * <code>
 * <ul>
 * <li> Создает функцию с типа параметрами (GenericParams) A, B
 * <li> Один аргумент, с именем <b>a</b> и типом переменной <b>A</b>
 * <li> Тип результата - тип переменная <b>B</b>
 * <li> сигнатура (toString()) = <code>[A,B](a:A):B</code>
 * </ul>
 * </ul>
 *
 * @param fgParams Определение переменных типа - функции
 * @param fParams  Параметры функции
 * @param fReturn  Результат функции
 */
class Fn(val generics: GenericParams
         , fParams: Params
         , fReturn: Type
        ) extends Fun {
  override type GENERICS = GenericParams

  require(generics != null)
  require(fParams != null)
  require(fReturn != null)

  override def assignable(t: Type)(implicit tracer: AssignableTracer): Boolean = {
    require(t != null)
    tracer("Fn", this, t)(
      if (!t.isInstanceOf[Fun]) {
        tracer(s"${t} is not instance of Fun")(false)
      } else {
        val fn = t.asInstanceOf[Fun]
        val genericAssignable: Boolean = tracer("fn generic params")(generics.assignable(fn.generics))
        val paramsCountMatched: Boolean = tracer("fn params count")(parameters.length == fn.parameters.length)
        val paramsAssignable: Boolean = paramsCountMatched match {
          case true =>
            if (parameters.length == 0) {
              tracer("receiver parameters list is empty")(true)
            } else {
              parameters.indices.map(pi => {
                val toParam = fn.parameters(pi)
                val fromParam = parameters(pi)
                tracer(s"does param [$pi] $toParam is assignable from $fromParam ?")(toParam.tip.assignable(fromParam.tip))
              }).reduce((a, b) => a && b)
            }
          case false =>
            false
        }
        val returnAssignable = tracer(
          s"does receiver return type $returns is assignable ${fn.returns}"
        )(returns.assignable(fn.returns))
        genericAssignable && paramsCountMatched && paramsAssignable && returnAssignable
      })
  }

  // Переменные типа объявленные для owner = FN, должны указывать на  GenericParams
  lazy val inputTypeVariables: Seq[TypeVariable] =
    fParams.params.map(p => p.tip match {
      case fn: Fn => fn.typeVariables
      case gv: TypeVariable => List(gv)
      case _ => List()
    }).filter(_ != null).flatten

  lazy val outTypeVariable: Seq[TypeVariable] =
    fReturn match {
      case fn: Fn => fn.typeVariables
      case gv: TypeVariable => List(gv)
      case _ => List()
    }

  private def inputTypeVarFetch(from: List[Any] = List()): List[TypeVarLocator] = fParams.params.flatMap { p =>
    p.tip match {
      case tv: TypeVariable => List(new TypeVarLocator(tv, p :: this :: from))
      case tvf: TypeVarFetch => tvf.typeVarFetch(p :: this :: from)
      case _ => List()
    }
  }

  private def outputTypeVarFetch(from: List[Any] = List()): List[TypeVarLocator] = fReturn match {
    case tv: TypeVariable => List(new TypeVarLocator(tv, "returns" :: this :: from))
    case tvf: TypeVarFetch => tvf.typeVarFetch("returns" :: this :: from)
    case _ => List()
  }

  private def inoutTypeVarFetch(from: List[Any] = List()): List[TypeVarLocator] = inputTypeVarFetch(from) ++ outputTypeVarFetch(from)

  /**
   * Извлечение переменных
   *
   * @return список переменных
   */
  override def typeVarFetch(from: List[Any] = List()): List[TypeVarLocator] = inoutTypeVarFetch(from)

  /**
   * Экземепляры переменных типа
   */
  override lazy val typeVariables: Seq[TypeVariable] = inputTypeVariables ++ outTypeVariable

  typeVariables
    .filter(gv => gv.owner == Type.FN)
    .map(_.name)
    .foreach(vname =>
      if (!generics.params.map(_.name).contains(vname)) {
        throw TypeError(s"bind undeclared type variable $vname into Fn")
      }
    )

  /**
   * Список параметров типа
   */
  //override lazy val generics: GenericParams = fgParams

  /**
   * Параметры функции
   */
  override lazy val parameters: Params = fParams

  /**
   * Результат вызова функции
   */
  override lazy val returns: Type = fReturn

  /**
   * Замена переменных
   *
   * @param recipe правило замены
   * @return новый тип
   */
  override def typeVarReplace(recipe: TypeVariable => Option[Type])(implicit trace: TypeVarTracer): Fun = {
    val ret: Type = trace("returns")(returns match {
      case tv: TypeVariable => recipe(tv).getOrElse(
        tv match {
          case tvr: TypeVarReplace[_] => tvr.typeVarReplace(recipe).asInstanceOf[Type]
          case _ => tv
        })
      case _ => returns match {
        case tvr: TypeVarReplace[_] => tvr.typeVarReplace(recipe).asInstanceOf[Type]
        case _ => returns
      }
    })

    val paramz = trace("parameters")(Params(parameters.params.map(p => p.typeVarReplace(recipe))))

    var genericReplacements: List[(TypeVariable, Type)] =
      returns match {
        case retTv: TypeVariable => List((retTv -> ret))
        case _ => List()
      }

    paramz.indices.foreach(pi => {
      val toParam = paramz(pi)
      val fromParam = parameters(pi)
      fromParam.tip match {
        case fp: TypeVariable =>
          genericReplacements = (fp -> toParam.tip) :: genericReplacements
        case _ =>
      }
    })

    val replaceTypeVarsNames =
      trace("replaceTypeVarsNames for generics")(
        genericReplacements.groupBy(_._1.name).map({ case (str, tuples) =>
          str -> tuples.map(_._2 match {
            case tv: TypeVariable => tv.name
            case _ => null
          }).filter(_ != null).distinct
        }).toMap
      )

    replaceTypeVarsNames.foreach(r => if (r._2.length > 1) {
      trace(s"ambiguous variable name ${r._1} => ${r._2}")(replaceTypeVarsNames)
      throw TypeError(s"ambiguous variable name ${r._1} => ${r._2}")
    })

    val grp = genericReplacements.groupBy(_._1).map(r => r._1.name -> r._2.map(x => x._2))
    val genericReplaceMap = {
      trace("compute genericReplaceMap")(grp.map({ case (name, types) =>
        if (types.length > 1) {
          val tis = types.indices
          tis.foreach(ti1 =>
            tis.foreach(ti2 =>
              trace(s"check generic type of param ${name}: ${types(ti1)} assignable ${types(ti2)}")
              (if (!types(ti1).assignable(types(ti2))) {
                throw TypeError(s"ambiguous variable ${name} type ${types(ti1)} not assignable ${types(ti2)}")
              })
            )
          )
        }
        name -> types.head
      }))
    }

    val replaceTypeVarsName =
      trace(s"replaceTypeVarsName")(replaceTypeVarsNames.filter(_._2.nonEmpty).map(r => r._1 -> r._2.head))

    var ngenerics =
      trace("construct new GenericParams")(new GenericParams(
        generics.map {
          case av: AnyVariant =>
            if (genericReplaceMap.contains(av.name)) {
              trace(s"check assignable (AnyVariant) of ${av.name} from genericReplaceMap")(if (!av.assignable(genericReplaceMap(av.name))) {
                throw TypeError(s"can't assign generic param $av from ${genericReplaceMap(av.name)}")
              } else {
                if (replaceTypeVarsName.contains(av.name)) {
                  AnyVariant(replaceTypeVarsName(av.name))
                } else {
                  null
                }
              })
            } else {
              av
            }
          case cov: CoVariant =>
            if (genericReplaceMap.contains(cov.name)) {
              trace(s"check assignable (CoVariant) of ${cov.name} from genericReplaceMap")(if (!cov.assignable(genericReplaceMap(cov.name))) {
                throw TypeError(s"can't assign generic param $cov from ${genericReplaceMap(cov.name)}")
              } else {
                if (replaceTypeVarsName.contains(cov.name)) {
                  CoVariant(replaceTypeVarsName(cov.name), cov.tip)
                } else {
                  null
                }
              })
            } else {
              cov
            }
          case ctr: ContraVariant =>
            if (genericReplaceMap.contains(ctr.name)) {
              trace(s"check assignable (ContraVariant) of ${ctr.name} from genericReplaceMap")(if (!ctr.assignable(genericReplaceMap(ctr.name))) {
                throw TypeError(s"can't assign generic param $ctr from ${genericReplaceMap(ctr.name)}")
              } else {
                if (replaceTypeVarsName.contains(ctr.name)) {
                  ContraVariant(replaceTypeVarsName(ctr.name), ctr.tip)
                } else {
                  null
                }
              })
            } else {
              ctr
            }
        }.filter(_ != null).toList
      ))

    val retTypeVar: List[TypeVarLocator] = trace("returns type vars locators")(ret match {
      case tv: TypeVariable => List(new TypeVarLocator(tv))
      case tvf: TypeVarFetch => tvf.typeVarFetch()
      case _ => List()
    })
    val usedTypeVars = trace("usedTypeVars")(
      (
        trace("params type vars locators")(paramz.typeVarFetch()) ++
          retTypeVar
        ).map({ t => t.typeVar })
    )
    ngenerics = trace("final GenericParams")(
      new GenericParams((ngenerics.filter { gp => usedTypeVars.exists { tv => tv.name == gp.name } }).toList)
    )

    clone(ngenerics, paramz, ret)
  }

  /**
   * Клонирование
   *
   * @param fgParams Определение переменных типа - функции
   * @param fParams  Параметры функции
   * @param fReturn  Результат функции
   * @return клон
   */
  protected def clone(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = new Fn(fgParams, fParams, fReturn)

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoking[Z](impl: Seq[Any] => Any): CallableFn = {
    require(impl != null)
    new CallableFn(generics, parameters, returns, impl)
  }

  def callable[Z](impl: java.util.function.Function[java.util.List[Any], Any]): CallableFn = {
    require(impl != null)
    new CallableFn(generics, parameters, returns, args => {
      val list = new util.ArrayList[Any]()
      args.foreach(list.add)
      impl.apply(list)
    })
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[Z](impl: () => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 0) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns, _ => impl())
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A, Z](impl: A => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 1) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns, args => impl(args.head.asInstanceOf[A]))
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A, B, Z](impl: (A, B) => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 2) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B]
      ))
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A, B, C, Z](impl: (A, B, C) => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 3) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C]
      ))
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A, B, C, D, Z](impl: (A, B, C, D) => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 4) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C],
        args(3).asInstanceOf[D],
      ))
  }

  /**
   * Указывает реализацию
   *
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A, B, C, D, E, Z](impl: (A, B, C, D, E) => Z): CallableFn = {
    require(impl != null)
    if (parameters.length != 4) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics, parameters, returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C],
        args(3).asInstanceOf[D],
        args(4).asInstanceOf[E],
      ))
  }
}

/**
 * Создание функции
 */
object Fn {
  def apply(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = new Fn(fgParams, fParams, fReturn)

  def apply(fParams: Params, fReturn: Type): Fn = new Fn(GenericParams(), fParams, fReturn)

  def create(fParams: Params, fReturn: Type): Fn = new Fn(GenericParams(), fParams, fReturn)

  class GenericsBuilder(genericParams: GenericParams) {
    private var gp: MutableGenericParams = new MutableGenericParams(genericParams.toList)

    def build(): GenericParams = gp

    def any(name: String): GenericsBuilder = {
      require(name != null)
      gp.append(AnyVariant(name))
      this
    }

    def coVariant(name: String, tip: Type): GenericsBuilder = {
      require(name != null)
      require(tip != null)
      gp.append(CoVariant(name, tip))
      this
    }

    def contraVariant(name: String, tip: Type): GenericsBuilder = {
      require(name != null)
      require(tip != null)
      gp.append(ContraVariant(name, tip))
      this
    }

    def clear(): GenericsBuilder = {
      gp.clear()
      this
    }
  }

  class ParamsBuilder(var params: Params) {
    def build(): Params = params

    def clear(): ParamsBuilder = {
      params = new Params()
      this
    }

    def add(name: String, paramType: Type): ParamsBuilder = {
      require(name != null)
      require(paramType != null)
      params = new Params(params.toList ++ List(new Param(name, paramType)))
      this
    }
  }

  class Builder {
    private var genericParams: GenericParams = GenericParams()

    def generics(builder: java.util.function.Consumer[GenericsBuilder]): Builder = {
      require(builder != null)
      val b = new GenericsBuilder(genericParams)
      builder.accept(b)
      genericParams = b.build()
      this
    }

    private var returns: Type = Type.VOID

    def returns(tip: Type): Builder = {
      require(tip != null)
      returns = tip
      this
    }

    private var params: Params = Params()

    def params(builder: java.util.function.Consumer[ParamsBuilder]): Builder = {
      require(builder != null)
      val b = new ParamsBuilder(params)
      builder.accept(b)
      params = b.build()
      this
    }

    def build(): Fn = {
      Fn(genericParams, params, returns)
    }
  }

  def create(): Builder = new Builder
}
