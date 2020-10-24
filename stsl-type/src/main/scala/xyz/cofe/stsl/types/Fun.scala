package xyz.cofe.stsl.types

trait Fun extends Type {
  lazy val parameters:Params = Params()
  lazy val returns:Type = Type.VOID
}
