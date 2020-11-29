package xyz.cofe.stsl.types

/**
 * Мутабельный список методов объекта
 * @param functions карта методов
 */
class MutableMethods( private var functions:Map[String,Funs]=Map() ) extends Methods() with Freezing {
  require(functions!=null)

  //region "Заморозка"

  private var freezedValue : Boolean = false
  def freezed : Boolean = freezedValue

  def freeze:Unit = {
    freezedValue = true
    funs.values.filter(_.isInstanceOf[Freezing]).foreach(_.asInstanceOf[Freezing].freeze)
  }
  //endregion

  override def funs: Map[String, Funs] = functions

  //region мутация списка методов
  /**
   * Добавление метода
   * @param name имя метода
   * @param fun реализация/сигнатура метода
   */
  def append( name:String, fun:Fun ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(name!=null)
    require(fun!=null)

    val funz : Funs = functions.getOrElse(name, new MutableFuns())
    val mfunz : MutableFuns = funz match {
      case m:MutableFuns => m
      case _ =>
        val mf = new MutableFuns( funz.funs )
        mf
    }

    mfunz.append(fun)
    functions = functions + (name -> mfunz)
  }

  /**
   * Добавление метода
   * @param method имя и реализация/сигнатура метода
   */
  def += ( method:(String,Fun) ):Unit = {
    require(method!=null)
    append(method._1, method._2)
  }

  /**
   * Добавление методов
   * @param methods имя и реализация/сигнатура методов
   */
  def += ( methods:(String,Fun)* ):Unit = {
    if( freezed )throw TypeError("freezed")
    require(methods!=null)
    methods.foreach( m => append(m._1, m._2) )
  }

  /**
   * Удаление методов
   * @param filter фильтр <b>удержания</b> - указывает на элементы, которые следует оставить
   * @return SELF ссылка
   */
  def retain( filter:(String,Fun)=>Boolean ):MutableMethods = {
    if( freezed )throw TypeError("freezed")
    functions = functions.map({case(name,funz)=>
      val mfunz : MutableFuns = funz match {
        case m:MutableFuns => m
        case _ =>
          val mf = new MutableFuns( funz.funs )
          mf
      }
      mfunz.filter(filter(name,_))
      name -> mfunz
    })
    functions = functions.filter({case(name,funz)=>funz.funs.nonEmpty})
    this
  }

  /**
   * Удаление методов
   * @param filter фильтр - указывает на удаляемые элементы
   * @return SELF ссылка
   */
  def remove( filter:(String,Fun)=>Boolean ):MutableMethods = {
    if( freezed )throw TypeError("freezed")
    require(filter!=null)
    retain( (n,f) => !filter(n,f) )
  }
  //endregion
}
