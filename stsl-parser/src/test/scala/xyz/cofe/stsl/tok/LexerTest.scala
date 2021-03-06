package xyz.cofe.stsl.tok

import org.junit.jupiter.api.Test
import xyz.cofe.sparse.Tokenizer
import xyz.cofe.stsl.tok.Lexer._

class LexerTest {
  @Test
  def test01(): Unit ={
    val toks = Tokenizer.tokens(
      "  \"aa\"  \"b\\\\c\\\"d\"='hello'^^1234 + 0xffL-12.34 .5 10. 14d abc /* rr */ xyz",
      List(ws,string,number,comment,identifier,operator), null).toList

    toks.indices.foreach(i=>println(s"${i} ${toks(i)}"))
  }

  @Test
  def test02(): Unit ={
    val toks = Lexer.tokenizer("b / c")
    toks.foreach(println)
  }

  @Test
  def test03(): Unit ={
    val toks = Lexer.tokenizer("10 12f 13d 14w 12.3w")
    toks.foreach(println)
  }

  @Test
  def unicode01():Unit = {
    val toks = Lexer.tokenizer("10 'x\\'y' 'x\\u005Dy' \"x\\u005Dy\" ")
    toks.foreach(println)
  }
}
