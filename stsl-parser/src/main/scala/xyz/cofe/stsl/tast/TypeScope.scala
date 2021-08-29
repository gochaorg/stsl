package xyz.cofe.stsl.tast

import xyz.cofe.stsl.tast.JvmType.INT
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.pset.PartialSet
import xyz.cofe.stsl.types.{CallableFn, Fun, LocatorItem, LocatorItemFunParam, Named, Obj, TObject, Type}

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
  def imports( types:Seq[ _ <: Type] ):Unit = {
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
   * Импортирует набор типов
   * @param types набор типов
   */
  def imports( types:java.lang.Iterable[_ <: Type] ):Unit = {
    require(types!=null)
    var ls : List[Type] = List()
    types.forEach( t => {
      require(t!=null)
      ls = t :: ls
    })

    imports(ls)
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

  def getImplicits():List[Fun] = implicitsInst.toList
  def setImplicits( implicitsCalls : List[CallableFn] ):Unit = {
    require(implicitsCalls!=null)
    implicitsInst = implicitsCalls
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

  def apply(name:String): Type = {
    require(name!=null)
    types.filter( t => t match {
      case n:Named => n.name == name
      case _=> false
    }).head
  }

  def get(name:String): Option[Type] = {
    require(name!=null)
    types.find {
      case n: Named => n.name == name
      case _ => false
    }
  }

  private def callType( fun:Fun, args:List[Type], thiz:Option[TObject] ):CallType = {
    var tfun = fun
    if( fun.generics.nonEmpty ){
      //println(s"DEBUG calltype() generic fun ${fun} with args ${args}")
      //val typeVarLocators = fun.typeVarFetch()
      val locators = fun.typeVarFetch()
        .map( tvf => (tvf.typeVar,LocatorItem.parse(tvf.path)) )
        .filter( _._2.isDefined )
        .map( i => (i._1, i._2.get) )
        .filter( i => i._2.isInstanceOf[LocatorItemFunParam] )
        .map( i => (i._1, i._2.asInstanceOf[LocatorItemFunParam]) )
        .groupBy( i => i._1 )
        .mapValues( l => l.map(_._2) )
        .mapValues( l => l.map( _.resolve(fun,args)) )
        .mapValues( l => l.filter(_.isDefined).map(_.get) )

      locators.foreach{ case(tv,tps)=>
        //println(s"DEBUG tv ${tv.name} to ${tps}")
      }

      val singleVariant = locators.filter{ case (tv,ls)=>ls.size==1 }.mapValues( _.head )
      val nfun = fun.typeVarBake.fn(
        singleVariant.map({case (k,v) => (k.name, v)})
      )

      //println(s"DEBUG target fun $nfun")
      tfun = nfun
    }

    new CallType( tfun,
      tfun.parameters.map( p => p.tip match {
        case Type.FN => tfun
        case THIS => thiz.getOrElse(p.tip)
        case _ => p.tip
      }).toList,
      args,
      tfun.returns match {
        case Type.FN => tfun
        case THIS => thiz.getOrElse(tfun.returns)
        case _ => tfun.returns
      }
    )
  }
  private def imports(ctypes : List[CallType]):List[CallType] = {
    ctypes.foreach( ct => (ct.actual ++ ct.expected ++ List(ct.result)).foreach( t => {
      if( !contains(t) ){
        imports(t)
      }
    }))
    ctypes
  }

  /**
   * Получение типов вызовов для метода объекта
   * @param functions функции
   * @param args ожидаемые типы аргментов
   * @return варианты типов вызовов
   */
  def callTypes( functions:Seq[Fun], args:List[Type] ):List[CallType] = {
    require(functions!=null)
    functions.foreach( f => require(f!=null) )
    require(args!=null)

    val ctypes : List[CallType] = functions.map( fun => callType(fun, args, None) ).toList
    imports(ctypes)
  }

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

    val ctypes : List[CallType] = thiz.methods.get(method).map(
      funs => funs.funs.map( fun => callType( fun, args, Some(thiz) )
      )
    ).getOrElse( List() )

    imports(ctypes)
  }

  //region callCasesCache

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
  //endregion

  /**
   * Ищет варианты как можно вызвать метод указанного объекта с указанными типами аргументов
   * @param thiz объект (класс)
   * @param method метод
   * @param args ожидаемые типы аргументов
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
      val cases = new CallCases( callTypes(thiz, method, args), this)
      callCasesCache = callCasesCache + (caseId -> cases)
      cases
    }
  }

  /**
   * Ищет варианты как можно вызвать метод указанного объекта с указанными типами аргументов
   * @param thiz объект (класс)
   * @param method метод
   * @param args ожидаемые типы аргументов
   * @return варианты вызовов
   */
  def callCases( thiz:TObject, method: String, args:java.lang.Iterable[Type] ):CallCases = {
    require(args!=null)

    var a_ls : List[Type] = List();
    args.forEach { a => a_ls = a :: a_ls }

    callCases( thiz, method, a_ls )
  }

  /**
   * Ищет варианты какую функцию можно вызвать с указанными типами аргументов
   * @param functions функции
   * @param args ожидаемые типы аргументов
   * @return варианты вызовов
   */
  def callCases( functions:Seq[Fun], args:List[Type] ):CallCases = {
    require(functions!=null)
    require(args!=null)
    new CallCases( callTypes(functions, args), this)
  }

  /**
   * Ищет варианты какую функцию можно вызвать с указанными типами аргументов
   * @param functions функции
   * @param args ожидаемые типы аргументов
   * @return варианты вызовов
   */
  def callCases( functions:java.lang.Iterable[Fun], args:java.lang.Iterable[Type] ):CallCases = {
    require(functions!=null)
    require(args!=null)

    var funs : List[Fun] = List();
    functions.forEach { f => funs = f :: funs }

    var a_ls : List[Type] = List();
    args.forEach { a => a_ls = a :: a_ls }
    a_ls = a_ls.reverse

    callCases( funs, a_ls )
  }
}
