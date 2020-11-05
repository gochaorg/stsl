package xyz.cofe.scl

class SclTest {
  trait T[A] {
    def call(a:A):A
  }
  class C1 extends T[Int] {
    override def call(a: Int): Int = a
  }
}
