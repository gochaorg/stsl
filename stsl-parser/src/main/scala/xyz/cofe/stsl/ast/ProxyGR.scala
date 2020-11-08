package xyz.cofe.stsl.ast

import Parser.{AstGR, PTR}

class ProxyGR extends AstGR {
  var target : AstGR = null;
  override def apply(ptr: PTR): Option[AST] = {
    val trgt = target
    if( trgt!=null && ptr!=null ){
      trgt(ptr)
    }else{
      None
    }
  }
}
