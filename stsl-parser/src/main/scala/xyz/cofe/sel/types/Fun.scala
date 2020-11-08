package xyz.cofe.sel.types

/**
 * Функция
 * @param fn реализация
 * @param parameters Параметры
 * @param retType возврщаемый тип
 * @param description описание
 */
class Fun(
           val fn:(List[Any]=>Any),
           parameters : List[Param],
           retType : Type,
           val description:Option[String]=None,
           genericsParams:List[GenericParam] = List()
         ) extends Type with TypeReplace with BakeGenerics {
  println(s"build fn( genericsParams=$genericsParams )")

  require(fn!=null)
  require(parameters!=null)
  require(retType!=null)
  require(if(parameters.nonEmpty){
    parameters.map(p => p!=null && p.name!=null && p.paramType!=null).reduce((a,b)=>a&&b)
  }else{true})

  lazy val returnType : Type = {
    val self = this

    val repl = (x:Type) => { x match {
      case Type.FN => Some(self)
      case _ => None
    }}

    retType match {
      case Type.FN => self
      case gi:GenericInstance => gi.typeReplace(repl)
      case tr: TypeReplace => tr.typeReplace(repl)
      case _ => retType
    }
  }

  /**
   * Имя типа
   */
  override lazy val name: String = s"fn${params.length}"

  /**
   * Расширяет тип
   */
  override val extend: Option[Type] = Some(Type.FN)

  lazy val params : List[Param] = {
    val self = this
    parameters.map(
      p => Param(p.name, p.paramType match {
//        case gp: GenericPlaceholder =>
//          if( gp.owner==Type.FN )
//            GenericPlaceholder(gp.name, self)
//          else
//            gp
        case Type.FN => self
        case tr: TypeReplace => tr.typeReplace {
          case Type.FN => Some(self)
          case _ => None
        }
        case _ => p.paramType
      })
    )
  }

//  /**
//   * Проверка что переменная может содержать указанный тип
//   *
//   * @param t тип
//   * @return true - когда переменная может содержать тип данных t
//   */
//  override def assignable(t: Type): Boolean = {
//    require(t!=null)
//    if( t.isInstanceOf[Fun] ){
//      genericAssignable(t)
//    }else false
//  }

  /**
   * Проверка что переменная может содержать указанный тип
   *
   * @param t тип
   * @return true - когда переменная может содержать тип данных t
   */
  override def assignable( t:Type ):Boolean = {
    require(t!=null)
    t match {
      case fn: Fun =>
        if (fn.params.length != fn.params.length) {
          false
        } else {
          if (!returnType.assignable(fn.returnType)) {
            false
          } else {
            if (fn.params.nonEmpty) {
              fn.params.indices.map(pi => {
                val selfParam = params(pi)
                val asgnParam = fn.params(pi)
                asgnParam.paramType.assignable(selfParam.paramType)
              }).reduce((a, b) => a && b)
            } else {
              true
            }
          }
        }
      case _ => false
    }
  }

  /**
   * Параметры типа
   *
   * @return параметры типы
   */
  override lazy val generics: List[GenericParam] = {
    //params.map( p => ContraVariant(p.name, p.paramType) ) ++ List(CoVariant("Z", this.returnType))
    genericsParams
  }

  protected def sameSignature(fun: Fun):Boolean = {
    require(fun!=null)
    if( params.size==fun.params.size ){
      val paramMatch = if( params.nonEmpty ) {
        val p1 = params.map(_.paramType)
        val p2 = fun.params.map(_.paramType)
        val m : Boolean = p1.zip(p2).map({ case (t1, t2) =>
          t1 == t2
        }).reduce((a, b) => a && b)
        m
      } else {
        true
      }
      val retMatch = returnType == fun.returnType
      retMatch && paramMatch
    } else false
  }

  /**
   * Возвращает дистанцию между типами
   *
   * @param t тип
   * @return 0 - одинаковый тип; <br>
   *         1 - this ковариантент t, где t - прямой дочерний тип (сын) <br>
   *         2 - this ковариантент t, где t - дочерний тип второго порядка (внук) <br>
   *         N+ - this ковариантент t, где t - дочерний тип N+ порядка <br>
   *         -1 - this контр-ковариантент t, где t - прямой родительский тип (отец) <br>
   *         -2 - this контр-ковариантент t, где t - родительский тип второго порядка (дед) <br>
   *         N- - this контр-ковариантент t, где t - родительский тип N- порядка <br>
   */
  override def assignableDistance(t: Type): Option[Int] = {
    require(t!=null)
    if( t==this ){
      Some(0)
    } else {
      t match {
        case f: Fun => {
          if (assignable(f)) {
            if( sameSignature(f) ){
              Some(0)
            }else{
              Some(1)
            }
          }else{
            if(f.assignable(this)){
              val d = f.assignableDistance(this)
              if( d.isDefined )
                Some( -d.get )
              else
                None
            }else{
              None
            }
          }
        }
        case _ => {
          if( t==Type.FN ) Some(-1)
          else if( t==Type.OBJECT ) Some(-1)
          else None
        }
      }
    }
  }

  /**
   * Вызов функции
   * @param args аргументы
   */
  def call( args:List[Any] ) : Any = this.fn(args)

  /**
   * Проверка что типы параметров совпадают
   * @param f проверяемая функция
   * @return true - есть полное совпадения типов параметров
   */
  def sameArgs( f:Fun ):Boolean = {
    require(f!=null)
    if( f.params.length == params.length ){
      var matchParams = true
      for( i <- params.indices ){
        if( params(i).paramType != f.params(i).paramType ){
          matchParams = false
        }
      }
      matchParams
    } else false
  }

  /**
   * Проверка совпадения возвращаемого типа
   * @param f проверяемая функция
   * @return true - есть полное совпадения типов результата
   */
  def sameReturn( f:Fun ): Boolean ={
    require(f!=null)
    f.returnType == f.returnType
  }

  /**
   * Добавляет описание
   * @param desc описание
   * @return функция с описанием
   */
  def description( desc:String ):Fun = {
    if( desc!=null ){
      new Fun(fn, params, returnType, Some(desc), generics)
    }else{
      new Fun(fn, params, returnType, None, generics)
    }
  }

  /**
   * Замена типа в функции
   * @param replacement функция замены
   * @return функция
   */
  def typeReplace( replacement:Type=>Option[Type] ):Fun = {
    require(replacement!=null)

    val rret1 = returnType match {
      case tr:TypeReplace => Some(tr.typeReplace(replacement))
      case _ => replacement(returnType)
    }

    val rret = rret1.getOrElse(returnType)

    val rparams = params.map(p=> {
      val rpt = p.paramType match {
        case tr:TypeReplace => Some(tr.typeReplace(replacement))
        case _ => replacement(p.paramType)
      }

      Param(p.name,rpt.getOrElse(p.paramType))
    })

    new Fun(fn, rparams, rret, description, generics)
  }

  def bakeGenerics( recipe:Map[String,Type] ):Fun = {
    require(recipe!=null)
    val newGenerics = generics.filter( p => !recipe.contains(p.name) )
    val newParams : List[Param] = params.map( p => p.paramType match {
      case b:BakeGenerics => Param(p.name,b.bakeGenerics(recipe))
      case gp: GenericPlaceholder => if( recipe.contains(gp.name) ) {
        //TODO here check assignable
        Param(p.name, recipe(gp.name))
      }else{
        p
      }
      case _ => p
    })
    //TODO here check assignable
    val newRet = returnType match {
      case gi:BakeGenerics => gi.bakeGenerics(recipe)
      case gh:GenericPlaceholder => recipe.getOrElse(gh.name,gh)
      case _ => returnType
    }
    new Fun(fn, newParams, newRet, description, newGenerics)
  }

  override def toString: String = {
    val genericStr = if(generics.nonEmpty){
      "["+generics.map( p => s"$p" ).reduce( (a,b)=>a+", "+b)+"]"
    } else {
      ""
    }
    val paramStr = params.map( p => s"${p.name}:${p.paramType}").reduce( (a,b)=>a+", "+b)
    s"${genericStr}(${paramStr}):${this.returnType}" + (if(description.isDefined) "//"+description.get else "")
  }
}

