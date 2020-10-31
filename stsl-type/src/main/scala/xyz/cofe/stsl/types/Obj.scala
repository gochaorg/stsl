package xyz.cofe.stsl.types

trait Obj extends Type with Named {
  lazy val fields : Fields = Fields()
  lazy val methods : Methods = Methods()
}
