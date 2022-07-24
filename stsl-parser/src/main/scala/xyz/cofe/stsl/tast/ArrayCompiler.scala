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
 *
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    //noinspection NotImplementedCode
    override def compile(toaster: Toaster, arrayAST: ArrayAST): TAST = ???
  }
  
  /**
   * Массив, где тип элемента массива ANY
   * @param typeConstruct Создание тип, fn(itemType:Type)=>ARRAY, где itemType - тип элемента
   * @param emptyArray Создание экземпляра пустого массива
   * @param appendItem добавление элемента в конец массива
   * @param buildArray завершение создания массива
   * @tparam INST экземпляр массива
   * @tparam ARRAY тип контейнер массива (например может быть или Array или List или ...)
   */
  case class AnyTypedArray[INST,ARRAY <: Type](
    typeConstruct:Type=>ARRAY,
    emptyArray:()=>INST,
    appendItem:(INST,Any)=>INST,
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
     *
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    //noinspection ScalaUnusedSymbol,TypeAnnotation
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
        
        // массив из множества элементов
        val first = arrayTAST.head
        val tail = arrayTAST.tail
        val allAssignableFromFirst = tail
          .map { otherTAST => first.tast.supplierType.assignable(otherTAST.tast.supplierType) }.forall(b => b)
        if( !allAssignableFromFirst ){
          throw ToasterError("in array different item types", arrayAST)
        }
        val arrayType = typeConstruct(emptyArrayItemType)
        TAST(
          ast = arrayAST,
          supplierType = arrayType,
          supplier = ()=>{
            val inst = arrayTAST.foldLeft(emptyArray())( (arrInst, itemTAST) => {
              appendItem(arrInst, itemTAST.tast.supplier.get())
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
     *
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    //noinspection ScalaUnusedSymbol,TypeAnnotation
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
  
  /**
   * Массив, где тип элемент определяет путем слияния типов элементов, аля Duck Typing
   * @param collector коллектор типов элементов
   * @param reducer редуктор типов для получения Duck типа
   * @param arrayTypeConstruct Создание тип, fn(itemType:Type)=>ARRAY, где itemType - тип элемента
   * @param emptyArray Создание экземпляра пустого массива
   * @param appendItem добавление элемента в конец массива
   * @param buildArray завершение создания массива
   * @param emptryArrayItemType тип элемента массива используемый в случае пустого массива.
   * @tparam Acum Тип (Scala) аккумулятора
   * @tparam ArrInst Тип (Scala) экземпляра элемента
   * @tparam ArrType Тип (Scala) массива
   */
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
  
  /**
   * Предпросмотр типа первого элемента
   * @param pattern шаблон выбора способа слияния по типу первого элемента массива
   * @param default способ слияния если шаблон не совпал
   */
  case class LookupMerge( pattern:TAST=>Option[ArrayCompiler], default:ArrayCompiler ) extends ArrayCompiler {
    require(pattern!=null)
    require(default!=null)
    /**
     * Компиляция
     *
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, arrayAST: ArrayAST): TAST = {
      require(toaster!=null)
      require(arrayAST!=null)
      if( arrayAST.items.isEmpty ){
        default.compile(toaster, arrayAST)
      }else{
        val arrayTAST = arrayAST.items.map( itemAST=> toaster.compile(itemAST))
        pattern(arrayTAST.head) match {
          case Some(arrayCompiler) => arrayCompiler.compile(toaster, arrayAST)
          case None => default.compile(toaster, arrayAST)
        }
      }
    }
  }
  
  /**
   * Откат к резервному типу слияния
   * @param pattern шаблон выбора способа слияния по типу первого элемента массива
   * @param default способ слияния если шаблон не совпал
   */
  case class FallbackMerge( main:ArrayCompiler,
                            fallback:ArrayCompiler,
                            onError:Option[Throwable=>Option[ArrayCompiler]] = None
                          ) extends ArrayCompiler {
    require(main!=null)
    require(fallback!=null)
    require(onError!=null)
    /**
     * Компиляция
     *
     * @param toaster  тостер
     * @param arrayAST AST дерево
     * @return скомпилированное дерево
     */
    override def compile(toaster: Toaster, arrayAST: ArrayAST): TAST = {
      require(toaster!=null)
      require(arrayAST!=null)
      try {
        main.compile(toaster, arrayAST)
      } catch {
        case err:Throwable =>
          (onError match {
            case Some(customHandler) => customHandler(err).getOrElse(fallback)
            case None => fallback
          }).compile(toaster, arrayAST)
      }
    }
  }
}