object Fun {
  /**
   * Определение функции от 3-х аргументов
   * @param aName Имя аргумента
   * @param aType Тип аргумента
   * @param bName Имя аргумента
   * @param bType Тип аргумента
   * @param cName Имя аргумента
   * @param cType Тип аргумента
   * @param zType Тип результата
   * @param f функция
   * @tparam A Тип аргумента
   * @tparam B Тип аргумента
   * @tparam C Тип аргумента
   * @tparam Z Тип результата
   * @return функция
   */
  def fn[A,B,C,Z](
                   aName:String, aType:Type,
                   bName:String, bType:Type,
                   cName:String, cType:Type,
                   zType:Type, f:(A,B,C)=>Z ):Fun={
    require(f!=null)
    require(aName!=null); require(aType!=null);
    require(bName!=null); require(bType!=null);
    require(cName!=null); require(cType!=null);
    require(zType!=null)

    val ls = List(
      Param(aName,aType),
      Param(bName,bType),
      Param(cName,cType),
    )

    new Fun( (args)=>{
      require(args!=null)
      require(args.length==3)
      f(args.head.asInstanceOf[A], args(1).asInstanceOf[B], args(2).asInstanceOf[C])
    }, ls, zType )
  }

  /**
   * Определение функции от двух аргументов
   * @param aName Имя аргумента
   * @param aType Тип аргумента
   * @param bName Имя аргумента
   * @param bType Тип аргумента
   * @param zType Тип результата
   * @param f функция
   * @tparam A Тип аргумента
   * @tparam B Тип аргумента
   * @tparam Z Тип результата
   * @return функция
   */
  def fn[A,B,Z](aName:String, aType:Type, bName:String, bType:Type, zType:Type, f:(A,B)=>Z ):Fun={
    require(f!=null)
    require(aName!=null)
    require(aType!=null)
    require(bName!=null)
    require(bType!=null)
    require(zType!=null)

    val ls = List(Param(aName,aType),Param(bName,bType))
    new Fun( args=>{
      require(args!=null)
      require(args.length==2)
      f(args.head.asInstanceOf[A], args(1).asInstanceOf[B])
    }, ls, zType )
  }

  /**
   * Определение функции от одного аргумента
   * @param aName Имя аргумента
   * @param aType Тип аргумента
   * @param zType Тип результата
   * @param f функция
   * @tparam A Тип аргумента
   * @tparam Z Тип результата
   * @return функция
   */
  def fn[A,Z](aName:String, aType:Type, zType:Type, f:A=>Z ):Fun = {
    require(f!=null)
    require(aName!=null)
    require(aType!=null)
    require(zType!=null)

    val ls = List(Param(aName,aType))
    new Fun( args => {
      require(args!=null)
      require(args.length==1)
      f(args.head.asInstanceOf[A])
    }, ls, zType)
  }

  /**
   * Определение функции от 0 аргументов
   * @param zType Тип результата
   * @param f функция
   * @tparam Z Тип результата
   * @return функция
   */
  def fn[Z](zType:Type, f:()=>Z ):Fun = {
    require(f!=null)
    require(zType!=null)

    val ls = List()
    new Fun( args=>{
      require(args!=null)
      require(args.isEmpty)
      f()
    }, ls, zType)
  }
}