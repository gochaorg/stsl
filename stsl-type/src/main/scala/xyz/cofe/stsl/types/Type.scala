package xyz.cofe.stsl.types

/**
 * Описывает тип данных
 */
trait Type extends Assignable with Extendable with Genericable {
}

/**
 * Предопределенные типы
 */
object Type {
  //TODO убрать отсюда в сторону где примитивные типы
  /**
   * Системный, именнованый примитив
   */
  abstract class Primitive extends Type with Named {
    override type GENERICS = GenericParams
    val generics = GenericParams()
    //override def generics: GenericParams =
    override def toString: String = name
  }

  /**
   * Системный, пустой тип
   */
  val VOID:Type = new Primitive {
    override val name: String = "void"
  }

  /**
   * Системный, "любой" тип
   */
  val ANY:Type = new Primitive {
    override val name: String = "any"
  }

  /**
   * Системный, функция
   */
  val FN:Type = new Type {
    override type GENERICS = GenericParams
    val generics = GenericParams()

    override lazy val extend: Option[Type] = Some(ANY)
    override def toString: String = "fn"
  }

  /**
   * Системный, ссылка на собственный объект (this)
   */
  val THIS:Type = new Type with Named {
    override type GENERICS = GenericParams
    val generics = GenericParams()

    override val name: String = "THIS"
    override def toString: String = "THIS"
  }
}