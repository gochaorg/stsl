package xyz.cofe.stsl.types

trait Assignable {
  def assignable( t:Type ):Boolean = {
    this==t
  }
}
