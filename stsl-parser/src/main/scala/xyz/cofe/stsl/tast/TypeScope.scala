package xyz.cofe.stsl.tast

import xyz.cofe.stsl.tast.JvmType.INT
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet
import xyz.cofe.stsl.types.{Fun, Obj, TObject, Type}

/**
 * Область "видимых" типов данных
 */
class TypeScope {
  //region scn

  @volatile
  private var scnValue:Long = 0

  private var listeners = List[(Long,Long)=>Any]()

  /**
   * Возвращает SCN
   */
  object scn {
    /**
     * Возвращает текущий номер изменения (состояния) SCN (Sequence Change Number)
     * @return
     */
    def value : Long = scnValue

    /**
     * Добавляет подписчика на изменения состояния
     * @param listener подписчик
     */
    def listen( listener: =>Unit ):Unit = {
      //noinspection ComparingUnrelatedTypes
      require(listener!=null)
      listeners = ((old:Long,cur:Long)=>listener) :: listeners
    }
  }

  /**
   * Увеличивает номер изменения (состояния)
   * @return предыдущий и текущий номер состояния
   */
  protected def nextScn:(Long,Long) = {
    var old : Long = 0
    var cur : Long = 0
    this.synchronized {
      old = scnValue
      scnValue += 1
      cur = scnValue
    }
    val changes = (old,cur)
    listeners.foreach( ls => ls(old,cur) )
    changes
  }
  //endregion
  //region types:Set[Type]

  private var typeSet:Set[Type] = Set()

  /**
   * Возвращает типы
   * @return типы данных/объектов
   */
  def types:Set[Type] = typeSet

  /**
   * Указывает новый набор типов
   * @param newSet набор типов
   */
  def types_=( newSet:Set[Type] ): Unit = {
    require(newSet!=null)
    typeSet = newSet
    nextScn
  }

  /**
   * Импортирует набор типов
   * @param types набор типов
   */
  def imports( types:Seq[Type] ):Unit = {
    require(types!=null)
    typeSet = types.toSet ++ typeSet
    nextScn
  }

  /**
   * Импортирует тип
   * @param tip тип
   */
  def imports( tip:Type ):Unit = {
    require(tip!=null)
    typeSet = Set(tip) ++ typeSet
    nextScn
  }

  /**
   * Проверка наличия типа
   * @param tip тип
   * @return true - есть в области видимости
   */
  def contains( tip:Type ):Boolean = {
    require(tip!=null)
    typeSet.contains(tip)
  }
  //endregion
  //region implicits:Seq[Fun]

  private var implicitsInst:Seq[Fun] = List()

  /**
   * Указывает неявные преобразования из типа в тип
   * @return неявные преобразования типа
   */
  def implicits:Seq[Fun] = implicitsInst

  /**
   * Указывает неявные преобразования из типа в тип
   * @param value неявные преобразования типа
   */
  def implicits_=( value:Seq[Fun] ):Unit = {
    require(value!=null)
    implicitsInst = value
    nextScn
  }
  //endregion
  //region graph:PartialSet[Type]

  //noinspection ConvertNullInitializerToUnderscore
  protected var graphInst : PartialSet[Type] = null;

  /**
   * Возвращает граф типов
   * @return граф типов
   */
  def graph:PartialSet[Type] = {
    if( graphInst!=null ){
      graphInst
    }else{
      graphInst = PartialSet[Type](
        types,
        (a,b) => a == b,
        (a,b) => a.assignable(b)
      )
      graphInst
    }
  }

  scn.listen { graphInst = null }
  //endregion

  /**
   * Получение типов вызовов для метода объекта
   * @param thiz объект (класс)
   * @param method метод
   * @param args ожидаемые типы аргментов
   * @return варианты типов вызовов
   */
  def callTypes( thiz:TObject, method: String, args:List[Type] ):List[CallType] = {
    require(thiz!=null)
    require(method!=null)
    require(args!=null)

    (List(thiz) ++ args).foreach( t => {
      if( !contains(t) ){
        imports(t)
      }
    })

    val ctypes : List[CallType] = thiz.methods.get(method).map(
      funs => funs.funs.map( fun =>
        new CallType( fun,
          fun.parameters.map( p => p.tip match {
            case THIS => thiz
            case _ => p.tip
          }).toList,
          args,
          fun.returns match {
            case THIS => thiz
            case _ => fun.returns
          }
        )
      )
    ).getOrElse( List() )

    ctypes.foreach( ct => (ct.actual ++ ct.expected ++ List(ct.result)).foreach( t => {
      if( !contains(t) ){
        imports(t)
      }
    }))

    ctypes
  }

  protected var typeIdSeq = 0
  protected var typeIdCache : Map[Type,Int] = Map()

  scn.listen { typeIdCache=Map(); typeIdSeq=0; }

  protected def typeId( thiz:Type ):Int = {
    if( typeIdCache.contains(thiz) ){
      this.typeIdCache(thiz)
    }else{
      this.synchronized {
        val id = typeIdSeq
        typeIdSeq += 1
        typeIdCache = typeIdCache + ( thiz -> id )
        id
      }
    }
  }
  protected def idOf( thiz:TObject, method: String, args:List[Type] ):String = {
    require(thiz!=null)
    require(method!=null)
    require(args!=null)
    val sb = new StringBuilder()
    sb.append(typeId(thiz)).append(" call ")
    sb.append(method).append("() ")
    sb.append(args.map(a => typeId(a).toString).reduce((a,b) => a+","+b))
    sb.toString()
  }

  protected var callCasesCache : Map[String,CallCases] = Map()

  scn.listen { callCasesCache=Map() }

  /**
   * Ищет варианты как можно вызвать метод указанного объекта с указанными типами аргментов
   * @param thiz объект (класс)
   * @param method метод
   * @param args ожидаемые типы аргментов
   * @return варианты вызовов
   */
  def callCases( thiz:TObject, method: String, args:List[Type] ):CallCases = {
    require(thiz!=null)
    require(method!=null)
    require(args!=null)

    val caseId = idOf(thiz, method, args)
    if( callCasesCache.contains(caseId) ){
      callCasesCache(caseId)
    }else {
      val cases = new CallCases(thiz, method, args, this)
      callCasesCache = callCasesCache + (caseId -> cases)
      cases
    }
  }
}
