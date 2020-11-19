package xyz.cofe.stsl.types

/**
 * Функция
 * @param fgParams Определение переменных типа - функции
 * @param fParams Параметры функции
 * @param fReturn Результат функции
 */
class Fn( fgParams: GenericParams
             , fParams: Params
             , fReturn: Type
             ) extends Fun {
  require(fgParams!=null)
  require(fParams!=null)
  require(fReturn!=null)

  // Переменные типа обявленные для owner = FN, должны указывать на  GenericParams
  lazy val inputTypeVariables: Seq[TypeVariable] =
    fParams.params.map( p => p.tip match {
      case fn:Fn => fn.typeVariables
      case gv:TypeVariable => List(gv)
      case _ => List()
    }).filter( _ != null ).flatten

  lazy val outTypeVariable: Seq[TypeVariable] =
    fReturn match {
      case fn:Fn => fn.typeVariables
      case gv:TypeVariable => List(gv)
      case _ => List()
    }

  /**
   * Экземепляры переменных типа
   */
  override lazy val typeVariables: Seq[TypeVariable] = inputTypeVariables ++ outTypeVariable

  typeVariables
    .filter( gv => gv.owner == Type.FN )
    .map( _.name )
    .foreach( vname =>
      if( !fgParams.params.map(_.name).contains(vname) ){
        throw TypeError(s"bind undeclared type variable $vname into Fn")
      }
    )

  override lazy val generics: GenericParams = fgParams
  override lazy val parameters: Params = fParams
  override lazy val returns: Type = fReturn

  override def typeVarReplace(recipe: TypeVariable => Option[Type]): Fun = {
    val ret : Type = returns match {
      case tv:TypeVariable =>recipe(tv).getOrElse(
        tv match {
          case tvr: TypeVarReplace[_] => tvr.typeVarReplace(recipe).asInstanceOf[Type]
          case _ => tv
        })
      case _ => returns match {
        case tvr: TypeVarReplace[_] => tvr.typeVarReplace(recipe).asInstanceOf[Type]
        case _ => returns
      }
    }

    val paramz = Params( parameters.params.map(p => p.typeVarReplace(recipe)) )

    var genericReplacements : List[(TypeVariable,Type)] =
      returns match {
        case retTv:TypeVariable => List(( retTv -> ret ))
        case _ => List()
      }

    paramz.indices.foreach(pi => {
      val toParam = paramz(pi)
      val fromParam = parameters(pi)
      fromParam.tip match {
        case fp: TypeVariable =>
          genericReplacements = ( fp -> toParam.tip ) :: genericReplacements
        case _ =>
      }
    })

    val replaceTypeVarsNames =
      genericReplacements.groupBy( _._1.name ).map({ case (str, tuples) =>
        str -> tuples.map(_._2 match {
          case tv:TypeVariable => tv.name
          case _=> null
        }).filter(_!=null).distinct
      }).toMap

    replaceTypeVarsNames.foreach( r => if(r._2.length>1){
      throw TypeError(s"ambiguous variable name ${r._1} => ${r._2}")
    })

    val grp = genericReplacements.groupBy(_._1).map(r=>r._1.name -> r._2.map(x=>x._2))
    val genericMap = grp.map({ case (name, types) =>
      if( types.length>1 ){
        val tis = types.indices
        tis.foreach( ti1 =>
          tis.foreach( ti2 =>
            if( !types(ti1).assignable(types(ti2)) ){
              throw TypeError(s"ambiguous variable ${name} type ${types(ti1)} not assignable ${types(ti2)}")
            }
          )
        )
      }
      name -> types.head
    })

    val replaceTypeVarsName = replaceTypeVarsNames.filter( _._2.nonEmpty ).map( r => r._1 -> r._2.head )

    val ngenerics = GenericParams(
      generics.map {
        case av: AnyVariant =>
          if( !av.assignable(genericMap(av.name)) ){
            throw TypeError(s"can't assign generic param $av from ${genericMap(av.name)}")
          }else {
            if( replaceTypeVarsName.contains(av.name) ) {
              AnyVariant(replaceTypeVarsName(av.name))
            }else{
              null
            }
          }
        case cov: CoVariant =>
          if( !cov.assignable(genericMap(cov.name)) ){
            throw TypeError(s"can't assign generic param $cov from ${genericMap(cov.name)}")
          }else {
            if( replaceTypeVarsName.contains(cov.name) ) {
              CoVariant(replaceTypeVarsName(cov.name), cov.tip)
            }else{
              null
            }
          }
        case ctr: ContraVariant =>
          if( !ctr.assignable(genericMap(ctr.name)) ){
            throw TypeError(s"can't assign generic param $ctr from ${genericMap(ctr.name)}")
          }else {
            if( replaceTypeVarsName.contains(ctr.name) ){
              ContraVariant(replaceTypeVarsName(ctr.name), ctr.tip)
            }else{
              null
            }
          }
      }.filter(_!=null).toList
    )

    clone(ngenerics,paramz,ret)
  }

  /**
   * Клонирование
   * @param fgParams Определение переменных типа - функции
   * @param fParams Параметры функции
   * @param fReturn Результат функции
   * @return клон
   */
  protected def clone( fgParams: GenericParams, fParams: Params, fReturn: Type ):Fn = new Fn(fgParams,fParams,fReturn)

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoking[Z]( impl: Seq[_]=>_ ):CallableFn = {
    require(impl!=null)
    new CallableFn(generics,parameters,returns,impl)
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[Z]( impl: ()=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=0 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,_ => impl())
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A,Z]( impl: A=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=1 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,args => impl(args.head.asInstanceOf[A]))
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A,B,Z]( impl: (A,B)=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=2 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B]
      ))
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A,B,C,Z]( impl: (A,B,C)=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=3 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C]
      ))
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A,B,C,D,Z]( impl: (A,B,C,D)=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=4 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C],
        args(3).asInstanceOf[D],
      ))
  }

  /**
   * Указывает реализацию
   * @param impl реализация функции
   * @return вызываемая функция
   */
  def invoke[A,B,C,D,E,Z]( impl: (A,B,C,D,E)=>Z ):CallableFn = {
    require(impl!=null)
    if( parameters.length!=4 ) throw TypeError(s"function require ${parameters.length} parameters")
    new CallableFn(generics,parameters,returns,
      args => impl(
        args.head.asInstanceOf[A],
        args(1).asInstanceOf[B],
        args(2).asInstanceOf[C],
        args(3).asInstanceOf[D],
        args(4).asInstanceOf[E],
      ))
  }
}

object Fn {
  def apply(fgParams: GenericParams, fParams: Params, fReturn: Type): Fn = new Fn(fgParams, fParams, fReturn)
  def apply(fParams: Params, fReturn: Type): Fn = new Fn(GenericParams(), fParams, fReturn)
}
