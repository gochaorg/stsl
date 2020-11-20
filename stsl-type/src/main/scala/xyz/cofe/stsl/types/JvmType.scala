package xyz.cofe.stsl.types

import xyz.cofe.stsl.types.Type.{ANY, Primitive}

/**
 * Прикладные JVM типы данных
 */
object JvmType {
  /**
   * Прикладной тип - булево
   */
  val BOOLEAN:Type = new Primitive {
    override val name: String = "bool"
    override lazy val extend: Option[Type] = Some(ANY)
  }

  /**
   * Прикладной тип - число
   */
  val NUMBER:Type = new Primitive {
    override val name: String = "number"
    override lazy val extend: Option[Type] = Some(ANY)
  }

  /**
   * Прикладной тип - целое число 1 байт
   */
  val BYTE:Type = new Primitive {
    override val name: String = "byte"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }

  /**
   * Прикладной тип - целое число 2 байта
   */
  val SHORT:Type = new Primitive {
    override val name: String = "short"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }

  /**
   * Прикладной тип - целое число 4 байта
   */
  val INT:Type = new Primitive {
    override val name: String = "int"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }

  /**
   * Прикладной тип - целое число 8 байт
   */
  val LONG:Type = new Primitive {
    override val name: String = "long"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }

  /**
   * Прикладной тип - рациональное число 4 байта
   */
  val FLOAT:Type = new Primitive {
    override val name: String = "float"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }

  /**
   * Прикладной тип - рациональное число 8 байтов
   */
  val DOUBLE:Type = new Primitive {
    override val name: String = "double"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }
}
