package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.PojoAST
import xyz.cofe.stsl.types.{Field, Fn, Fun, TAnon, TObject, Type, WriteableField}

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
  case class ObjPrepared[I,T <: Type](instance:I, tip:T)
  
  trait ObjectDefiner[B,I,T<:Type] {
    def define():B
    def build(b:B):ObjPrepared[I,T]
  }
  
  trait ObjectBuilder[B,I,T<:Type] {
    def declareField(buildObj:B, name:String, valueTast:TAST):B
    def declareMethod(buildObj:B, name:String, fun:Fun):B
  }
  
  trait Initialize[I,T<:Type] {
    def initialize(inst:ObjPrepared[I,T]):Any
  }
  
  private def compilePojo[B,I,T<:Type]
  (
    toaster: Toaster,
    pojoAST: PojoAST
  )(implicit
    definer:ObjectDefiner[B,I,T],
    builder:ObjectBuilder[B,I,T],
    init:Initialize[I,T]
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
      supplier = ()=>{
        //objUnit.instance
        init.initialize(objUnit)
      },
      children = fields.map { it => it.fieldTast }
    )
  }
  
  case class ObjConstructor[T <: Type]
  (
    tip: T,
    fieldsInitializer: List[(String,TAST)] = List()
  )
  
  //region TObjectPojo
  
  case class TObjectDefiner(typeName:String) extends ObjectDefiner[
    ObjConstructor[TObject],
    ObjConstructor[TObject],
    TObject
  ] {
    override def define() = ObjConstructor(
      new TObject(typeName)
    )
    override def build(b: ObjConstructor[TObject]) = ObjPrepared(b,b.tip)
  }
  
  object TObjectBuilder extends ObjectBuilder[
    ObjConstructor[TObject],
    ObjConstructor[TObject],
    TObject
  ] {
    override def declareField(
                               buildObj: ObjConstructor[TObject],
                               fieldName: String,
                               valueTast: TAST):ObjConstructor[TObject] = {
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
      buildObj.copy(
        fieldsInitializer = (fieldName, valueTast) :: buildObj.fieldsInitializer
      )
    }
    override def declareMethod(
                                buildObj: ObjConstructor[TObject],
                                name: String,
                                fun: Fun): ObjConstructor[TObject] = {
      buildObj.tip.methods.append(name,fun)
      buildObj
    }
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
      implicit val objBld = TObjectBuilder
      implicit val initializeObject:Initialize[ObjConstructor[TObject],TObject] = init => {
        val inst = new java.util.LinkedHashMap[Any,Any]()
        init.instance.fieldsInitializer.reverse.foreach { case (fname,tast) =>
          inst.put(fname, tast.supplier.get())
        }
        inst
      }
      compilePojo(toaster, pojoAST)
    }
  }
  //endregion
  
  object TAnonDefiner extends ObjectDefiner[
    ObjConstructor[TAnon],
    ObjConstructor[TAnon],
    TAnon
  ] {
    override def define() = {
      val t = new TAnon()
      ObjConstructor(t)
    }
    override def build(b: ObjConstructor[TAnon]): ObjPrepared[ObjConstructor[TAnon], TAnon] = ObjPrepared(b,b.tip)
  }
  
  object TAnonBuilder extends ObjectBuilder[
    ObjConstructor[TAnon],
    ObjConstructor[TAnon],
    TAnon
  ] {
    override def declareField(
                               buildObj: ObjConstructor[TAnon],
                               fieldName: String,
                               valueTast: TAST
                             ) = {
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
      buildObj.copy(
        fieldsInitializer = (fieldName, valueTast) :: buildObj.fieldsInitializer
      )
    }
    override def declareMethod(
                                buildObj: ObjConstructor[TAnon],
                                name: String,
                                fun: Fun
                              ) = {
      buildObj.tip.methods.append(name,fun)
      buildObj
    }
  }
  
  case class TAnonPojo() extends PojoCompiler {
    /**
     * Компиляция
     *
     * @param toaster тостер
     * @param pojoAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, pojoAST: PojoAST): TAST = {
      implicit val objDef = TAnonDefiner
      implicit val objBld = TAnonBuilder
      implicit val initialize: Initialize[ObjConstructor[TAnon],TAnon] = init => {
        val inst = new util.LinkedHashMap[Any,Any]()
        inst.put(AnonymousObject.TypeDefinition, init.tip)
        init.instance.fieldsInitializer.reverse.foreach { case (fname,tast) =>
          inst.put(fname, tast.supplier.get())
        }
        inst
      }
      compilePojo(toaster, pojoAST)
    }
  }
}