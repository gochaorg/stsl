package xyz.cofe.stsl.types

case class GenericInstance( recipe:Map[String,Type]=Map(), source:Type ) extends Type {
}
