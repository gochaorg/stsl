package xyz.cofe.stsl.types

/**
 * Сводная информация по всем полям
 */
trait InheritedFields extends FieldsProperty {
  self =>
  
  /**
   * кеш вычисленных значений
   */
  private var inheritedFieldsCache:Option[List[Fields]] = None
  
  /**
   * Унаследованные поля в порядке наследования
   * @return поля
   */
  def inheritedFields:List[Fields] = {
    if (inheritedFieldsCache.isDefined && self.isInstanceOf[Freezing] && self.asInstanceOf[Freezing].freezed) {
      inheritedFieldsCache.get
    } else {
      val computed = self match {
        case ext: Extendable =>
          ext.extendPath.map {
            case ownType@(fprop: FieldsProperty) =>
              Some(Fields(fprop.fields.fields, ownType))
            case _ =>
              None
          }.filter(_.isDefined).map(_.get)
        case _ => List(self.fields)
      }
      inheritedFieldsCache = Some(computed)
      computed
    }
  }
  
  /** Публичные поля в порядке наследования */
  def publicFields:List[Field] = {
    inheritedFields.flatten
  }
}
