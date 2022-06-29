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
  case class ObjInst[I,T](instance:I, tip:T)
  
  trait ObjBuilder[B,I,T] {
    def declareField(buildObj:B, name:String, valueTast:TAST):B
    def declareMethod(buildObj:B, name:String, fun:Fun):B
    def build(buildObj:B):ObjInst[I,T]
  }
  
  trait ObjectBuilder[B,I,T] extends ObjBuilder[B,I,T] {
    def declareType(name:String):B
  }
  
  class TObjectBuilder extends ObjectBuilder[ ObjInst[java.util.Map[String,Any],TObject], java.util.Map[String,Any], TObject ]
  {
    override def declareType(name: String): ObjInst[util.Map[String, Any], TObject] = {
      ObjInst(
        new util.LinkedHashMap[String,Any](),
        new TObject(name)
      )
    }
    override def declareField(
                               buildObj: ObjInst[util.Map[String, Any], TObject],
                               fieldName: String,
                               valueTast: TAST
                             ): ObjInst[util.Map[String, Any], TObject] = {
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
                                buildObj: ObjInst[util.Map[String, Any], TObject],
                                name: String,
                                fun: Fun
                              ): ObjInst[util.Map[String, Any], TObject] = {
      buildObj.tip.methods.append(name,fun)
      buildObj
    }
    override def build(buildObj: ObjInst[util.Map[String, Any], TObject]): ObjInst[util.Map[String, Any], TObject] = buildObj
  }
  
  /**
   * Создает [[java.util.LinkedHashMap]] для Pojo, тип от TObject
   * @param typeNameBuilder создание имени, fn( unique ): name
   */
  case class TObjectPojo(
    typeNameBuilder: Long=>String = id => s"Pojo$id"
  ) extends PojoCompiler {
    require(typeNameBuilder!=null)
    
    val idSeq = new AtomicLong(0)
    val objBuilder = new TObjectBuilder()
  
    /**
     * Компиляция
     *
     * @param toaster тостер
     * @param pojoAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, pojoAST: PojoAST): TAST = {
      require(toaster!=null)
      require(pojoAST!=null)
      
      val id = idSeq.incrementAndGet()
      
      var objConstruct = objBuilder.declareType(typeNameBuilder(id))
      
      val pojoItems = pojoAST.items.map(it => new {
        val keyAst = it.key
        val valueTast = toaster.compile(it.value)
      })
      
      val fieldsRaw = pojoItems.filterNot { it => it.valueTast.supplierType.isInstanceOf[Fun] }
      val fields = fieldsRaw.map { it =>
        new {
          val fieldTast = it.valueTast
          val fieldName = it.keyAst.tok.name
        }
      }
      fields.foreach( it => {
        objConstruct = objBuilder.declareField(objConstruct, it.fieldName, it.fieldTast)
      })
  
      val funsRaw = pojoItems.filter { it => it.valueTast.supplierType.isInstanceOf[Fun] }
      val methods = funsRaw.map { it =>
        new {
          val name = it.keyAst.tok.name
          val fun = it.valueTast.supplierType.asInstanceOf[Fun]
        }
      }
      methods.foreach { it =>
        objConstruct = objBuilder.declareMethod(objConstruct, it.name, it.fun)
      }
      
      val objUnit = objBuilder.build(objConstruct)
      
      TAST(
        ast = pojoAST,
        supplierType = objUnit.tip,
        supplier = ()=>objUnit.instance,
        children = fields.map { it => it.fieldTast }
      )
    }
  }
}