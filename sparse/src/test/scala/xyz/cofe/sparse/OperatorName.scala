package xyz.cofe.sparse

object OperatorName {
  sealed trait EnumVal
  case object Add extends EnumVal
  case object Sub extends EnumVal
  case object Mul extends EnumVal
  case object Div extends EnumVal
}
