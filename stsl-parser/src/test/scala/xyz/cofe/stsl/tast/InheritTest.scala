package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.{ASTDump, Parser}
import xyz.cofe.stsl.types.TObject
import xyz.cofe.stsl.types.{Fn, Params, TObject, Type, TypeVariable}
import xyz.cofe.stsl.types.Type.{ANY, THIS}
import xyz.cofe.stsl.tast.JvmType

class InheritTest {
  class Base {
    def base(a:Int):Int = {
      println(s"Base.base( a=$a )")
      a+a
    }
    def some(a:Int):Int = {
      println(s"Base.some( a=$a )")
      a+a
    }
  }
  lazy val baseType = {
    val typeDef = new TObject("Base")
    typeDef.methods += "base" -> Fn(Params("this"->THIS, "a"->JvmType.INT),JvmType.INT).invoke[Base,Int,Int]( (self,a)=>self.base(a) )
    typeDef.methods += "some" -> Fn(Params("this"->THIS, "a"->JvmType.INT),JvmType.INT).invoke[Base,Int,Int]( (self,a)=>self.some(a) )
    typeDef
  }
  
  class Child extends Base {
    override def some(a:Int):Int = {
      println(s"Child.some( a=$a )")
      a+a+a
    }
  }
  lazy val childType = {
    val typeDef = new TObject("Child", oextend = Some(baseType))
    typeDef.methods += "some" -> Fn(Params("this"->THIS, "a"->JvmType.INT),JvmType.INT).invoke[Base,Int,Int]( (self,a)=>self.some(a) )
    typeDef
  }
  
  def runScript(script:String):Either[Throwable,(Any,Type)] = {
    try {
      val ts = new TypeScope()
      ts.implicits = JvmType.implicitConversion
      ts.imports( JvmType.types )
      ts.imports( List(baseType,childType) )
  
      val vs = new VarScope()
      vs.put("obj" -> childType -> new Child)
  
      val ast = Parser.parse(script)
      println("AST")
      ast.foreach( ASTDump.dump )
      assert( ast.isDefined )
  
      val toaster = new Toaster(ts,vs)
  
      val tast = toaster.compile(ast.get)
      assert(tast!=null)
      println("TAST")
      TASTDump.dump(tast)
  
      println("exec")
      println("-"*30)
  
      println( s"computedType = ${tast.supplierType}" )
  
      val computedValue =  tast.supplier.get()
      println( s"computedValue = $computedValue" )
      Right( (computedValue,tast.supplierType) )
    } catch {
      case err:Throwable =>
        println(err)
        err.printStackTrace(System.out)
        Left(err)
    }
  }
  
  @Test
  def callDirectDeclared:Unit = {
    println("callDirectDeclared")
    println("="*30)
    runScript("obj.some( 10 )")
  }
  
  @Test
  def callInhereted:Unit = {
    println("callInhereted")
    println("="*30)
    runScript("obj.base( 10 )")
  }
}
