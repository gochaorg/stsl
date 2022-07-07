package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Field, GenericInstance, InheritedFields, Named, TObject, Type, WriteableField}

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
}
