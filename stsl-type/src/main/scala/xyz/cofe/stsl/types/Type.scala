package xyz.cofe.stsl.types

trait Type extends Assignable with Extendable with Genericable {
}

object Type {
  abstract class Primitive extends Type with Named {
    override def toString: String = name
  }
  val VOID:Type = new Primitive {
    override val name: String = "void"
  }
  val OBJECT:Type = new Primitive {
    override val name: String = "object"
  }
  val NUMBER:Type = new Primitive {
    override val name: String = "number"
    override lazy val extend: Option[Type] = Some(OBJECT)
  }
  val INT:Type = new Primitive {
    override val name: String = "int"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }
  val DOUBLE:Type = new Primitive {
    override val name: String = "double"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }
}