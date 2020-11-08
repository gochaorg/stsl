package xyz.cofe.stsl.ast

import xyz.cofe.stsl.ast.Parser.PTR

case class ParseLocation( val begin:PTR, val end:PTR ) {
  require(begin!=null)
  require(end!=null)
}
