package xyz.cofe.sel.cmpl.rt

/**
 * Варианты вызовов
 * @param funs вызываемые варианты
 */
class Invokables( val funs : List[Invokable] ) extends Iterable[Invokable] {
  override def iterator: Iterator[Invokable] = funs.iterator

  /** Предпочительно */
  lazy val preferred: List[Invokable] = {
    if( nonEmpty ) {
      val minDiff = map(_.overlap.difference).min;
      filter(_.overlap.difference == minDiff).toList
    }else{
      List()
    }
  }

  /** Конвертирует в набор функций */
  lazy val toFuns : Funs = new Funs(funs.map(_.fn))
}

object Invokables {
  def apply(funs: List[Invokable]): Invokables = new Invokables(funs)
  def apply(funs: Seq[Invokable]): Invokables = new Invokables(funs.toList)
}
