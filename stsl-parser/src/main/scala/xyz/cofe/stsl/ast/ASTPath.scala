package xyz.cofe.stsl.ast

class ASTPath( val reversePath:List[AST] = List() ) {
  require(reversePath!=null)

  lazy val path:List[AST] = reversePath.reverse
  lazy val last:AST = reversePath.head
  lazy val empty:Boolean = reversePath.isEmpty

  def +(ast: AST):ASTPath = {
    require(ast!=null)
    new ASTPath(ast :: reversePath)
  }
}

object ASTPath {
  def apply(ast: AST):ASTPath = {
    require(ast!=null)
    new ASTPath(List(ast))
  }
}