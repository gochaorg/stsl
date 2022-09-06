package xyz.cofe.sparse.hlist

sealed trait HList
object HList {
  case class Nil() extends HList {
    def ::[H](h: H): Head[H, Nil] = {
      Head(h, nil)
    }
  }

  val nil: Nil = Nil()

  case class Head[H, T](head: H, tail: T) extends HList {
    def ::[A](h: A): Head[A, Head[H, T]] = Head(h, this)
  }
}

