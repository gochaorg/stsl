package xyz.cofe.stsl.types

/**
 * Иммутабельный список функций
 * @param functions функции
 */
class Funs( private val functions: List[Fun] ) extends Seq[Fun] {
  require(functions!=null)
  functions.foreach( f => require(f!=null, "funs contains null") )

  /**
   * Возвращает список функций
   * @return список функций
   */
  def funs: List[Fun] = functions

  //region Проверка чтоб в одном списке функций не попадались функции с одинаковыми сигнатурами

  private val matched = functions.indices.flatMap(fi => {
    ((fi + 1) until functions.length).flatMap(fj => {
      val f1 = functions(fi)
      val f2 = functions(fj)
      if (f1.sameTypes(f2)) {
        List((fi, f1, fj, f2))
      } else {
        List()
      }
    })
  })

  if( matched.nonEmpty ){
    val matchedStr = matched.map({case(fi,fn1, fj, fn2)=>
      s"fun[$fi](=$fn1) params type match with fun[$fj](=$fn2)"
    }).reduce((a,b)=>a+"\n"+b)
    throw TypeError("has duplicate type params in functons:\n"+matchedStr)
  }
  //endregion

  //region Методы Seq[Fun]

  /**
   * Кол-во элементов в списке
   * @return Кол-во элементов
   */
  override def length: Int = funs.length

  /**
   * Возвращает итератор по функциям
   * @return итератор по функциям
   */
  override def iterator: Iterator[Fun] = funs.iterator

  /**
   * Возвращает функцию по ее индексу в списке
   * @param idx индекс функции
   * @return функция
   */
  override def apply(idx: Int): Fun = funs.apply(idx)
  //endregion
}

object Funs {
  //def apply(funs: List[Fun]): Funs = new Funs(funs)
  def apply(funs: Fun*): Funs = new Funs(funs.toList)
}
