package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.PojoAST
import xyz.cofe.stsl.types.{TObject, WriteableField}

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
  /**
   * Создает [[java.util.LinkedHashMap]] для Pojo, тип от TObject
   * @param typeNameBuilder создание имени, fn( unique ): name
   */
  case class TObjectPojo( typeNameBuilder: Long=>String = id => s"Pojo$id" ) extends PojoCompiler {
    require(typeNameBuilder!=null)
    
    val idSeq = new AtomicLong(0)
  
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
      val typeObj = new TObject(typeNameBuilder(id))
  
      val fields = pojoAST.items.map(pojoItemAst => {
        val fieldTastValue = toaster.compile(pojoItemAst.value)
        val writeableField = new WriteableField(
          pojoItemAst.key.tok.name, fieldTastValue.supplierType,
          obj => {
            val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
            val name = pojoItemAst.key.tok.name
            if( mmap.containsKey(name) ){
              mmap.get(name)
            } else {
              val computed = fieldTastValue.supplier.get()
              mmap.put(name,computed)
              computed
            }
          },
          (obj,vl) => {
            val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
            val name = pojoItemAst.key.tok.name
            mmap.put(name,vl)
          }
        )
        (writeableField,fieldTastValue,pojoItemAst)
      })
  
      fields.foreach( fld => typeObj.fields.append(fld._1) )
  
      TAST(pojoAST, typeObj, ()=>{
        val mapObj = new util.LinkedHashMap[String,Any]()
        
        fields.foreach { case (writeableField, fieldValue, pojoItemAst) =>
          mapObj.put(writeableField.name, writeableField.reading(mapObj))
        }
        mapObj
      },
        fields.map { case (writeableField, fieldTastValue, pojoItemAst) => fieldTastValue }
      )
    }
  }
}