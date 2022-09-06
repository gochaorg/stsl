package hlist

import org.junit.jupiter.api.Test
import xyz.cofe.sparse.hlist.HList._

class HListTest {

  @Test
  def test1(): Unit = {
    //val third: Head[String, Head[Boolean, Head[Int, Nil]]] = "third" :: true :: 10 :: nil
    val third = "third" :: true :: 10 :: nil

    println(third.find[String])
    println(third.find[Boolean])
    println(third.find[Int])

    //third((some)=>println(some))
    third.tail.tail(println)
    third.tail((a,b) => println(s"$a $b"))
    third.tail( a => println(s"$a"))
  }
}
