package xyz.cofe.stsl.types

trait Extendable extends Assignable {
  lazy val extend : Option[Type] = None
  override def assignable(t: Type): Boolean = {
    require(t!=null)
    if( this==t ){
      true
    }else{
      var x = t
      var matched = false
      while( x!=null && !matched ){
        x = x match {
          case cov:CoVariant => cov.tip
          case _ => x
        }
        matched = this==x
        x = x.extend.orNull
      }
      matched
    }
  }
}
