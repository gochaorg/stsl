package xyz.cofe.stsl.ast

import org.junit.jupiter.api.Test

class AstTest {
  @Test
  def test01(): Unit ={
    val astRoot = Parser.parse("123 + 234d * 345l")
    assert(astRoot.isDefined)

    println("ast")
    ASTDump.dump(astRoot.get)

    println("....."*4)
    val literalASTs = astRoot.get.tree.map(_.last).filter( a => a.isInstanceOf[LiteralAST] ).map( _.asInstanceOf[LiteralAST] )
    literalASTs.foreach(println)
  }
}
