package xyz.cofe.stsl.types

/**
 * Определение класса данных/объектов
 * @param Name Имя класса
 * @param ogenerics Параметры типа
 * @param oextend Какой тип расширяет
 * @param ofields Атрибуты/поля класса
 * @param omethods Методы класса
 */
class TObject( Name:String,
               ogenerics:MutableGenericParams=new MutableGenericParams(),
               oextend:Option[Type] = Some(Type.ANY),
               ofields:MutableFields=new MutableFields(),
               omethods:MutableMethods=new MutableMethods()
             ) extends Obj with TypeVarReplace[TObject] with Freezing {
  require(Name!=null)
  require(ogenerics!=null)
  require(ofields!=null)
  require(omethods!=null)
  require(oextend!=null)

  //region "Заморозка"

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue

  def freeze:Unit = {
    validateTypeVariables()
    freezedValue = true
    methods.freeze
    fields.freeze
    generics.freeze
  }
  //endregion

  private var nameValue : String = Name
  override def name: String = nameValue
  def name_=( value:String ):String = {
    require(value!=null)
    require(value.trim.length>0)
    if( freezed )throw TypeError("freezed")
    nameValue = value
    value
  }
  def setName( value:String ):TObject = {
    this.name = value
    this
  }

  private var extendValue = oextend
  override def extend: Option[Type] = extendValue
  def extend_=( value:Option[Type] ):Option[Type] = {
    require(value!=null)
    if( freezed )throw TypeError("freezed")
    extendValue = value
    extendValue
  }
  def extend( parentType:Type ):TObject = {
    require(parentType!=null)
    this.extend = Some(parentType)
    this
  }

  override lazy val generics: MutableGenericParams = ogenerics
  override lazy val fields: MutableFields = ofields
  override lazy val methods: MutableMethods = omethods

  private def fieldsTypeVariablesMap = fields.filter(f => f.tip.isInstanceOf[TypeVariable]).map(f => f.name -> f.tip.asInstanceOf[TypeVariable]).toMap
  private def fieldsTypeVariables  = fieldsTypeVariablesMap.values
  private def methodsTypeVariables = methods.funs.values.flatMap(f => f.funs).flatMap(f => f.typeVariables)
  private def typeVariables = fieldsTypeVariables ++ methodsTypeVariables

  /**
   * Проверка что указанные типы-переменных соответствуют объявленным
   */
  private def validateTypeVariables():Unit = {
    typeVariables
      .filter( tv => tv.owner==Type.THIS )
      .foreach( vname =>
        if( !generics.params.map(_.name).contains(vname.name) ){
          throw TypeError(s"bind undeclared type variable $vname into Object")
        }
      )
  }

  validateTypeVariables()

  /**
   * Клонирует объект с новыым именем
   * @param name новое имя
   * @return клон
   */
  def withName(name:String):TObject = {
    require(name!=null)
    new TObject(name,generics,extend,fields,methods)
  }

  override def toString: String = {
    s"${name}${generics}"
  }

  //region typeVarReplace() - Замена переменных типа
  /**
   * Замена переменных типа в данном классе
   * @param recipe правило замены переменных
   * @return клон с замененными переменными-типами
   */
  override def typeVarReplace(recipe: TypeVariable => Option[Type]): TObject = {
    require(recipe!=null)
    var replaceTypeVarMap : Map[String,Type] = Map()

    val replacement : TypeVariable => Option[Type] = (tv) => {
      val trgt = recipe(tv)
      if( trgt.isDefined ){
        println( s"replacement ${tv} => ${trgt.get}" )
        if( replaceTypeVarMap.contains(tv.name) ){
          //
        }else{
          if( !generics(tv.name).assignable(trgt.get) ){
            throw TypeError(s"can't assign type ${trgt.get} into type variable ${generics(tv.name)}")
          }
          replaceTypeVarMap = replaceTypeVarMap + ( tv.name -> trgt.get )
        }
      }
      trgt
    }

    val newGeneric = generics match {
      case tvr: TypeVarReplace[GenericParams] =>
        tvr.typeVarReplace(replacement)
      case _ =>
        generics
    }
    val newExtend = if( extend.isDefined ){
      val ext : Type = extend.get match {
        case t: TypeVarReplace[_] => t.typeVarReplace(replacement).asInstanceOf[Type]
        case _ => extend.get
      }
      Some(ext)
    }else{
      None
    }
    val asIsFields : Seq[Field] = fields.filter( f=> !fieldsTypeVariablesMap.contains(f.name) )
    val newTvFields : Seq[Field] = fields
      .filter( f=> fieldsTypeVariablesMap.contains(f.name) )
      .map( f => new Field(f.name,
        f.tip match {
          case tv:TypeVariable =>
            replacement(tv).getOrElse(
              tv match {
                case tv2:TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
                case _ => tv
              }
            )
          case _ =>
            f.tip match {
              case tv2:TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
              case _ => f.tip
            }
        }
      )
    )
    val newFields = Fields( (asIsFields ++ newTvFields).toList )
    val newMethods = new Methods(
      methods.funs.map({case(name, funs)=>
        name -> new Funs(funs.map( fun => {
          fun.typeVarReplace(replacement)
        }).toList)
      })
    )

    val nnewGeneric = new GenericParams(
      newGeneric.params.map( p => {
        if( replaceTypeVarMap.get(p.name).exists(t=>t.isInstanceOf[TypeVariable]) ){
          val tv = replaceTypeVarMap(p.name).asInstanceOf[TypeVariable]
          p match {
            case av:AnyVariant => AnyVariant(tv.name)
            case cov:CoVariant => CoVariant(tv.name,cov.tip)
            case ctr:ContraVariant => ContraVariant(tv.name,ctr.tip)
          }
        }else{
          null
        }
      }).filter(_ != null)
    )

    val newTObj = TObject(name,nnewGeneric,newExtend,newFields,newMethods)

    newTObj
  }
  //endregion
}

