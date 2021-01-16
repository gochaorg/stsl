package xyz.cofe.sparse

class DigitToken( begin:CharPointer
                  , end:CharPointer
                  , val value:Int )
  extends CToken(begin,end)

object DigitToken {
  def apply( begin:CharPointer, end:CharPointer, value:Int ) = new DigitToken(begin,end,value)
//  def apply( begin:CharPointer, end:CharPointer, digits:Seq[DigitToken], base:Int = 10 ):DigitToken = {
//    val dgts = digits.reverse.zipWithIndex.map( { case (d,idx) =>
//      if( idx==0 ) d.value else {
//        var b = base
//        (0 until idx-1).foreach( _ => {
//          b = b * base
//        })
//        d.value * b
//      }
//    })
//    sum = dgts.sum
//    ???
//  }
}