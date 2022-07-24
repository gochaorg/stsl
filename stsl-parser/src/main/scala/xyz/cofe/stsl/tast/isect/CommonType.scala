package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.Type

object CommonType {
  /**
   * Поиск общего типа среди заданных
   * @param at первый тип
   * @param bt второй тип
   * @return общий тип
   */
  def commonType(at:Type, bt:Type):Option[Type] = {
    (at.assignable(bt), bt.assignable(at)) match {
      case (true, true) => Some(at)
      case (true, false) => Some(at)
      case (false, true) => Some(bt)
      case _ => None
    }
  }
}
