package xyz.cofe.stsl.ast

import xyz.cofe.stsl.ast.Parser.PTR

/**
 * Указание на кусок исходника
 * @param begin начало в тексте
 * @param end конец в тексте
 */
case class ParseLocation( val begin:PTR, val end:PTR ) {
  require(begin!=null)
  require(end!=null)
}
