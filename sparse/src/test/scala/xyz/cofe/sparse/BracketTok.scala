package xyz.cofe.sparse

class BracketTok(begin:CharPointer
                 , end:CharPointer
                 , name:BracketName.EnumVal )
  extends CToken(begin,end) {
  override def toString: String = s"BracketTok(begin=$begin,end=$end,name=$name)"
}
