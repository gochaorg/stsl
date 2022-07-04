package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.{ASTDump, Parser}
import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.{AnyVariant, Fn, GenericParams, Named, Params, TObject, Type, TypeDescriber, TypeVariable}

class ArrayTest {
  val javaListType: TObject = {
    val openType = TObject("List").extend(Type.ANY).build
    
    openType.generics.append.any("A")
    
    openType.fields ++= "size" -> JvmType.INT ->
      ((inst:Any) => inst.asInstanceOf[java.util.List[Any]].size()) ->
      ((inst:Any, newValue:Any) => throw new RuntimeException("immutable"))
    
    openType.methods += "get" -> Fn(
      Params(
        "this" -> Type.THIS,
        "index" -> JvmType.INT
      ),
      TypeVariable("A",THIS)
    )
    
    openType.freeze
    openType
  }
  
  @Test
  def emptyArray():Unit = {
    val parser = new Parser(
      arraySupport = true
    )
    
    val vs = new VarScope()
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports( JvmType.types )
    
    val toaster = new Toaster(
      typeScope = ts,
      varScope = vs,
      arrayCompiler =
        ArrayCompiler.FirstAssignType[java.util.List[Any],TObject](
          typeConstruct = itemType => {
            val listType = javaListType.typeVarBake.thiz("A" -> itemType)
            itemType match {
              case n:Named => listType.withName(s"List_${n.name}")
              case _ => listType
            }
          },
          emptyArray = ()=>new java.util.ArrayList[Any](),
          appendItem = (inst,item,itemType) => {
            inst.add(item)
            inst
          }
        )
    )
    
    val astOpt = parser.parse("[ 1, 2, 3 ]")
    assert(astOpt.isDefined)
    
    astOpt.foreach( ast => {
      ASTDump.dump(ast)
      
      val tast = toaster.compile(ast)
      TASTDump.dump(tast)
  
      println(TypeDescriber.describe(tast.supplierType))
      
      val computed = tast.supplier.get()
      println(computed)
    })
  }
}
