package xyz.cofe.stsl.types

/**
 * Коллекция типов-параметров
 * @param parameters типы параметров
 */
class GenericParams( private val parameters: List[GenericParam]=List() ) extends Seq[GenericParam] {
  require(parameters!=null)
  parameters.groupBy(p=>p.name).foreach( p=>
    if(p._2.size>1)throw TypeError(s"generic parameter ${p._1} duplicate")
  )

  /**
   * Список типов - параметров
   * @return
   */
  def params:List[GenericParam] = parameters;

  /**
   * Проверка на допустимость присвоения
   * @param genericParams присваемое значение
   * @return true - допускается присвоение
   */
  def assignable( genericParams: GenericParams ):Boolean = {
    require(genericParams!=null)
    if( genericParams.params.length!=params.length ){
      false
    }else{
      if( params.length<=0  ){
        true
      }else{
        params.indices.map( pi =>
          params(pi).assignable(genericParams.params(pi))
        ).reduce((a,b)=>a && b)
      }
    }
  }

  /**
   * Сравнение типов параметров на эквивалетность
   * @param paramz типы-параметры
   * @return true - параметры идентичны, false - параметры различаются
   */
  def sameTypes(paramz:GenericParams):Boolean = {
    require(paramz!=null)
    if( paramz.params.length!=params.length ){
      false
    }else{
      if(params.isEmpty){
        true
      }else{
        params.indices.map(pi=> {
          val gp1:GenericParam = params(pi)
          val gp2:GenericParam = paramz.params(pi)
          gp1.sameType(gp2)
        }).reduce((a,b)=>a&&b)
      }
    }
  }

  /**
   * Возвращает текстовое представление параметров
   * @return текстовое представление
   */
  override def toString: String = if( params.isEmpty ) "" else "["+params.map(_.toString).reduce((a,b)=>a+","+b)+"]"

  /**
   * Возвращает кол-во параметров
   * @return кол-во параметров
   */
  override def length: Int = params.length

  /**
   * Возвращает итератор по списку
   * @return итератор по списку
   */
  override def iterator: Iterator[GenericParam] = params.iterator

  /**
   * Возвращает параметр по индексу
   * @param idx индекс
   * @return параметр
   */
  override def apply(idx: Int): GenericParam = params(idx)

  /**
   * Возвращает параметр по имени
   * @param paramName имя параметра
   * @return параметр
   */
  def apply(paramName:String):GenericParam = {
    require(paramName!=null)
    filter(_.name == paramName).head
  }

  /**
   * Возвращает параметр по имени
   * @param paramName имя параметра
   * @return параметр
   */
  def get(paramName:String):Option[GenericParam] = {
    require(paramName!=null)
    filter(_.name == paramName).headOption
  }

}

object GenericParams {
  def apply(params: GenericParam*): GenericParams = new GenericParams(params.toList)
}