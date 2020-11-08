package xyz.cofe.sel.types

class ObjectType(
                  name:String,
                  extend:Option[Type] = None,
                  private val props: Properties = Properties.empty,
                  private val genericParams:List[GenericParam]=List(),
                  objectMethods:Methods = new Methods()
                ) extends BasicType( name, extend ) {
  require(name!=null)
  require(extend!=null)

  // Проверка generic параметров
  require(genericParams!=null)

  // Имена generic параметров не должны совпадать
  if( genericParams.groupBy(p => p.name).map({ case (k, v) => k -> v.size }).map({ case (k, c) => c }).exists(c => c > 1) ){
    throw TypeError(s"has duplicate generic name params")
  }

  // имена в GenericPlaceholder должны совпадать с genericParams
  private val genericParamNames = genericParams.map(_.name)
  objectMethods.map.values.flatten.flatMap(f => f.params.map(p => p.paramType) ++ List(f.returnType)).
    filter(t => t.isInstanceOf[GenericPlaceholder]).
    map( t => t.asInstanceOf[GenericPlaceholder]).
    filter( gp =>
      gp.owner == this ||
        gp.owner == Type.THIS
    ).
    map( t => t.name ).toList.distinct.foreach( gpName =>
      require(genericParamNames.contains(gpName))
    )


  // Проверка свйоств
  require(props!=null)

  // Имена свйоств не должны совпадать
  if( props.groupBy(p => p.name).map({ case (k, v) => k -> v.size }).map({ case (k, c) => c }).exists(c => c > 1) ){
    throw TypeError(s"has duplicate property name")
  }

  /**
   * Поля объекта
   * @return поля
   */
  override lazy val properties: Properties = {
    if( extend.isDefined ){
      new Properties(props.map.toList.map(_._2), List(extend.get.properties))
    }else{
      props
    }
  }

  /**
   * Параметры типа
   * @return параметры типы
   */
  override lazy val generics: List[GenericParam] = genericParams

  /**
   * методы объекта
   */
  override lazy val methods: Methods = {
    objectMethods.thisType(this)
  }

  /**
   * Замена generic заместителей на типы данных
   * @param recipe правила замены
   * @param targetName имя типа, если не указано будет сгененировано
   */
  def bakeGenerics( recipe:Map[String,Type], targetName:Option[String]=None ):ObjectType = {
    require(recipe!=null)

    // Имена должны совпадать
    recipe.foreach({case(name,replacement)=>require(
      genericParamNames.contains(name) && replacement!=null && !replacement.isInstanceOf[GenericPlaceholder]
    )})

    // типы должны быть совместимы
    recipe.foreach({case(name,replacement)=>
      val gp = genericParams.find( g=>g.name==name )
      require(gp.get.assignable(replacement))
    })

    if( recipe.isEmpty ){
      this
    }else {
      val newGenerics = generics.filter( g => !recipe.contains(g.name) )
      var newMethods = methods
      val self = this
      def replacement( from:Type ):Option[Type] = {
        if( from==null ){
          None
        }else{
          from match {
            case gh:GenericPlaceholder =>
              if( gh.owner == self ) {
                recipe.get(gh.name)
              } else None
            case _ => if(
              from==self
            ) {
              from match {
                case gi: GenericInstance => None
                case _ => Some(Type.THIS)
              }
            } else {
              None
            }
          }
        }
      }
      newMethods = newMethods.typeReplace(replacement)

      val newName = if( targetName.isDefined ){ targetName.get } else {
        name + "_" + recipe.values.map( t => t.name ).reduce( (a,b)=>a+"_"+b )
      }
      new ObjectType(
        newName, extend, props, newGenerics, newMethods
      )
    }
  }
}
