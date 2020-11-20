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

  def append(field: Field):Unit = {
    require(field!=null)
    if( freezed )throw TypeError("freezed")
    fieldList = field :: fieldList.filter( f => !f.name.equals(field.name) )
  }

  override def filter(fltr:Field => Boolean):MutableFields = {
    require(fltr!=null)
    if( freezed )throw TypeError("freezed")
    fieldList = fieldList.filter(fltr)
    this
  }

  //noinspection TypeAnnotation
  def +=(field: Field) = append(field)

  def +=(fields:Seq[Field]):Unit = {
    if( freezed )throw TypeError("freezed")
    require(fields!=null)
    fields.indices.foreach(fi=>{
      val fld = fields(fi)
      require(fld!=null,s"fields[${fi}] is null")
    })
    fields.foreach(append)
  }

  def += (name:String, tip:Type):Unit = {
    if( freezed )throw TypeError("freezed")
    require(name!=null)
    require(tip!=null)
    append(Field(name,tip))
  }

  def += (field:(String,Type)):Unit = {
    if( freezed )throw TypeError("freezed")
    require(field!=null)
    require(field._1!=null)
    require(field._2!=null)
    append(Field(field._1,field._2))
  }
}
