package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.{ASTDump, Parser}

class ToasterTest {
  @Test
  def test01:Unit = {
    println("test01")
    println("="*30)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("20 + 20 / 2")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )

    val tst = new Toaster(ts)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )
    println( tast.supplier.get() )
  }
}
