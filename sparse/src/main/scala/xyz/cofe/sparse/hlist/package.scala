package xyz.cofe.sparse
import xyz.cofe.sparse.hlist.HList._

package object hlist {
  implicit def baseFetch[W, T]: Fetch[W, Head[W, T]] = hl => hl.head
  implicit def inductFetch[W, H, T](implicit f: Fetch[W, T]): Fetch[W, Head[H, T]] = hl => f.fetch(hl.tail)
  implicit class Find[HL <: HList](hl: HL) {
    def find[W](implicit fetch: Fetch[W, HL]): W = fetch.fetch(hl)
  }

  implicit class Apply1[H,T]( hl:Head[H,T] ){
    def apply[Z](f:H=>Z):Z = f(hl.head)
  }
  implicit class Apply2[A,B,T]( hl:Head[A,Head[B,T]] ){
    def apply[Z](f:(A)=>Z):Z = f( hl.head)
    def apply[Z](f:(A,B)=>Z):Z =
      f( hl.head,
         hl.tail.head
      )
  }
  implicit class Apply3[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply4[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply5[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply6[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply7[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply8[A,B,C,T]( hl:Head[A,Head[B,Head[C,T]]] ){
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head
      )
  }
  implicit class Apply9[A,B,C,D,E,F,G,H,I,T]( hl:Head[A,Head[B,Head[C,Head[D,Head[E,Head[F,Head[G,Head[H,Head[I,T]]]]]]]]] ){
    def apply[Z](f:(A)=>Z):Z =
      f( hl.head,
      )
    def apply[Z](f:(A,B)=>Z):Z =
      f( hl.head,
         hl.tail.head,
      )
    def apply[Z](f:(A,B,C)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D,E)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
         hl.tail.tail.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D,E,F)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
         hl.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D,E,F,G)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
         hl.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D,E,F,G,H)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
         hl.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.tail.head,
      )
    def apply[Z](f:(A,B,C,D,E,F,G,H,I)=>Z):Z =
      f( hl.head,
         hl.tail.head,
         hl.tail.tail.head,
         hl.tail.tail.tail.head,
         hl.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.tail.head,
         hl.tail.tail.tail.tail.tail.tail.tail.tail.head
      )
  }
}
