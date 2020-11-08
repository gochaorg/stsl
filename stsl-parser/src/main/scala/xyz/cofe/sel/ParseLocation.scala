package xyz.cofe.sel

import xyz.cofe.sel.Parser.PTR

case class ParseLocation( val begin:PTR, val end:PTR ) {
  require(begin!=null)
  require(end!=null)
}
