package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.tast.isect.CommonType.commonType
import xyz.cofe.stsl.types.{Type, WriteableField}

/**
 * Редукция коллектора полей до списка совместимых полей
 *
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
