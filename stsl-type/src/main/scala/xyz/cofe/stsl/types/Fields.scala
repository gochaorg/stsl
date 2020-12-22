package xyz.cofe.stsl.types

/**
 * Имутабельный список полей класса
 * @param fieldList поля
 * @param owner владелец, по умолчанию VOID
 */
class Fields( private val fieldList:List[Field]=List(), val owner:Type=Type.VOID ) extends Seq[Field] {
  require(fieldList!=null)
  require(owner!=null)

  /**
   * Возвращает список полей класса
   * @return список полей класса
   */
  def fields:List[Field]=fieldList

  /**
   * Проверка на отсуствие дубликатов полей/аттрибутов с одинаковыми именами
   */
  protected def validateDuplicates():Unit = {
    fields.groupBy(p=>p.name).foreach( p=>
      if(p._2.size>1)throw TypeError(s"field name ${p._1} duplicate")
    )
  }

  validateDuplicates()

  /**
   * Возвращает кол-во полей класса
   * @return кол-во полей класса
   */
  override def length: Int = fields.length

  /**
   * Возвращает итератор по полям класса
   * @return итератор по полям класса
   */
  override def iterator: Iterator[Field] = fields.iterator

  /**
   * Возвращает поле класса по его индексу
   * @param idx индекс поля
   * @return поле класса
   */
  def apply(idx:Int):Field = fields(idx)

  /**
   * Возвращает поле класса по его имени
   * @param fieldName имя поля
   * @return поле класса
   */
  def apply(fieldName:String):Field = {
    require(fieldName!=null)
    filter(_.name == fieldName).head
  }

  /**
   * Возвращает поле класса по его имени
   * @param fieldName имя поля
   * @return поле класса
   */
  def get(fieldName:String):Option[Field] = {
    require(fieldName!=null)
    filter(_.name == fieldName).headOption
  }
}

object Fields {
  def apply(fields: List[Field], owner: Type=Type.VOID): Fields = new Fields(fields, owner)
  def apply(fields: (String,Type)*): Fields = new Fields(fields.map(f=>new Field(f._1,f._2)).toList)
}
