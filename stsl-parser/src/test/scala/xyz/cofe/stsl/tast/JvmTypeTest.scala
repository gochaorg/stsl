package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.types.TypeDescriber

class JvmTypeTest {
  @Test
  def desc01(): Unit ={
    println(TypeDescriber.describe(JvmType.BOOLEAN))
  }
}
