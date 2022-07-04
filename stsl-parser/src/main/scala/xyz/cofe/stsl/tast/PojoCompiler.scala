package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.PojoAST
import xyz.cofe.stsl.types.{Field, Fn, Fun, TObject, Type, WriteableField}

import java.util
import java.util.concurrent.atomic.AtomicLong

/**
 * Компиляция [[PojoAST]]
 */
sealed trait PojoCompiler {
  /**
   * Компиляция
   * @param toaster тостер
   * @param pojoAST AST дерево
   * @return скомпилированное дерево
   */
  def compile(toaster: Toaster, pojoAST: PojoAST):TAST
}

object PojoCompiler {
  case class ObjInst[I,T <: Type](instance:I, tip:T)
  
  trait ObjectDefiner[B,I,T<:Type] {
    def define():B
    def build(b:B):ObjInst[I,T]
  }
  
  trait ObjectBuilder[B,I,T<:Type] {
    def declareField(buildObj:B, name:String, valueTast:TAST):B
    def declareMethod(buildObj:B, name:String, fun:Fun):B
  }
  
  case class TObjectDefiner(typeName:String) extends ObjectDefiner[
    ObjInst[java.util.LinkedHashMap[Any,Any], TObject],
    java.util.LinkedHashMap[Any,Any],
    TObject
  ] {
    override def define(): ObjInst[util.LinkedHashMap[Any, Any], TObject] = ObjInst(
      new util.LinkedHashMap[Any,Any](),
      new TObject(typeName)
    )
    override def build(b: ObjInst[util.LinkedHashMap[Any, Any], TObject]): ObjInst[util.LinkedHashMap[Any, Any], TObject] = b
  }
  
  implicit object TObjectBuilder extends ObjectBuilder[
    ObjInst[java.util.LinkedHashMap[Any,Any], TObject],
    java.util.LinkedHashMap[Any,Any],
    TObject
  ] {
    override def declareField(
                               buildObj: ObjInst[util.LinkedHashMap[Any, Any], TObject],
                               fieldName: String,
                               valueTast: TAST):ObjInst[util.LinkedHashMap[Any, Any], TObject] = {
      val fieldDef = new WriteableField(
        name = fieldName,
        tip = valueTast.supplierType,
        reading = obj => {
          val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
          mmap.get(fieldName)
        },
        writing = (obj,vl) => {
          val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
          val name = fieldName
          mmap.put(name,vl)
        }
      )
      buildObj.tip.fields.append(fieldDef)
      buildObj.instance.put(fieldName, valueTast.supplier.get())
      buildObj
    }
    override def declareMethod(
                                buildObj: ObjInst[util.LinkedHashMap[Any, Any], TObject],
                                name: String,
                                fun: Fun): ObjInst[util.LinkedHashMap[Any, Any], TObject] = {
      buildObj.tip.methods.append(name,fun)
      buildObj
    }
  }
  
  private def compilePojo[B,I,T<:Type]
  (
    toaster: Toaster,
    pojoAST: PojoAST
  )(implicit
    definer:ObjectDefiner[B,I,T],
    builder:ObjectBuilder[B,I,T]
  ):TAST = {
    var objConstruct = definer.define()
  
    //noinspection TypeAnnotation
    val pojoItems = pojoAST.items.map(it => new {
      val keyAst = it.key
      val valueTast = toaster.compile(it.value)
    })
  
    val fieldsRaw = pojoItems.filterNot { it => it.valueTast.supplierType.isInstanceOf[Fun] }
    //noinspection TypeAnnotation
    val fields = fieldsRaw.map { it =>
      new {
        val fieldTast = it.valueTast
        val fieldName = it.keyAst.tok.name
      }
    }
    fields.foreach( it => {
      objConstruct = builder.declareField(objConstruct, it.fieldName, it.fieldTast)
    })
  
    val funsRaw = pojoItems.filter { it => it.valueTast.supplierType.isInstanceOf[Fun] }
    //noinspection TypeAnnotation
    val methods = funsRaw.map { it =>
      new {
        val name = it.keyAst.tok.name
        val fun = it.valueTast.supplierType.asInstanceOf[Fun]
      }
    }
    methods.foreach { it =>
      objConstruct = builder.declareMethod(objConstruct, it.name, it.fun)
    }
  
    val objUnit = definer.build(objConstruct)
  
    TAST(
      ast = pojoAST,
      supplierType = objUnit.tip,
      supplier = ()=>objUnit.instance,
      children = fields.map { it => it.fieldTast }
    )
  }
  
  //region TObjectPojo

  /**
   * Создает [[java.util.LinkedHashMap]] для Pojo, тип от TObject
   * @param typeNameBuilder создание имени, fn( unique ): name
   */
  case class TObjectPojo(
    typeNameBuilder: Long=>String = id => s"Pojo$id"
  ) extends PojoCompiler {
    require(typeNameBuilder!=null)
    
    val idSeq = new AtomicLong(0)
  
    /**
     * Компиляция
     * @param toaster тостер
     * @param pojoAST AST дерево
     * @return скомпилированное дерево
     */
    //noinspection TypeAnnotation
    override def compile(toaster: Toaster, pojoAST: PojoAST): TAST = {
      require(toaster!=null)
      require(pojoAST!=null)
      
      val id = idSeq.incrementAndGet()
      implicit val objDef: TObjectDefiner = TObjectDefiner(typeNameBuilder(id))
      compilePojo(toaster, pojoAST)
    }
  }
  //endregion
  
  
}