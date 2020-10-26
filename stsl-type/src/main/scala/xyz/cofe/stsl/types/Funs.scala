package xyz.cofe.stsl.types

case class Funs( val funs: List[Fun] ) {
  require(funs!=null)
  funs.foreach( f => require(f!=null, "funs contains null") )

  private val matched = funs.indices.flatMap(fi => {
    ((fi + 1) until funs.length).flatMap(fj => {
      val f1 = funs(fi)
      val f2 = funs(fj)
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
}

object Funs {
  //def apply(funs: List[Fun]): Funs = new Funs(funs)
  def apply(funs: Fun*): Funs = Funs(funs.toList)
}
