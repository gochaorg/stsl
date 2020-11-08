package xyz.cofe.stsl.ast

object ASTDump {
  def dump(out: Appendable, ast: AST, level:Int):Unit = {
    require(out!=null)
    require(ast!=null)

    if( level>0 )out.append("-|"*level).append(" ")
    out.append(ast.toString).append("\n")

    ast.children().foreach( a => {
      dump(out, a, level+1)
    })
  }

  def dump(out: Appendable, ast: AST):Unit = {
    require(out!=null)
    require(ast!=null)
    dump(out, ast, 0)
  }

  def dump(ast: AST):Unit = {
    require(ast!=null)
    dump(System.out, ast, 0)
  }
}
