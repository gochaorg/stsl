package xyz.cofe.stsl.ast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.tast.{CallStack, StackedArgumentAST}

class ParserTest {
  import AstTest._

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
  def testDelegateAST(): Unit ={
    val ast = Parser.parse("( 123 - 234 ) * 2")
    ast.foreach( ASTDump.dump )
    test(ast, binary, delegate, binary, literal, literal, literal)
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
  def objDef01():Unit = {
    var ast = Parser.parse("{ k1: 1, k2: 2 }")
    ast.foreach( ASTDump.dump )

    ast = Parser.parse("{k1:\"abc\",k2:2}")
    ast.foreach( ASTDump.dump )
  }

  @Test
  def objDef02():Unit = {
    var ast = Parser.parse("{ }")
    ast.foreach( ASTDump.dump )
  }

  @Test
  def objDef03():Unit = {
    val ast = Parser.parse("{}")
    ast.foreach( ASTDump.dump )
  }
  
  @Test
  def arrayDef01():Unit = {
    val parser = Parser.defaultParser.copy(
      arraySupport = true
    )
    val ast = parser.parse("[ 1, 2, 3 ]" )
    ast.foreach( ASTDump.dump )
  }
  
  @Test
  def arrayDef02():Unit = {
    val parser = Parser.defaultParser.copy(
      arraySupport = true
    )
    val ast = parser.parse("[ 1, 2, 3, ]" )
    ast.foreach( ASTDump.dump )
  }

//  @Test
//  def lmbda02replace(): Unit ={
//    println( "lmbda02replace()" )
//    val astOpt = Parser.parse("() => a+b")
//
//    println("before")
//    astOpt.foreach( ASTDump.dump )
//
//    test(astOpt,
//      lamda,
//      binary, identifier, identifier
//    )
//
//    var ast : AST = astOpt.get
//    val ids = astOpt.get.tree.map(_.last).filter(_.isInstanceOf[IdentifierAST]).map(_.asInstanceOf[IdentifierAST]).toList
//    if( ids.nonEmpty ){
//      val from = ids.head
//      val call = new CallStack
//      val to = StackedArgumentAST(call,from, xyz.cofe.sel.types.Type.OBJECT)
//      println(s"replacing from $from to $to")
//
//      var rast = ast.replace(from,to)
//      println("after replace")
//
//      ASTDump.dump(rast)
//    }
//  }
}
