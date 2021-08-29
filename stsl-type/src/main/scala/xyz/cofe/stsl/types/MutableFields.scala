package xyz.cofe.stsl.types

/**
 * Мутабельный список полей/аттрибутов класса
 * @param fieldList поля/аттрибуты
 */
class MutableFields( private var fieldList:List[Field]=List() ) extends Fields(fieldList) with Freezing {
  require(fieldList!=null)
  fieldList.indices.foreach( fi => {
    val fld = fieldList(fi)
    require(fld!=null,s"field[${fi}] is null")
  })

  //region "Заморозка"

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue

  def freeze:Unit = {
    freezedValue = true
  }
  //endregion

  override def fields: List[Field] = fieldList

  /**
   * Добавление поля в список
   * @param field поле
   */
  def append(field: Field):Unit = {
    require(field!=null)
    if( freezed )throw TypeError("freezed")
    fieldList = field :: fieldList.filter( f => !f.name.equals(field.name) )
  }

//  override def filter(fltr:Field => Boolean):MutableFields = {
//    require(fltr!=null)
//    if( freezed )throw TypeError("freezed")
//    fieldList = fieldList.filter(fltr)
//    this
//  }

  /**
   * Удаление указанных полей
   * @param filter поле
   * @return SELF ссылка
   */
  def remove( filter:Field=>Boolean ):MutableFields = {
    if( freezed )throw TypeError("freezed")
    require(filter!=null)
    fieldList = fieldList.filterNot(filter)
    this
  }

  /**
   * Удаляет все элементы кроме указанных
   * @param filter какое поле оставить
   * @return SELF ссылка
   */
  def retain( filter:Field=>Boolean ):MutableFields = {
    if( freezed )throw TypeError("freezed")
    require(filter!=null)
    fieldList = fieldList.filter(filter)
    this
  }

  /**
   * Добавляет поле в список
   * @param field поле
   */
  //noinspection TypeAnnotation
  def +=(field: Field) = append(field)

  /**
   * Добавляет поле в список
   * @param fields список полей
   */
  def +=(fields:Seq[Field]):Unit = {
    if( freezed )throw TypeError("freezed")
    require(fields!=null)
    fields.indices.foreach(fi=>{
      val fld = fields(fi)
      require(fld!=null,s"fields[${fi}] is null")
    })
    fields.foreach(append)
  }

  /**
   * Добавление поля
   * @param name имя поля
   * @param tip тип
   */
  def += (name:String, tip:Type):Unit = {
    if( freezed )throw TypeError("freezed")
    require(name!=null)
    require(tip!=null)
    append(new Field(name,tip))
  }

  /**
   * Добавление поля
   * @param field поле
   */
  def += (field:(String,Type)):Unit = {
    if( freezed )throw TypeError("freezed")
    require(field!=null)
    require(field._1!=null)
    require(field._2!=null)
    append(new Field(field._1,field._2))
  }

  /**
   * Добавление writeable поля
   * @param field поле
   */
  def ++= (field: (((String,Type),Any=>Any),(Any,Any)=>Any)):Unit = {
    if( freezed )throw TypeError("freezed")
    require(field!=null)
    require(field._1!=null)
    require(field._1._1!=null)
    require(field._1._1._1!=null)
    require(field._1._1._2!=null)
    require(field._2!=null)
    append(new Field(field._1._1._1,field._1._1._2).writeable(field._1._2, field._2))
  }
}
