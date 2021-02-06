package xyz.cofe.sparse

import xyz.cofe.sparse.OperatorName.EnumVal

object BracketName {
  sealed trait EnumVal
  case object Open extends EnumVal
  case object Close extends EnumVal
}
