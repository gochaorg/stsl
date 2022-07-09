package xyz.cofe.stsl.ast

import org.junit.jupiter.api.Test
import xyz.cofe.sparse.Tok
import xyz.cofe.stsl.ast.Parser.PTR
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
  def objDef04():Unit = {
    val source =
      """
        |{
        | first : {
        |  name: "hello", value: 1
        | },
        | second : {
        |  name: "world", value: 2
        | }
        |}
        |""".stripMargin
  
    var indent = 0
    val tracer = new ParserTracer[Parser.PTR] {
      override def begin(name: String, ptr: PTR): Unit = {
        println(" "*indent+name+"{"+" ptr="+ptr.pointer()+" "+ptr.lookup(0))
        indent+=1
      }
      override def end(name: String, result: Option[Any]): Unit = {
        indent-=1
        println(" "*indent+"}"+name+result.map(r=>" succ="+r.toString).getOrElse(" fail") )
      }
    }
    val parser = Parser.defaultParser.copy(
      lexerDump = tokens => {
        println("tokens:")
        tokens.foreach( t => println("  "+t))
      },
      tracer = tracer
    )
    val ast = parser.parse(source)
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
}
