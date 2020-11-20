package xyz.cofe.stsl.types

/**
 * Мутабельная коллекция типов-параметров
 * @param parameters типы параметров
 */
class MutableGenericParams( private var parameters: List[GenericParam]=List() ) extends GenericParams(parameters) with Freezing {
  //region "Заморозка"

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue
  //noinspection UnitMethodIsParameterless
  def freeze:Unit = {
    validateUnique()
    freezedValue = true
  }
  //endregion

  override def params: List[GenericParam] = parameters

  /**
   * Проверка на уникальность параметров
   */
  protected def validateUnique():Unit = {
    params.groupBy(p=>p.name).foreach( p=>
      if(p._2.size>1)throw TypeError(s"generic parameter ${p._1} duplicate")
    )
  }

  /**
   * Добавление параметра
   * @param gp параметр
   */
  def append(gp:GenericParam):Unit = {
    if( freezed )throw new IllegalStateException("freezed")
    require(gp!=null)
    parameters = gp :: parameters.filter(p => p.name!=gp.name)
  }

  object append {
    def any(name:String):Unit = {
      require(name!=null)
      append(AnyVariant(name))
    }
    def covariant(name:String, tip:Type):Unit = {
      require(name!=null)
      require(tip!=null)
      append(CoVariant(name,tip))
    }
    def contravariant(name:String, tip:Type):Unit = {
      require(name!=null)
      require(tip!=null)
      append(ContraVariant(name,tip))
    }
  }

  /**
   * Указание параметров
   * @param newParams параметры типов-переменных
   */
  def params_=( newParams:List[GenericParam] ):Unit = {
    if( freezed )throw new IllegalStateException("freezed")
    require(newParams!=null)
    newParams.foreach( p=>{
      require(p!=null,"found null param")
    })
    newParams.groupBy(p=>p.name).foreach( p=>
      if(p._2.size>1)throw TypeError(s"generic parameter ${p._1} duplicate")
    )
    parameters = newParams
  }

  /**
   * Фильтрация параметров
   * @param fltr фильтр
   * @return SELF ссылка
   */
  override def filter( fltr:GenericParam => Boolean ):MutableGenericParams = {
    require(fltr!=null)
    if( freezed )throw new IllegalStateException("freezed")
    parameters = parameters.filter(fltr)
    this
  }
}
