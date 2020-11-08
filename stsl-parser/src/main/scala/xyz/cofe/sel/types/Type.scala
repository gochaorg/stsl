package xyz.cofe.sel.types

/**
 * Определение типа
 */
trait Type extends Assignable {
  /**
   * Имя типа
   */
  val name: String

  /**
   * Расширяет тип
   */
  val extend: Option[Type]

  /**
   * Путь от корневого типа к текущему
   */
  lazy val extendPath: List[Type] = {
    var ls: List[Type] = List(this)
    while (ls.head.extend.isDefined) {
      ls = ls.head.extend.get :: ls
    }
    ls
  }

  /**
   * Проверка что переменная может содержать указанный тип
   *
   * @param t тип
   * @return true - когда переменная может содержать тип данных t
   */
  def assignable(t: Type): Boolean = {
    require(t != null)
    if( genericAssignable(t) ) {
      if (this == t) {
        true
      } else {
        t.extendPath.contains(this)
      }
    } else false
  }

  protected def genericAssignable( t:Type ):Boolean = {
    require(t!=null)
    val genCountMatch = generics.size == t.generics.size
    if (genCountMatch) {
      val asgnSeq = generics.indices.map(gi => generics(gi).assignable(t.generics(gi)))
      if (asgnSeq.nonEmpty) asgnSeq.reduce((a, b) => a && b) else true
    } else {
      false
    }
  }

  /**
   * Возвращает дистанцию между типами
   *
   * @param t тип
   * @return 0 - одинаковый тип; <br>
   *         1 - this ковариантент t, где t - прямой дочерний тип (сын) <br>
   *         2 - this ковариантент t, где t - дочерний тип второго порядка (внук) <br>
   *         N+ - this ковариантент t, где t - дочерний тип N+ порядка <br>
   *         -1 - this контр-ковариантент t, где t - прямой родительский тип (отец) <br>
   *         -2 - this контр-ковариантент t, где t - родительский тип второго порядка (дед) <br>
   *         N- - this контр-ковариантент t, где t - родительский тип N- порядка <br>
   */
  def assignableDistance(t: Type): Option[Int] = {
    require(t != null)
    if (this == t) {
      Some(0)
    } else {
      val fnd = t.extendPath.zipWithIndex.find { case (ft, idx) => ft == this }
      if (fnd.isDefined) {
        val fndIdx = fnd.get._2
        Some((t.extendPath.length - 1) - fndIdx)
      } else {
        if (t.assignable(this)) {
          Some(-1 * t.assignableDistance(this).get)
        } else {
          None
        }
      }
    }
  }

  /**
   * Свойства объекта
   * @return свойства
   */
  lazy val properties: Properties = Properties.empty

  /**
   * Параметры типа
   * @return параметры типы
   */
  lazy val generics: List[GenericParam] = GenericParam.empty

  /**
   * Методы объекта
   */
  lazy val methods: Methods = new Methods()

  override def toString: String = {
    val sb = new StringBuilder
    sb.append( s"${name}" )
    if( generics!=null && generics.nonEmpty ){
      val paramDesc = generics.zipWithIndex.map( {case (gp,pi) =>
        val str = s"${gp.name}${
          gp match {
            case inv:InVariant => ":"+inv.genericType
            case cov:CoVariant => ":"+cov.genericType
            case cnt:ContraVariant => ":"+cnt.genericType
            case _ => ""
          }
        }${gp match {
          case _:InVariant => ""
          case _:CoVariant => "+"
          case _:ContraVariant => "-"
          case _ => ""
        }}"
        if( pi>0 ) ","+str else str
      })
      sb.append("[")
      sb.append( paramDesc.reduce((a,b)=>a+b) )
      sb.append("]")
    }
    sb.toString()
  }
}

/**
 * Предопределенные типы
 */
object Type {
  val OBJECT = new BasicType("object")
  val THIS = new BasicType("THIS")

  val NUMBER = new BasicType("number", Some(OBJECT))
  val BYTE = new BasicType("byte", Some(NUMBER))
  val SHORT = new BasicType("short", Some(NUMBER))
  val INT = new BasicType("int", Some(NUMBER))
  val LONG = new BasicType("long", Some(NUMBER))
  val FLOAT = new BasicType("float", Some(NUMBER))
  val DOUBLE = new BasicType("double", Some(NUMBER))
  val BIGINT = new BasicType("bigInt", Some(NUMBER))
  val DECIMAL = new BasicType("decimal", Some(NUMBER))
  val STRING = new BasicType("string", Some(OBJECT))
  val ARRAY = new BasicType("array", Some(OBJECT))
  val BOOL = new BasicType("bool", Some(OBJECT))
  val VOID = new BasicType("void", Some(OBJECT))
  val FN = new BasicType("fn", Some(OBJECT))

  lazy val types: Map[String, Type] = Map(
    OBJECT.name -> OBJECT,
    NUMBER.name -> NUMBER,
    BYTE.name -> BYTE, SHORT.name -> SHORT, INT.name -> INT, LONG.name -> LONG,
    FLOAT.name -> FLOAT, DOUBLE.name -> DOUBLE,
    BIGINT.name -> BIGINT, DECIMAL.name -> DECIMAL,
    STRING.name -> STRING,
    ARRAY.name -> ARRAY,
    BOOL.name -> BOOL,
    VOID.name -> VOID,
    FN.name -> FN,
  )
}