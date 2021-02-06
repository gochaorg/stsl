package xyz.cofe.sparse

import org.junit.jupiter.api.Test
import xyz.cofe.sparse.ParserSample.PTR

class ParserSampleTest {
  @Test
  def sample01():Unit = {
    val ast = ParserSample.parse("1 + 2 * 3 + 4")
    ast.foreach(ASTDump.dump)

//    val toks = LexerSample.tokenizer("1").filter( t => !t.isInstanceOf[WS] ).toList
//    val ptr = new PTR(0,toks)
//    val t0 = ParserSample.literal(ptr)
//    println(t0)
  }
}
