package xyz.cofe.stsl.ast

import xyz.cofe.sparse.{Pointer, Tok}

trait ParserTracer[PTR <: Pointer[_,_,_]] {
  def begin(name:String,ptr:PTR):Unit = {}
  def end(name:String,result:Option[Any]):Unit = {}
}

object ParserTracer {
  object dummy extends ParserTracer[Parser.PTR]
}