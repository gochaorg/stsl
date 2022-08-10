package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.AST

trait ToasterTracer {
  def begin(ast:AST):Unit
  def endSucc(ast:AST,result:TAST):Unit
  def endFail(ast:AST,result:ToasterError):Unit
}

object ToasterTracer {
  object dummy extends ToasterTracer {
    override def begin(ast: AST): Unit = {}
    override def endSucc(ast: AST, result: TAST): Unit = {}
    override def endFail(ast: AST, result: ToasterError): Unit = {}
  }
}