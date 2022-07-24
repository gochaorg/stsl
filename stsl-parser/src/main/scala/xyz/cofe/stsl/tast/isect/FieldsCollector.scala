package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.{Field, InheritedFields}

/**
 * Коллектор полей (field) объектов
 *
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
