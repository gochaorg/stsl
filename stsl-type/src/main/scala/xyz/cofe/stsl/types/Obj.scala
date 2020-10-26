package xyz.cofe.stsl.types

trait Obj extends Type {
  lazy val fields : Fields = Fields()
  lazy val methods : Methods = Methods()
}
