package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.Fun
import xyz.cofe.sel.types.Type

/**
 * Коллекция функций
 */
@Deprecated
class Funs ( val funs : List[Fun], sameReturn:Boolean=false, sameArgs:Boolean=true ) extends Seq[Fun] {
  require(funs!=null)

  /**
   * Добавление функции
   * @param fun функция
   * @return новая коллекция с функцией
   */
  def add( fun : Fun ):Funs = {
    require(fun!=null)
    val ls = funs.filter { f =>
      val ret = if( sameReturn ) {
        !f.sameReturn(fun)
      } else {
        true
      }

      val args = if( sameArgs ){
        !f.sameArgs(fun)
      } else {
        true
      }

      ret && args
    }
    new Funs(fun :: ls, sameReturn, sameArgs)
  }

  /**
   * Возвращает кол-во элементов
   * @return кол-во элементов
   */
  override def length: Int = funs.length

  /**
   * Возвращает элемент по индексу
   * @param idx индекс
   * @return элемент
   */
  override def apply(idx: Int): Fun = funs(idx)

  /**
   * Возвращает итератор
   * @return итератор
   */
  override def iterator: Iterator[Fun] = funs.iterator

  /**
   * Поиск подходящей функции
   */
  object find {
    /**
     * Поиск функции совместимой по типу параметров
     * @param args тип параметров
     */
    def sameArgs( args:List[Type] )(implicit implcTypeConv:Funs = null):Invokables = {
      require(args!=null)

      if( args.isEmpty ){
        new Invokables( filter( fn => fn.params.length == args.length ).map( fn => new Invokable(fn, args) ).toList )
      }else {
        val matchArgsCount = filter( fn => fn.params.length == args.length )

        val invks = matchArgsCount.map( fn => {
          val callable = fn.params.indices.map( pi => {
            val consumerParam = fn.params(pi).paramType
            val supplierParam = args(pi)
            consumerParam.assignable(supplierParam)
          }).reduce( (a,b) => a && b )

          var invs : List[Invokable] = List()
          if( callable ){
            invs = new Invokable(fn, args) :: invs
          }else if( implcTypeConv!=null ){
            val implConvs = fn.params.indices.map( pi => {
              val consumerParam = fn.params(pi).paramType
              val supplierParam = args(pi)
              if( consumerParam.assignable(supplierParam) ){
                (true, null)
              }else{
                val implConvSearch = implcTypeConv.find.same(List(supplierParam),consumerParam)
                if( implConvSearch.nonEmpty ) {
                  val implConvsInks = implConvSearch.preferred
                  (implConvsInks.length == 1, implConvsInks.head)
                }else{
                  (false, null)
                }
              }
            })
            if( !implConvs.exists(_._1==false) ){
              val inv = new Invokable(fn, args, null, implConvs.map(i => if( i._2!=null) i._2.fn else null).toList)
              invs = inv :: invs
            }
          }

          invs
        })

        new Invokables(invks.toList.flatten)
      }
    }

    /**
     * Поиск функции совместимой по типу параметров и результату
     * @param args тип параметров
     * @param retType тип результата
     */
    def same( args:List[Type], retType:Type ):Invokables = {
      require(args!=null)
      require(retType!=null)

      val matchArgsCount = filter( fn => fn.params.length == args.length )
      val assignableReturn = matchArgsCount.filter( fn => fn.returnType.assignable(retType) )
      val assignableParams = assignableReturn.filter( fn => {
        args.indices.map(pi=>{
          val arg = args(pi)
          val fparam = fn.params(pi).paramType
          fparam.assignable(arg)
        }).reduce( (a,b)=>a && b )
      })

      Invokables(assignableParams.map(fn => new Invokable(fn, args, retType)))
    }
  }
}
