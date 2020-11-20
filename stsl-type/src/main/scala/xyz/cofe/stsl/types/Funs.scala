package xyz.cofe.stsl.types

class Funs( private val functions: List[Fun] ) extends Seq[Fun] {
  require(functions!=null)
  functions.foreach( f => require(f!=null, "funs contains null") )

  def funs: List[Fun] = functions

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

  override def length: Int = funs.length
  override def iterator: Iterator[Fun] = funs.iterator
  override def apply(idx: Int): Fun = funs.apply(idx)
}

object Funs {
  //def apply(funs: List[Fun]): Funs = new Funs(funs)
  def apply(funs: Fun*): Funs = new Funs(funs.toList)
}
