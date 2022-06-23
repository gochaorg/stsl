package xyz.cofe.stsl.types

import java.util

/**
 * Определение класса данных/объектов
 *
 * @param Name Имя класса
 * @param ogenerics Параметры типа
 * @param oextend Какой тип расширяет
 * @param fields Атрибуты/поля класса
 * @param methods Методы класса
 */
class TObject( Name:String,
               val generics:MutableGenericParams=new MutableGenericParams(),
               oextend:Option[Type] = Some(Type.ANY),
               val fields:MutableFields=new MutableFields(),
               val methods:MutableMethods=new MutableMethods()
             ) extends Obj with Named with TypeVarReplace[TObject] with Freezing {
  
  override type FIELDS = MutableFields
  override type METHODS = MutableMethods
  override type GENERICS = MutableGenericParams
  
  require(Name!=null)
  require(generics!=null)
  require(fields!=null)
  require(methods!=null)
  require(oextend!=null)

  //region "Заморозка"
  
  private var freezedValue : Boolean = false

  /**
   * Проверка что объект уже заморожен
   * @return true - объект уже заморожен, его нельзя изменять
   */
  def freezed : Boolean = freezedValue

  /**
   * Заморозка объекта
   */
  def freeze:Unit = {
    validateTypeVariables()
    freezedValue = true
    methods.freeze
    fields.freeze
    generics.freeze
  }
  //endregion
  
  //region name:String

  private var nameValue : String = Name

  /**
   * Возвращает название класса
   * @return название класса
   */
  override def name: String = nameValue

  /**
   * Указывает название класса
   * @param value название класса
   * @return новое название класса
   */
  def name_=( value:String ):String = {
    require(value!=null)
    require(value.trim.nonEmpty)
    if( freezed )throw TypeError("freezed")
    nameValue = value
    value
  }

  /**
   * Указывает название класса
   * @param value название класса
   * @return self ссылка
   */
  def setName( value:String ):TObject = {
    this.name = value
    this
  }
  
  /**
   * Клонирует объект с новыым именем
   * @param name новое имя
   * @return клон
   */
  def withName(name:String):TObject = {
    require(name!=null)
    new TObject(name,generics,extend,fields,methods)
  }
  //endregion

  //region extend
  
  private var extendValue = oextend

  /**
   * Возвращает родительский класс/тип данных
   * @return родительский класс/тип данных
   */
  override def extend: Option[Type] = extendValue

  /**
   * Указывает родительский тип данных
   * @return родительский класс/тип данных
   */
  def extend_=( value:Option[Type] ):Option[Type] = {
    require(value!=null)
    if( freezed )throw TypeError("freezed")
    extendValue = value
    extendValue
  }

  /**
   * Указывает родительский тип данных
   * @param parentType родительский класс/тип данных
   * @return self ссылка
   */
  def extend( parentType:Type ):TObject = {
    require(parentType!=null)
    this.extend = Some(parentType)
    this
  }
  //endregion

  //override lazy val generics: MutableGenericParams = ogenerics

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

    if( generics.isEmpty ){
      this
    }else {
      var replaceTypeVarMap: Map[String, Type] = Map()

      val replacement: TypeVariable => Option[Type] = (tv) => {
        val trgt = recipe(tv)
        if (trgt.isDefined) {
          //println( s"replacement ${tv} => ${trgt.get}" )
          if (replaceTypeVarMap.contains(tv.name)) {
            //
          } else {
            if (!generics(tv.name).assignable(trgt.get)) {
              throw TypeError(s"can't assign type ${trgt.get} into type variable ${generics(tv.name)}")
            }
            replaceTypeVarMap = replaceTypeVarMap + (tv.name -> trgt.get)
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

      val newExtend = if (extend.isDefined) {
        val ext: Type = extend.get match {
          case t: TypeVarReplace[_] => t.typeVarReplace(replacement).asInstanceOf[Type]
          case _ => extend.get
        }
        Some(ext)
      } else {
        None
      }

      val asIsFields: Seq[Field] = fields.filter(f => !fieldsTypeVariablesMap.contains(f.name))
      val newTvFields: Seq[Field] = fields
        .filter(f => fieldsTypeVariablesMap.contains(f.name))
        .map(f => new Field(f.name,
          f.tip match {
            case tv: TypeVariable =>
              replacement(tv).getOrElse(
                tv match {
                  case tv2: TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
                  case _ => tv
                }
              )
            case _ =>
              f.tip match {
                case tv2: TypeVarReplace[_] => tv2.typeVarReplace(replacement).asInstanceOf[Type]
                case _ => f.tip
              }
          }
        )
        )
      val newFields = Fields((asIsFields ++ newTvFields).toList)
      val newMethods = new Methods(
        methods.funs.map({ case (name, funs) =>
          name -> new Funs(funs.map(fun => {
            fun.typeVarReplace(replacement)
          }).toList)
        })
      )

      val nnewGeneric = new GenericParams(
        newGeneric.params.map(p => {
          if (replaceTypeVarMap.get(p.name).exists(t => t.isInstanceOf[TypeVariable])) {
            val tv = replaceTypeVarMap(p.name).asInstanceOf[TypeVariable]
            p match {
              case av: AnyVariant => AnyVariant(tv.name)
              case cov: CoVariant => CoVariant(tv.name, cov.tip)
              case ctr: ContraVariant => ContraVariant(tv.name, ctr.tip)
            }
          } else {
            null
          }
        }).filter(_ != null)
      )

      val newTObj = TObject(name, nnewGeneric, newExtend, newFields, newMethods)

      newTObj
    }
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

  //region Конструирование объекта
  class FieldBuilder(
                      private var fields: Fields,
                      private val newFields:(Fields)=>Any,
                      private val fb: FieldsBuilder,
                      //val name:String, val tip:Type
                      var field : Field
                    ){
    require(field!=null)
    def add:FieldsBuilder = {
      fields = new Fields((fields.filter( f => f.name != field.name ) ++ List(field)).toList)
      newFields(fields)
      fb
    }
    def writeablez( read:java.util.function.Function[Any,Any], write:java.util.function.BiFunction[Any,Any,Any]
                 ):FieldBuilder = {
      require(read!=null)
      require(write!=null)
      field = field.writeable( obj => read.apply(obj), (obj,fldVal)=>write.apply(obj,fldVal) )
      this
    }
    def writeable( read:Any=>Any, write:(Any,Any)=>Any ):FieldBuilder = {
      field = field.writeable(read,write)
      this
    }
    def build:Fields = this.fields
  }
  class FieldsBuilder( private var fields:Fields ) {
    def build:Fields = fields
    def field(name:String, fieldType:Type):FieldBuilder = {
      require(name!=null)
      require(fieldType!=null)
      new FieldBuilder(fields, flds => {
        fields = flds
      }, this, new Field(name,fieldType)) // name, fieldType)
    }
    def field(fld : Field):FieldBuilder = {
      require(fld!=null)
      new FieldBuilder(fields, flds => {fields = flds}, this, fld )
    }
  }

  class ParamsBuilder {
    private var params:Params = Params()
    def build:Params = params
    def param(name:String, tip:Type):ParamsBuilder = {
      require(name!=null)
      require(tip!=null)
      params = Params( params.toList ++ List(Param(name,tip)) )
      this
    }
  }

  class MethodBuilder( private val mb:MethodsBuilder, private val methods:MutableMethods ){
    private var name:Option[String] = None
    def getName:String = name.orNull
    def setName(newName:String):Unit = {
      require(newName!=null)
      name = Some(newName)
    }
    def name(newName:String):MethodBuilder = {
      setName(newName)
      this
    }

    private var generics:GenericParams = GenericParams()
    def getGenerics:GenericParams = generics
    def setGenerics(newGenerics:GenericParams):Unit = {
      require(newGenerics!=null)
      generics = newGenerics
    }
    def generics(newGenerics:GenericParams):MethodBuilder = {
      setGenerics(newGenerics)
      this
    }
    def generics(builder: java.util.function.Consumer[Fn.GenericsBuilder]):MethodBuilder = {
      require(builder!=null)
      val b = new Fn.GenericsBuilder(generics)
      builder.accept(b)
      generics = b.build()
      this
    }

    private var params:Params = Params()
    def getParams:Params = params
    def setParams(newParams:Params):Unit = {
      require(newParams!=null)
      params = newParams
    }
    def params(newParams:Params):MethodBuilder = {
      require(newParams!=null)
      params = newParams
      this
    }
    def params(builder: java.util.function.Consumer[ParamsBuilder]):MethodBuilder = {
      require(builder!=null)
      val pb = new ParamsBuilder
      builder.accept(pb)
      params = pb.build
      this
    }

    private var result:Type = Type.VOID
    def getResult:Type = result
    def setResult(newResult:Type):Unit = {
      require(newResult!=null)
      result = newResult
    }
    def result(newResult:Type):MethodBuilder = {
      require(newResult!=null)
      result = newResult
      this
    }

    private var call : Option[Seq[Any] => Any] = None
    def getCall:Seq[Any]=>Any = call.orNull
    def setCall(newCall:Seq[Any]=>Any):Unit = {
      require(newCall!=null)
      call = Some(newCall)
    }
    def invoking(newCall:Seq[Any]=>Any):MethodBuilder = {
      require(newCall!=null)
      call = Some(newCall)
      this
    }
    def callable(newCall:java.util.List[Any]=>Any):MethodBuilder = {
      require(newCall!=null)
      call = Some(args=>{
        val ls = new util.ArrayList[Any]()
        args.foreach(ls.add)
        newCall.apply(ls)
      })
      this
    }

    var fnBuilder : (GenericParams,Params,Type) => Fn = null
    def fnBuilder( builder: (GenericParams,Params,Type) => Fn ):MethodBuilder = {
      fnBuilder = builder
      this
    }

    def add:MethodsBuilder = {
      if( name==null )throw TypeError("name not set")
      var f = if( fnBuilder!=null ) fnBuilder(generics,params,result) else Fn(generics, params, result)
      if( call.isDefined ){
        f = f.invoking(call.get)
      }
      methods.append(name.get, f)
      call = None
      result = Type.VOID
      params = Params()
      generics = GenericParams()
      fnBuilder = null
      mb
    }
  }
  class MethodsBuilder( private var methods: Methods ){
    private val mutMethods = new MutableMethods(methods.funs);
    def build():Methods = mutMethods
    def method( builder:java.util.function.Consumer[MethodBuilder] ):MethodsBuilder = {
      require(builder!=null)
      builder.accept( new MethodBuilder(this, mutMethods))
      this
    }
  }

  class GenericBuilder( var generics : GenericParams ) {
  }

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
    def generics(builder:java.util.function.Consumer[GenericBuilder]):Builder = {
      require(builder!=null)
      val bld = new GenericBuilder(ogenerics)
      builder.accept(bld)
      val ogen = bld.generics
      ogenerics = if( ogen!=null )ogen else ogenerics
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
    def fileds(builder:java.util.function.Consumer[FieldsBuilder]):Builder = {
      require(builder!=null)
      val bld = new FieldsBuilder(ofields)
      builder.accept(bld)
      ofields = bld.build
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
    def methods(builder:java.util.function.Consumer[MethodsBuilder]):Builder = {
      require(builder!=null)
      val mths = new MethodsBuilder(omethods)
      builder.accept(mths)
      omethods = mths.build()
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
  def create(Name:String) = new Builder(Name)

  class TBuilder( val tobj : TObject ) {
    require(tobj!=null)
    def methods(builder:java.util.function.Consumer[MethodsBuilder]):TBuilder = {
      require(builder!=null)
      val mths = new MethodsBuilder(tobj.methods)
      builder.accept(mths)

      tobj.methods.clear()
      mths.build().funs.foreach( {case(name,funs)=>
        funs.foreach( f => {
          tobj.methods.append(name, f)
        })
      })

      this
    }
  }

  def build(tip:TObject):TBuilder = {
    require(tip!=null)
    new TBuilder(tip)
  }
  //endregion
}