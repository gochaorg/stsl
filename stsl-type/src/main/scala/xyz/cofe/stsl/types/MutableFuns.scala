package xyz.cofe.stsl.types

/**
 * Мутабельный список функций
 * @param functions1 функции
 */
class MutableFuns(
                   private var functions1: List[Fun] = List()
                 ) extends Funs(List()) with Freezing {
  require(functions1!=null)

  //region "Заморозка"

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue
  def freeze:Unit = {
    freezedValue = true
  }
  //endregion

  private var functions : List[Fun] = List()
  override def funs: List[Fun] = functions

  def append( fun:Fun ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(fun!=null)
    functions = functions.filter( f => !f.sameTypes(fun) ) ++ List(fun)
  }

  functions1.filter( f => f!=null ).foreach( append )

  def += ( fun:Fun ):MutableFuns = {
    append(fun)
    this
  }

  def += ( funs:Seq[Fun] ):MutableFuns = {
    require(funs!=null)
    funs.foreach(append)
    this
  }

  /**
   * Удаление функций
   * @param filter фильтр <b>удержания</b> - указывает на элементы, которые следует оставить
   * @return SELF ссылка
   */
  def retain(filter:(Fun)=>Boolean):MutableFuns = {
    require(filter!=null)
    if( freezed )throw new IllegalStateException("freezed")
    functions = functions.filter( filter )
    this
  }

  /**
   * Удаление функций
   * @param filter фильтр - указывает на удаляемые элементы
   * @return SELF ссылка
   */
  def remove(filter:(Fun)=>Boolean):MutableFuns = {
    require(filter!=null)
    if( freezed )throw new IllegalStateException("freezed")
    functions = functions.filterNot( filter )
    this
  }
}