object TObject {
  def apply( Name: String
           , ogenerics: GenericParams
           , oextend:Option[Type]
           , ofields: Fields
           , omethods: Methods): TObject = new TObject(
    Name,
    ogenerics match {
      case m:MutableGenericParams => m
      case _ => new MutableGenericParams(ogenerics.params)
    },
    oextend,
    ofields match {
      case m: MutableFields => m
      case _ => new MutableFields(ofields.fields)
    },
    omethods match {
      case m:MutableMethods => m
      case _ => new MutableMethods(omethods.funs)
    }
  )

  class Builder( val name:String ) {
    private var oextend:Option[Type] = Some(Type.ANY)
    def extend(ext:Option[Type]):Builder = {
      require(ext!=null)
      oextend = ext
      this
    }
    def extend(ext:Type):Builder = {
      require(ext!=null)
      oextend = Some(ext)
      this
    }

    private var ogenerics = GenericParams()
    def generics(newGenerics:GenericParams):Builder = {
      require(newGenerics!=null)
      ogenerics = newGenerics
      this
    }
    def generics(newGenerics:GenericParam*):Builder = {
      require(newGenerics!=null)
      ogenerics = new GenericParams(newGenerics.toList)
      this
    }
    private var ofields = Fields()
    def fields(newFields:Fields):Builder = {
      require(newFields!=null)
      ofields = newFields
      this
    }
    def fields(newFields:(String,Type)*):Builder = {
      require(newFields!=null)
      ofields = new Fields( newFields.map(f=> new Field(f._1,f._2)).toList )
      this
    }
    private var omethods = new Methods()
    def methods(newMethods:Methods):Builder = {
      require(newMethods!=null)
      omethods = newMethods
      this
    }
    def methods(newMethods:(String,Fun)*):Builder = {
      require(newMethods!=null)
      val map1:Map[String,Seq[Fun]] = newMethods.toList.groupBy(_._1).map({case(name,ls)=> name -> ls.map(_._2)}).toMap
      val map2 = map1.map({case(name,ls)=> name -> new Funs(ls.toList)})
      omethods = new Methods(map2)
      this
    }
    def build:TObject = {
      new TObject(
        name,
        ogenerics match {
          case m:MutableGenericParams => m
          case _ => new MutableGenericParams(ogenerics.params)
        },
        oextend,
        new MutableFields(ofields.fields),
        new MutableMethods(omethods.funs))
    }
  }

  def apply(Name: String) = new Builder(Name)
}