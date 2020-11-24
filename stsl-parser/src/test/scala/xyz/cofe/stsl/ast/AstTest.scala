package xyz.cofe.stsl.ast

import org.junit.jupiter.api.Test

object AstTest {
  //noinspection TypeAnnotation
  val delegate = (ast:AST) => ast.isInstanceOf[DelegateAST]

  //noinspection TypeAnnotation
  val binary = (ast:AST) => ast.isInstanceOf[BinaryAST]

  //noinspection TypeAnnotation
  val literal = (ast:AST) => ast.isInstanceOf[LiteralAST]

  //noinspection TypeAnnotation
  val identifier = (ast:AST) => ast.isInstanceOf[IdentifierAST]

  //noinspection TypeAnnotation
  val ternary = (ast:AST) => ast.isInstanceOf[TernaryAST]

  //noinspection TypeAnnotation
  val property = (ast:AST) => ast.isInstanceOf[PropertyAST]

  //noinspection TypeAnnotation
  val call = (ast:AST) => ast.isInstanceOf[CallAST]

  //noinspection TypeAnnotation
  val lamda = (ast:AST) => ast.isInstanceOf[LambdaAST]

  //noinspection TypeAnnotation
  val param = (ast:AST) => ast.isInstanceOf[ParamAST]

  //noinspection TypeAnnotation
  val typename = (ast:AST) => ast.isInstanceOf[TypeNameAST]

  def test(ast: AST, nodes:(AST=>Boolean)*):Unit = {
    require(ast!=null)
    require(nodes!=null)

    val ftree = ast.tree.map(_.last).toList
    assert(ftree.size==nodes.size)
    nodes.indices.foreach( ni => {
      assert( nodes(ni).apply(ftree(ni)) )
    })
  }

  def test(ast:Option[AST], nodes:(AST=>Boolean)*):Unit = {
    require(ast!=null)
    assert(ast.isDefined)
    test(ast.get, nodes: _*)
  }
}
