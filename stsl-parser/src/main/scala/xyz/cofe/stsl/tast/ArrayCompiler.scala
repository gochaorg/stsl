package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.ArrayAST
import xyz.cofe.stsl.types.Type

/**
 * Компиляция массивов [[ArrayAST]]
 */
trait ArrayCompiler {
  /**
   * Компиляция
   * @param toaster тостер
   * @param arrayAST AST дерево
   * @return скомпилированное дерево
   */
  def compile(toaster: Toaster, arrayAST: ArrayAST):TAST
}

object ArrayCompiler {
  case class NoImpl() extends ArrayCompiler {
    /**
     * Компиляция
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, arrayAST: ArrayAST): TAST = ???
  }
  
  /**
   * Массив, где первый элемент определяет тип элементов массива.
   * Второй и последующие типы должны быть assignable к первому
   * @tparam INST экземпляр массива
   * @tparam ARRAY тип контейнер массива (например может быть или Array или List или ...)
   * @param typeConstruct Создание тип, fn(itemType:Type)=>ARRAY, где itemType - тип элемента
   * @param emptyArray Создание экземпляра пустого массива
   * @param appendItem добавление элемента в конец массива
   * @param buildArray завершение создания массива
   * @param emptyArrayItemType тип элемента массива используемый в случае пустого массива.
   */
  case class FirstAssignType[INST,ARRAY <: Type](
    typeConstruct:Type=>ARRAY,
    emptyArray:()=>INST,
    appendItem:(INST,Any,Type)=>INST,
    buildArray:Option[INST=>INST]=None,
    emptyArrayItemType:Type=Type.ANY
  ) extends ArrayCompiler {
    require(typeConstruct!=null)
    require(emptyArray!=null)
    require(buildArray!=null)
    require(buildArray!=null)
    require(emptyArrayItemType!=null)
    /**
     * Компиляция
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, arrayAST: ArrayAST): TAST = {
      require(toaster!=null)
      require(arrayAST!=null)
      if( arrayAST.items.isEmpty ){
        // Пустой массив
        val arrayType = typeConstruct(emptyArrayItemType)
        TAST(
          ast = arrayAST,
          supplierType = arrayType,
          supplier = ()=>{
            val inst = emptyArray()
            buildArray match {
              case Some(bld) => bld(inst)
              case None => inst
            }
          }
        )
      }else{
        val arrayTAST = arrayAST.items.map( itemAST=> new {
          val ast = itemAST
          val tast = toaster.compile(itemAST)
        })
        if( arrayTAST.length==1 ){
          // массив из одного элемента
          val first = arrayTAST.head
          val arrayType = typeConstruct(first.tast.supplierType)
          TAST(
            ast = arrayAST,
            supplierType = arrayType,
            supplier = ()=>{
              val inst = appendItem(emptyArray(), first.tast.supplier.get(), first.tast.supplierType)
              buildArray match {
                case Some(bld) => bld(inst)
                case None => inst
              }
            },
            arrayTAST.map(_.tast)
          )
        }else{
          // массив из множества элементов
          val first = arrayTAST.head
          val tail = arrayTAST.tail
          val allAssignableFromFirst = tail
            .map { otherTAST => first.tast.supplierType.assignable(otherTAST.tast.supplierType) }.forall(b => b)
          if( !allAssignableFromFirst ){
            throw ToasterError("in array different item types", arrayAST)
          }
          val arrayType = typeConstruct(first.tast.supplierType)
          TAST(
            ast = arrayAST,
            supplierType = arrayType,
            supplier = ()=>{
              val inst = arrayTAST.foldLeft(emptyArray())( (arrInst, itemTAST) => {
                appendItem(arrInst, itemTAST.tast.supplier.get(), itemTAST.tast.supplierType)
              })
              buildArray match {
                case Some(bld) => bld(inst)
                case None => inst
              }
            },
            arrayTAST.map(_.tast)
          )
        }
      }
    }
  }
}
