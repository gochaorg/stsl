package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.ast.{AST, ASTDump, BinaryAST, CallAST, IdentifierAST, LambdaAST, LiteralAST, ParamAST, PropertyAST, TernaryAST, TypeNameAST}
import xyz.cofe.sel.cmpl.rt.{CallStack, StackedArgumentAST}

class ParserTest {
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

  @Test
  def test01(): Unit ={
    val ast = Parser.parse("123 + 234 * 345")
    ast.foreach( ASTDump.dump )
    test(ast, binary, literal, binary, literal, literal)
  }

  @Test
  def test02(): Unit ={
    val ast = Parser.parse("123 / 234 - 345")
    ast.foreach( ASTDump.dump )
    test(ast, binary, binary, literal, literal, literal)
  }

  @Test
  def test03(): Unit ={
    val ast = Parser.parse("123 - 234 / 345")
    ast.foreach( ASTDump.dump )
    test(ast, binary, literal, binary, literal, literal)
  }

  @Test
  def test04(): Unit ={
    val ast = Parser.parse("a - b / c")
    ast.foreach( ASTDump.dump )
    test(ast, binary, identifier, binary, identifier, identifier)
  }

  @Test
  def cmp01(): Unit ={
    val ast = Parser.parse("a < b")
    ast.foreach( ASTDump.dump )
    test(ast, binary, identifier, identifier)
  }

  @Test
  def if01(): Unit ={
    val ast = Parser.parse("a ? b : c")
    ast.foreach( ASTDump.dump )
    test(ast, ternary, identifier, identifier, identifier)
  }

  @Test
  def prop01(): Unit ={
    val ast = Parser.parse("x.a + x.b.c")
    ast.foreach( ASTDump.dump )
    test(ast, binary, property, identifier, property, property, identifier)
  }

  @Test
  def call01(): Unit ={
    val ast = Parser.parse("a( 10, 12 ) + b.a( 1, 2, 3 )")
    ast.foreach( ASTDump.dump )
    test(ast, binary,
      call, identifier, literal, literal,
      call, property, identifier, literal, literal, literal
    )
  }

  @Test
  def call02(): Unit ={
    val ast = Parser.parse("obj.m()")
    ast.foreach( ASTDump.dump )
    test(ast, call, property, identifier)
  }

  @Test
  def lmbda01(): Unit ={
    val ast = Parser.parse("a:int , b:int => a+b")
    ast.foreach( ASTDump.dump )
    test(ast,
      lamda,
      param, identifier, typename,
      param, identifier, typename,
      binary, identifier, identifier
    )
  }

  @Test
  def lmbda01r(): Unit ={
    val ast = Parser.parse("a:int , b:int , r :: int => a+b")
    ast.foreach( ASTDump.dump )
    test(ast,
      lamda,
      param, identifier, typename,
      param, identifier, typename,
      param, identifier, typename,
      binary, identifier, identifier
    )
  }

  @Test
  def lmbda02(): Unit ={
    val ast = Parser.parse("() => a+b")
    ast.foreach( ASTDump.dump )
    test(ast,
      lamda,
      binary, identifier, identifier
    )
  }

  @Test
  def lmbda02replace(): Unit ={
    println( "lmbda02replace()" )
    val astOpt = Parser.parse("() => a+b")

    println("before")
    astOpt.foreach( ASTDump.dump )

    test(astOpt,
      lamda,
      binary, identifier, identifier
    )

    var ast : AST = astOpt.get
    val ids = astOpt.get.tree.map(_.last).filter(_.isInstanceOf[IdentifierAST]).map(_.asInstanceOf[IdentifierAST]).toList
    if( ids.nonEmpty ){
      val from = ids.head
      val call = new CallStack
      val to = StackedArgumentAST(call,from, xyz.cofe.sel.types.Type.OBJECT)
      println(s"replacing from $from to $to")

      var rast = ast.replace(from,to)
      println("after replace")

      ASTDump.dump(rast)
    }
  }
}
