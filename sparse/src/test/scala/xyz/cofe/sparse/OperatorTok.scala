package xyz.cofe.sparse

class OperatorTok( begin:CharPointer
                  , end:CharPointer
                  , val name:OperatorName.EnumVal )
  extends CToken(begin,end) {
  override def toString: String = s"OperatorTok(begin=$begin,end=$end,name=$name)"
}