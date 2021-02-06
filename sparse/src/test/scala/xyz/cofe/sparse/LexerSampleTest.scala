package xyz.cofe.sparse

import org.junit.jupiter.api.Test

class LexerSampleTest {
  @Test
  def sample01():Unit = {
    val ptr = CharPointer.of("1234")
    println(LexerSample.intNumber(ptr))
  }

  @Test
  def sample02():Unit = {
    LexerSample.tokenizer("12 * ( / + - )").toList.foreach(println)
  }
}
