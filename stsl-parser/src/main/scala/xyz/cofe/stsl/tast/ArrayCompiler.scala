package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.{AST, ArrayAST}
import xyz.cofe.stsl.tast.isect.{AnonCollector, AnonReductor}
import xyz.cofe.stsl.types.{Obj, Type}

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
  
  case class MergeAnon[Acum,ArrInst,ArrType<:Type]
  (
    collector:AnonCollector[Acum],
    reducer:AnonReductor[Acum],
    arrayTypeConstruct:Type=>ArrType,
    emptyArray:()=>ArrInst,
    appendItem:(ArrInst,Any,Type)=>ArrInst,
    buildArray:Option[ArrInst=>ArrInst]=None,
    emptryArrayItemType:Type=Type.ANY
  ) extends ArrayCompiler {
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
        val arrayType = arrayTypeConstruct(emptryArrayItemType)
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
      }else {
        val arrayTAST = arrayAST.items.map( itemAST=> new {
          val ast: AST = itemAST
          val tast: TAST = toaster.compile(itemAST)
        })
        if( arrayTAST.length==1 ){
          // массив из одного элемента
          val first = arrayTAST.head
          val arrayType = arrayTypeConstruct(first.tast.supplierType)
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
          val targetItemType = reducer.reduce(
            arrayTAST.foldLeft(collector.initial)( (accum,itemTAST) => {
              collector.collect(accum, itemTAST.tast.supplierType)
            })
          )
          val arrayType = arrayTypeConstruct(targetItemType)
          TAST(
            ast = arrayAST,
            supplierType = arrayType,
            supplier = ()=>{
              val arrayInst = arrayTAST.foldLeft(emptyArray())( (inst, itm) => appendItem(inst,itm.tast.supplier.get(),itm.tast.supplierType) )
              buildArray match {
                case Some(builder) => builder(arrayInst)
                case None => arrayInst
              }
            },
            arrayTAST.map(_.tast)
          )
        }
      }
    }
  }
}
