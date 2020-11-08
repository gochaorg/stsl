package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.{Fun, Type}

/**
 * Рабочее пространство
 */
class Scope {
  //#region types : List[Type]

  private var types0 = Map[String,Type]()
  private var onTypesChanged : List[()=>Any] = List()
  private def onTypesChanged( f:()=>Any ):Unit={
    require(f!=null)
    onTypesChanged = f :: onTypesChanged
  }

  /**
   * Типы данных
   * @return типы
   */
  def types : Map[String,Type] = types0

  /**
   * Типы данных
   * @param tps типы
   * @return типы
   */
  def types_=( tps:Map[String,Type] ):Map[String,Type] = {
    require(tps!=null)
    types0 = tps
    onTypesChanged.foreach( _() )
    types0
  }

  //#endregion

  /**
   * Переменные
   */
  object vars {
    private var scnValue:Long = 0
    def scn:Long = scnValue

    private var variables0 : Map[String,Variable] = Map()
    def map : Map[String,Variable] = variables0
    protected def map_=( m:Map[String,Variable] ):Unit = {
      require(m!=null)
      variables0 = m
      scnValue += 1
    }

    /**
     * Проверка наличия переменной
     * @param name имя переменной
     * @return true - переменная определена
     */
    def exists( name:String ):Boolean = map.contains(name)

    /**
     * Установка или определение переменной
     * @param name имя переменной
     * @param varType тип переменной
     * @param value значение переменной
     */
    def setOrDef( name:String, varType:Type, value:Any ):Unit = {
      require(name!=null)
      require(varType!=null)

      if( exists(name) ){
        set(name,varType,value)
      }else{
        define(name,varType,value)
      }
    }

    /**
     * Определение переменной
     * @param name имя переменной
     * @param varType тип переменной
     * @param value значение переменной
     * @param readOnly true - переменная только для чтения
     * @param overridable false - переменную нельзя переопределить
     */
    def define( name:String, varType:Type, value:Any, readOnly:Boolean=false, overridable:Boolean=true ):Unit = {
      require(name!=null)
      require(varType!=null)
      val vdef = vars.map.getOrElse(name, null)
      if( vdef!=null ){
        if( vdef.readOnly ) throw new Error(s"variable $name is read only")
        if( !vdef.overridable ) throw new Error(s"variable $name not overridable")
      }
      map = map + ( name -> new Variable(value, varType, readOnly, overridable ) )
    }

    /**
     * Установка значения переменной
     * @param name имя переменной
     * @param varType тип переменной
     * @param value значение переменной
     */
    def set( name:String, varType:Type, value:Any ):Unit = {
      require(name!=null)
      require(varType!=null)

      val vdef = map.get(name)
      if( vdef.isEmpty )throw new RuntimeException(s"variable $name not defined")

      val v = vdef.get
      if( v.varType!=varType && !v.overridable )throw new RuntimeException(s"variable $name not overridable")

      v.varType = varType
      v.value = value

      scnValue += 1
    }
  }

  /**
   * Функции
   */
  object funs {
    private var functionsCache : Map[String,List[Fun]] = Map()
    private var functionsCacheVarsSCN : Long = -1
    def functions:Map[String,List[Fun]] = {
      if( vars.scn==functionsCacheVarsSCN ){
        functionsCache
      }else{
        functionsCacheVarsSCN = vars.scn
        functionsCache = vars.map.map({ case(varName, variable) =>
          if( variable.value!=null ){
            variable.value match {
              case funs1: Funs => varName -> funs1.toList
              case f:Fun => varName -> List(f)
              case _ => varName -> List()
            }
          }else{
            varName -> List()
          }
        }).filter( _._2.nonEmpty )
        functionsCache
      }
    }
    def define( name:String, fun: Fun*):Unit = {
      require(name!=null)
      require(fun!=null)
      val v = vars.map.get(name)
      if( v.isDefined ){
        val eFns = if( v.get.value!=null ) {
          v.get.value match {
            case fun1: Fun =>new Funs(List(fun1))
            case f:Funs => f
            case _ => new Funs(List())
          }
        }else{
          new Funs(List())
        }
        var nFns = eFns
        fun.foreach( f => nFns = nFns.add(f) )

        vars.set(name,Type.ARRAY,nFns)
      }else{
        vars.define(name,Type.ARRAY,new Funs(fun.toList))
      }
    }

    private var methodsCacheVarsSCN : Long = -1
    private var methodsCache : Map[Type,Map[String,List[Fun]]] = Map()
    def methodsOf( obj:Type ):Map[String,List[Fun]] = {
      require(obj!=null)

      lazy val fetch: Map[String, List[Fun]] = functions.map({case (fnName, fns) =>
          fnName -> fns.filter( fn => fn.params.nonEmpty && fn.params.head.paramType.assignable(obj) )
        }).filter({case (_,meths) => meths.nonEmpty})

      if( methodsCacheVarsSCN==vars.scn ){
        val cached = methodsCache.get(obj)
        if( cached.isDefined ){
          cached.get
        }else{
          methodsCache = methodsCache + ( obj -> fetch )
          fetch
        }
      }else{
        methodsCacheVarsSCN = vars.scn
        methodsCache = Map( obj -> fetch )
        fetch
      }
    }
  }
}

object Scope {
  def default: Scope = {
    val ws = new Scope()
    ws.types = Type.types
    PredefFun.init(ws)
    ws
  }
}
