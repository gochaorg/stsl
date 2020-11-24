package xyz.cofe.sel.cmpl.rt

import xyz.cofe.stsl.ast.AST

@Deprecated
class CompileError(val message:String, val cause:Throwable, val ast:List[AST] = List()) extends Error(message,cause) {
}

@Deprecated
object CompileError {
  def apply(message: String, cause: Throwable): CompileError = new CompileError(message, cause)
  def apply(message: String): CompileError = new CompileError(message, null)
  def apply(message: String, ast:AST* ): CompileError = new CompileError(message, null, ast.toList)
}
