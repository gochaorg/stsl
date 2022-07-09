package xyz.cofe.stsl.ast

import xyz.cofe.sparse.CToken

trait LexerDump {
  def tokens(tokens:List[CToken]):Unit
}

object LexerDump {
  object dummy extends LexerDump {
    override def tokens(tokens: List[CToken]): Unit = {}
  }
}
