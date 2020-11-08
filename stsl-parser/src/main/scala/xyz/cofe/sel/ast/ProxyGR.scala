package xyz.cofe.sel.ast

import xyz.cofe.sel.Parser.{AstGR, PTR}

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
