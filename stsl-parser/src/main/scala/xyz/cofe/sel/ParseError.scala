package xyz.cofe.sel

import xyz.cofe.sel.Parser.PTR

class ParseError(
                  message:String,
                  cause:Throwable,
                  val locations: List[ParseLocation] = List()
                ) extends Error(message, cause)

object ParseError {
  def apply(message: String, cause: Throwable = null, locations: List[ParseLocation] = List()): ParseError = {
    require(message!=null)
    require(locations!=null)
    new ParseError(message, cause, locations)
  }
  def apply(message: String, locations: ParseLocation* ): ParseError = {
    require(message!=null)
    require(locations!=null)
    new ParseError(message, null, locations.toList)
  }
  def apply(message: String, begin: PTR, end: PTR ): ParseError = {
    require(message!=null)
    require(begin!=null)
    require(end!=null)
    new ParseError(message, null, List(ParseLocation(begin,end)))
  }
  def apply(message: String, location: PTR):ParseError = {
    require(message!=null)
    require(location!=null)
    new ParseError(message, null, List(ParseLocation(location,location)))
  }
}
