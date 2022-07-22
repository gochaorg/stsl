package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.{CallableFn, Field, Fields, Fn, Fun, Funs, GenericInstance, InheritedFields, Methods, MutableFuns, Named, Obj, Param, Params, TAnon, TObject, Type, WriteableField}

/**
 * Вся эта штука применима к TAnon
 */
package object isect {
  /**
   * Поиск общего типа среди заданных
   * @param at первый тип
   * @param bt второй тип
   * @return общий тип
   */
  def commonType(at:Type, bt:Type):Option[Type] = {
    (at.assignable(bt), bt.assignable(at)) match {
      case (true, true) => Some(at)
      case (true, false) => Some(at)
      case (false, true) => Some(bt)
      case _ => None
    }
  }
  
  /**
   * Коллектор полей (field) объектов
   * @param fields поля
   */
  case class FieldsCollector(fields: Map[String,List[Field]] )
  object FieldsCollector {
    def apply[FIELDS <: InheritedFields](fielTypes: Seq[FIELDS] ): FieldsCollector = {
      fielTypes.foldLeft(new FieldsCollector(Map()))((collector0, fieldOwner) => {
        fieldOwner.publicFields.foldLeft(collector0)( (collector,field) => {
          collector.copy(
            fields = collector.fields + (
              field.name ->
                (field :: collector.fields.getOrElse(field.name,List()))
              )
          )
        })
      })
    }
  }
  
  /**
   * Указывает опциональный generic тип
   * @param genericType generic тип
   * @param typeParamName имя типа-переменной
   */
  case class OptionalField( genericType:TObject, typeParamName:String )
  
  /**
   * Создание опционального типа
   */
  sealed trait OptionalBuilder {
    def build(of:OptionalField, tip:Type):Type
  }
  
  /**
   * Создание типа заменой типа-переменной
   */
  case class OptBaker() extends OptionalBuilder {
    override def build(of:OptionalField, tip:Type): Type = {
      of.genericType.typeVarBake.thiz(of.typeParamName -> tip) match {
        case nameTypeResult:Named => tip match {
          case namedParamType:Named =>
            nameTypeResult.withName( s"${nameTypeResult.name}$$${of.typeParamName}=${namedParamType.name}" )
          case _ => nameTypeResult
        }
        case t => t
      }
    }
  }
  
  /**
   * Создание типа путем подстановки [[GenericInstance]]
   */
  case class OptGenInstance() extends OptionalBuilder {
    override def build(of: OptionalField, tip: Type): Type =
      new GenericInstance[TObject](
        Map(of.typeParamName -> tip),
        of.genericType
      )
  }
  
  /**
   * Редукция коллектора полей до списка совместимых полей
   * @param optionalField тип который будет опциональным
   * @param optBuilder построение опционального типа
   */
  case class FieldsReductor(
                             optionalField: OptionalField,
                             optBuilder:OptionalBuilder = OptBaker()
                           ) {
    def reduce( fieldsCollector: FieldsCollector ):List[WriteableField] = {
      val instanceCount = fieldsCollector.fields.map(_._2.size).max
      val commonType0 = fieldsCollector.fields.map { case(name,fields) =>
        if( fields.isEmpty ){
          (true,name,None)
        }else{
          if( fields.size==1 ){
            (fields.size>=instanceCount,name,Some(fields.head.tip))
          }else{
            val fieldTypes : List[Type] = fields.map(_.tip)
            val ct = fieldTypes.tail.foldLeft( Some(fieldTypes.head):Option[Type] )( (result,fType) => {
              result match {
                case Some(r) => commonType(r,fType)
                case None => result
              }
            })
            (fields.size>=instanceCount,name,ct)
          }
        }
      }.filter { case(fully,name,ct) => ct.isDefined }
        .map { case(fully,name,ct) => (fully,name,ct.get) }
      
      commonType0.map { case(fully,name,tip) =>
        if (fully) {
          (name, tip)
        } else {
          ( name,
            optBuilder.build(optionalField, tip)
          )
        }
      }.map { case(name,tip) => new WriteableField(
        name = name,
        tip = tip,
        reading = (inst:Any) => inst.asInstanceOf[java.util.Map[Any,Any]].get(name),
        writing = (inst:Any,newValue:Any) => {
          inst.asInstanceOf[java.util.Map[Any,Any]].put(name,newValue)
          newValue
        }
      )}.toList
    }
  }
  
  /**
   * Коллектор методов
   * @param common общая проекция методов
   * @param joinCount кол-во объеденных классов
   */
  case class MethodCollector( common:Map[String,Funs] = Map(),
                              joinCount:Int=0
                            ) {
    def join( methods:Methods ):MethodCollector = {
      joinCount match {
        case 0 => joinInitial(methods)
        case _ => joinContinue(methods)
      }
    }
    
    private def joinInitial( methods:Methods ):MethodCollector = {
      copy(joinCount=joinCount+1, common=methods.funs)
    }
    
    //noinspection SimplifyBooleanMatch
    private def joinContinue( methods:Methods ):MethodCollector = {
      val y: Map[String, Seq[Fun]] = common.map { case(cmName,cmFuns) =>
        val x: Seq[Fun] = methods.get(cmName) match {
          case Some(jmFuns) =>
            val joinedMeth: Seq[Fun] = cmFuns.flatMap { cmFun =>
              val joinedFun: Option[Fun] = jmFuns.map { jmFun => {
                val retType = (jmFun.returns.assignable(cmFun.returns), cmFun.returns.assignable(jmFun.returns)) match {
                  case (false, false) => None
                  case (true, true) => Some(cmFun.returns)
                  case (true, false) => Some(jmFun.returns)
                  case (false, true) => Some(cmFun.returns)
                }
                
                (cmFun.assignable(jmFun) match {
                  case true => jmFun.assignable(cmFun) match {
                    case true => Some(cmFun)
                    case false => Some(jmFun)
                  }
                  case false => jmFun.assignable(cmFun) match {
                    case true => Some(cmFun)
                    case false => None
                  }
                }).flatMap( f => retType.map( rt =>
                  Fn( f.generics, f.parameters, rt )
                ))
              }
              }.find {
                _.isDefined
              }.flatten
              joinedFun
            }
            joinedMeth
          case None =>
            Seq()
        }
        cmName -> x
      }
      val z = y.map { case (name,funs) =>
        name -> funs.foldLeft(new MutableFuns())( (mfuns,f) => {mfuns.append(f); mfuns} ).asInstanceOf[Funs]
      }.filter{ case (_,funs) => funs.nonEmpty }
      copy(
        joinCount=joinCount+1,
        common = z
      )
    }
  }
  
  /**
   * Генерирует вызываемый метод (функцию) на основании существующей функции для вызова методов анонимного объекта (TAnon)
   * @param methName имя вызываемого метода
   * @param fun функция (сигнатура)
   * @param insertThisArg добавить this параметр в генерируемый метод
   * @param sendThisArg передавать this параметр в целевой метод
   * @return новый метод
   */
  def anonCallable( methName:String, fun:Fun, insertThisArg:Boolean=true, sendThisArg:Boolean=false ):CallableFn = {
    new CallableFn(
      fun.generics,
      if( insertThisArg ){
        Params((Param("this",THIS)) :: fun.parameters.toList)
      }else{
        fun.parameters
      },
      fun.returns,
      call = args => {
        if( args.isEmpty )throw new RuntimeException(s"can't call $fun")
        val self = args.head
        AnonymousObject.definitionOf(self) match {
          case Some(anonType) => anonType.methods.get(methName) match {
            case Some(funs) =>
              funs.find( f => f.sameTypes(fun) ) match {
                case Some(targetFun) => targetFun match {
                  case callFn: CallableFn =>
                    callFn.invoke(if(sendThisArg) args else args.tail)
                  case _ => throw new RuntimeException(s"method $methName($fun) not callable")
                }
                case None => throw new RuntimeException(s"method $methName($fun) not found in $anonType")
              }
            case None => throw new RuntimeException(s"method $methName not found in $anonType")
          }
          case None => throw new RuntimeException(s"undefined type of this arg ($self)")
        }
      }
    )
  }
  
  /**
   * Коллектор анонимных типов
   * @tparam A аккумулятор разных типов
   */
  trait AnonCollector[A] {
    /** Начальное значением аккумулятора */
    def initial:A
  
    /**
     * аккумуляция
     * @param acum аккумулятор
     * @param obj анонимный тип
     * @return аккумулятор
     */
    def collect(acum:A,obj:Type):A
  }
  
  /**
   * Редукция аккумулятора к анонимному типу
   * @tparam A аккумулятор разных типов
   */
  trait AnonReductor[A] {
    /**
     * Редукция аккумулятора к анонимному типу
     * @param acum аккумулятор разных типов
     * @return анонимный тип
     */
    def reduce(acum:A):TAnon
  }
  
  /**
   * Работает с элементами TAnon, иначе генерирует ошибку
   * @param optionalField опциональный generic тип для поля
   * @param optBuilder создание optional
   */
  case class AnonFieldsReductor(
                        optionalField: OptionalField,
                        optBuilder:OptionalBuilder = OptBaker()
                      ) {
    case class FieldAcum( anons:List[TAnon]=List[TAnon]() )
    object AnonCollector extends AnonCollector[FieldAcum] {
      /** Начальное значением аккумулятора */
      override def initial: FieldAcum = FieldAcum()
  
      /**
       * аккумуляция
       *
       * @param acum аккумулятор
       * @param obj  анонимный тип
       * @return аккумулятор
       */
      override def collect(acum: FieldAcum, obj: Type): FieldAcum = {
        obj match {
          case t:TAnon =>
            acum.copy( t :: acum.anons )
          case _ => throw ToasterError(s"${obj} not instance of TAnon")
        }
      }
    }
    
    object AnonReductor extends AnonReductor[FieldAcum] {
      /**
       * Редукция аккумулятора к анонимному типу
       *
       * @param acum аккумулятор разных типов
       * @return анонимный тип
       */
      override def reduce(acum: FieldAcum): TAnon = {
        TAnon(Fields(FieldsReductor(optionalField,optBuilder).reduce(FieldsCollector(acum.anons))))
      }
    }
  }
}
