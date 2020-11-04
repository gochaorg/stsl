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
  val ANY:Type = new Primitive {
    override val name: String = "any"
  }
  val NUMBER:Type = new Primitive {
    override val name: String = "number"
    override lazy val extend: Option[Type] = Some(ANY)
  }
  val INT:Type = new Primitive {
    override val name: String = "int"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }
  val DOUBLE:Type = new Primitive {
    override val name: String = "double"
    override lazy val extend: Option[Type] = Some(NUMBER)
  }
  val FN:Type = new Type {
    override lazy val extend: Option[Type] = Some(ANY)
    override def toString: String = "fn"
  }
  val THIS:Type = new Type with Named {
    override val name: String = "THIS"
    override def toString: String = "THIS"
  }
}