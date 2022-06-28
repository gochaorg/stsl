package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast._
import xyz.cofe.stsl.tok._
import JvmType._
import xyz.cofe.stsl.types.{CallableFn, Fn, Fun, GenericInstance, MutableFields, Param, Params, TObject, Type, WriteableField}

import java.util
import scala.collection.mutable

/**
 * "Тостер" - Компиляция AST выражений
 */
class Toaster( val typeScope: TypeScope, val varScope: VarScope=new VarScope() ) {
  require(typeScope!=null)
  require(varScope!=null)

  def compile( ast:AST ):TAST = {
    require(ast!=null)
    ast match {
      case a:StackedArgumentAST => compile(a)
      case a:LiteralAST => compile(a)
      case a:IdentifierAST => compile(a)
      case a:BinaryAST => compile(a)
      case a:DelegateAST => compile(a)
      case a:TernaryAST => compile(a)
      case a:PropertyAST => compile(a)
      case a:CallAST => compile(a)
      case a:LambdaAST => compile(a)
      case a:PojoAST => compile(a)
      case _ => //noinspection NotImplementedCode
        ???
    }
  }

  /**
   * Компиляция литерального выражения
   * @param literalAST выражение
   * @return типизированный узел AST
   */
  def compile( literalAST: LiteralAST ):TAST = {
    require(literalAST!=null)
    literalAST.tok match {
      case num: FloatNumberTok => TAST( literalAST, FLOAT, ()=>num.value.floatValue() )
      case num: DoubleNumberTok => TAST( literalAST, DOUBLE, ()=>num.value.doubleValue() )
      case num: DecimalNumberTok => TAST( literalAST, DECIMAL, ()=>num.value.asInstanceOf[BigDecimal] )
      case num: ByteNumberTok => TAST( literalAST, BYTE, ()=>num.value.byteValue() )
      case num: ShortNumberTok => TAST( literalAST, SHORT, ()=>num.value.shortValue() )
      case num: IntNumberTok => TAST( literalAST, INT, ()=>num.value.intValue() )
      case num: LongNumberTok => TAST( literalAST, LONG, ()=>num.value.longValue() )
      case num: BigIntNumberTok => TAST( literalAST, BIGINT, ()=>num.value.asInstanceOf[BigInt] )
      case num: NumberTok => TAST( literalAST, NUMBER, ()=>num.value )
      case str: StringTok => TAST( literalAST, STRING, ()=>str.value )
    }
  }

  /**
   * Компиляция вызова метода
   * @param thiz вызываемый объект (класс)
   * @param method вызываемый метод
   * @param args аргументы вызова
   * @return вариант вызова
   */
  def call(thiz:TObject, method:String, args:List[Type] ):CallCase = {
    val cases = typeScope.callCases(thiz,method,args)
    val implArgNames = if( args.isEmpty ){
      ""
    } else if( args.size==1 ){
      s"${args.head}"
    } else {
      s"${args.map(a=>a.toString).reduce((a,b)=>a+","+b)}"
    }
    val implName = s"$thiz $method($implArgNames)"

    if( cases.preferred.isEmpty ){
      throw ToasterError(s"implementation of $implName not found")
    }else if( cases.preferred.size>1 ){
      throw ToasterError(s"ambiguous implementation of $implName not found")
    }

    cases.preferred.head
  }

  /**
   * Компиляция вызова функции из ряда возможных, с учетом типов аргументов
   * @param functions возможно вызываемые функции
   * @param args типы аргументов
   * @param funName имя функции
   * @return вариант вызова
   */
  def call(functions:Seq[Fun], args:List[Type], funName:Option[String]=None):CallCase = {
    val cases = typeScope.callCases(functions,args)
  
    val implArgNames = if( args.isEmpty ){
      ""
    } else if( args.size==1 ){
      s"${args.head}"
    } else {
      s"${args.map(a=>a.toString).reduce((a,b)=>a+","+b)}"
    }
    val implName = s"${funName.getOrElse("")}($implArgNames)"

    if( cases.preferred.isEmpty ){
      throw ToasterError(s"implementation of $implName not found")
    }else if( cases.preferred.size>1 ){
      throw ToasterError(s"ambiguous implementation of $implName not found")
    }

    cases.preferred.head
  }

  /**
   * Компиляция бинарного выражения
   * @param binaryAST бинарное выражение
   * @return типизированный узел AST
   */
  def compile( binaryAST: BinaryAST ):TAST = {
    require(binaryAST!=null)
    val left = compile(binaryAST.left)
    val right = compile(binaryAST.right)

    val opName = binaryAST.operator.tok.name
    if( !left.supplierType.isInstanceOf[TObject] ){
      throw ToasterError(s"left operand of $opName not object", binaryAST)
    }

    val callCase = call(
      left.supplierType.asInstanceOf[TObject],
      opName,
      List(left.supplierType, right.supplierType))

    val invoke = callCase.invoking()

    TAST(
      binaryAST,
      invoke._2,
      ()=>{ invoke._1.invoke( List(left.supplier.get(), right.supplier.get()) ) },
      List(left,right))
  }

  /**
   * Компиляция (скобочного) выражения
   * @param delegateAST (скобочное) выражение
   * @return типизированный узел AST
   */
  def compile( delegateAST: DelegateAST ):TAST = compile( delegateAST.target )

  /**
   * Компиляция тренарного выражения
   * @param ternaryAST тренарное выражение
   * @return типизированный узел AST
   */
  def compile( ternaryAST: TernaryAST ):TAST = {
    require(ternaryAST!=null)
    val cond = compile(ternaryAST.first)
    if( !BOOLEAN.assignable(cond.supplierType) )throw ToasterError("condition not "+BOOLEAN.name, ternaryAST.first)
  
    val succ = compile(ternaryAST.second)
    val fail = compile(ternaryAST.third)
    if( !succ.supplierType.assignable(fail.supplierType) )throw ToasterError(
      "different type return in success block and failure block", ternaryAST.second, ternaryAST.third)

    TAST(ternaryAST, succ.supplierType, ()=>{
      val quest = cond.supplier.get()
      if( quest!=null ){
        quest match {
          case bval: Boolean =>
            if (bval) {
              succ.supplier.get()
            } else {
              fail.supplier.get()
            }
          case _ =>
            throw new RuntimeException("condition return not " + BOOLEAN.name)
        }
      }else{
        fail.supplier.get()
      }
    }, List(cond,succ,fail))
  }

  /**
   * Компиляция чтения значения переменной
   * @param identifierAST идентификатор переменной
   * @return типизированный узел AST
   */
  def compile( identifierAST: IdentifierAST ):TAST = {
    require(identifierAST!=null)
    //val variable = scope.vars.map.get(identifierAST.tok.name)
    val variable = varScope.get(identifierAST.tok.name)
    if( variable.isEmpty )throw ToasterError(s"undefined variable ${identifierAST.tok.name}")
    TAST( identifierAST, variable.get.tip, ()=>variable.get.read() )
  }

  /**
   * Компиляция чтения значения свойства объекта
   * @param propertyAST свойство объекта
   * @return типизированный узел AST
   */
  def compile( propertyAST: PropertyAST ):TAST = {
    require(propertyAST!=null)

    val obj = compile(propertyAST.obj)
    val pname = propertyAST.name.tok.text
    val objType = obj.supplierType match {
      case t:TObject => t
      case _ => throw ToasterError("property owner not object", propertyAST.obj)
    }
  
    val thizWithParents = {
      var from = objType
      var lst = List(from)
      while( from.extend.isDefined && from.extend.get.isInstanceOf[TObject] ){
        from = from.extend.get.asInstanceOf[TObject]
        lst = from :: lst
      }
      lst
    }
    
    val fields = thizWithParents.foldLeft( new MutableFields() )( (flds,tobj) => {
      tobj.fields.fields.foreach( fld => {
        flds.append(fld)
      })
      flds
    })

    val propOpt = fields.get(pname)
    if( propOpt.isEmpty ){
      throw ToasterError(s"property $pname not found in $objType", propertyAST.name)
    }

    val prop = propOpt.get match {
      case w: WriteableField => w
      case _ => throw ToasterError(s"property $pname of $objType not readable", propertyAST.name)
    }

    TAST(propertyAST, propOpt.get.tip, ()=>{
      val objInst = obj.supplier.get()
      prop.reading(objInst)
    },List(obj))
  }

  /**
   * Компиляция чтения значения расположенного в стеке вызовов
   * @param stackedArgumentAST стек переменная
   * @return типизированный узел AST
   */
  def compile( stackedArgumentAST: StackedArgumentAST ):TAST = {
    require(stackedArgumentAST!=null)
    TAST( stackedArgumentAST, stackedArgumentAST.argumentType, ()=>stackedArgumentAST.value )
  }
  
  /**
   * Компиляция вызова функции или метода
   * @param callAST вызов
   * @return типизированный узел AST
   */
  def compile( callAST: CallAST ):TAST = {
    require(callAST!=null)
    callAST.callable match {
      case fname:StackedArgumentAST => compileFunCall(callAST,fname)
      case fname:IdentifierAST => compileFunCall(callAST,fname.tok.text)
      case prop:PropertyAST => compileMethCall(callAST,prop.obj,prop.name.tok.text)
    }
  }
  private def compileFunCall( callAST: CallAST, callable:StackedArgumentAST ):TAST = {
    val callTast : TAST = compile(callable)

    val targetFn = callTast.supplierType match {
      case f:CallableFn => f
      case _=> throw ToasterError(s"function ${callable.argumentName} is not CallableFn", callable)
    }
    val argumentsTast = callAST.arguments.map( a => compile(a) )

    // checking types
    val calling = this.call(List(targetFn), argumentsTast.map(_.supplierType), Some(callable.argumentName)).invoking()

    TAST( callAST, calling._2,
      () => {
        val callFn = callTast.supplier.get() match {
          case f:CallableFn => f
          case _=> throw new ClassCastException(s"can't cast stacked argument ${callable.argumentName} to CallableFn")
        }
        callFn.invoke(argumentsTast.map(a => a.supplier.get()))
      }, argumentsTast
    )
  }
  private def compileFunCall(callAST: CallAST, functionName:String ):TAST = {
    val fnVar = varScope.get(functionName)
    if( fnVar.isEmpty ) throw ToasterError(s"function $functionName not defined", callAST)

    val targetFn = fnVar.get.tip match {
      case f:CallableFn => f
      case _=> throw ToasterError(s"function $functionName is not CallableFn", callAST)
    }

    val argumentsTast = callAST.arguments.map( a => compile(a) )

    // checking types
    val calling = this.call(List(targetFn), argumentsTast.map(_.supplierType), Some(functionName)).invoking()

    TAST( callAST, calling._2,
      () => {
        val callFn = fnVar.get.read() match {
          case f:CallableFn => f
          case _=> throw new ClassCastException(s"can't cast variable $functionName to CallableFn")
        }
        callFn.invoke(argumentsTast.map(a => a.supplier.get()))
      }, argumentsTast
    )
  }
  private def compileMethCall( callAST: CallAST, objAst:AST, methodName:String ):TAST = {
    val objTast = compile(objAst)
    val objType = objTast.supplierType match {
      case o:TObject => o
      case gi:GenericInstance[_] =>
        gi.source match {
          case _:TObject => gi.source.typeVarBake.thiz(gi.recipe).asInstanceOf[TObject].withName(gi.toString+"$")
          case _=> throw ToasterError("GenericInstance.source is not TObject", objAst)
        }
      case _=> throw ToasterError("callable obj is not TObject", objAst)
    }
    //val argumentsTast = objTast :: callAST.arguments.map( a => compile(a) )

    val callObjTast = TAST(objTast.ast,objType,objTast.supplier,objTast.children)
    val argumentsTast = callObjTast :: callAST.arguments.map( a => compile(a) )

    val calling = call(objType,methodName,argumentsTast.map(_.supplierType)).invoking()
    TAST(
      callAST, calling._2,
      ()=>calling._1.invoke(argumentsTast.map(a => a.supplier.get())),
      argumentsTast
    )
  }
  
  /**
   * Компиляция вызова лямбды
   * @param lambdaAST лямбда
   * @return типизированный узел AST
   */
  def compile( lambdaAST: LambdaAST ):TAST = {
    require(lambdaAST!=null)
    val debug = false

    // имена параметров не должны повторяться
    val argNameDuplicates = (lambdaAST.params.map(_.name.tok.name) ++ {
      if( lambdaAST.recursion.isEmpty ) {
        List[String]()
      } else {
        List[String](lambdaAST.recursion.get.name.tok.name)
      }
    }).groupBy(name=>name).map(_._2.size).toList.filter( cnt => cnt>1)

    val hasDuplicateArgNames : Boolean = argNameDuplicates.nonEmpty
    if( hasDuplicateArgNames ) throw ToasterError("has duplicate arg names", null, lambdaAST.params)

    // типы переменных должны присуствовать в области видимости
    val xxx1 = lambdaAST.params.map( p => (p.typeName, typeScope.get(p.typeName.name)) ) ++ (
      if( lambdaAST.recursion.isEmpty ) {
        List()
      } else {
        List((lambdaAST.recursion.get.typeName, typeScope.get(lambdaAST.recursion.get.typeName.name)))
      }
      )
    val undefinedTypes = xxx1.filter( p => p._2.isEmpty )

    if( undefinedTypes.nonEmpty ){
      throw ToasterError("undefined argument types", undefinedTypes.map(_._1))
    }

    // используемые перменные должны быть
    //   вариант 1 - аргументами
    //   вариант 2 - аргументами + быть взяты из контекста

    var bodyIdentifiers = lambdaAST.body.tree.map(_.last).filter( _.isInstanceOf[IdentifierAST] ).map( _.asInstanceOf[IdentifierAST] ).toList
    val bodyIdentifiersUniqNames = bodyIdentifiers.map(_.tok.name).distinct

    // реализация варианта 1
    // внешние переменные не должны быть задействованы
    val externalIdentifierNames = bodyIdentifiersUniqNames.diff(
      lambdaAST.params.map(p=>p.name.tok.name) ++ (
        if( lambdaAST.recursion.isEmpty )
          List[String]()
        else
          List[String]( lambdaAST.recursion.get.name.tok.name )
        )
    )
    if( externalIdentifierNames.nonEmpty ){
      throw ToasterError("body contains external identifiers",
        bodyIdentifiers.filter( id=> externalIdentifierNames.contains(id.tok.name) )
      )
    }

    // стек
    val callStack = new CallStack()

    // стековые аргументы
    var stackArgs = lambdaAST.params.map( p =>
      p.name.tok.name -> StackedArgumentAST(callStack, p.name, typeScope(p.typeName.name))
    ).toMap

    var selfFn : Fun = null

    if( lambdaAST.recursion.isDefined ){
      //noinspection NotImplementedCode
      val fn = Fn(
        new Params(
          lambdaAST.params.map( p => {
            val stArg = stackArgs(p.name.tok.name)
            Param(stArg.argumentName,stArg.argumentType)
          })
        ),
        typeScope(lambdaAST.recursion.get.typeName.name)
      ).invoking( _ => ??? )

      val stAST = StackedArgumentAST(callStack, lambdaAST.recursion.get.name, fn)
      stackArgs = stackArgs + ( lambdaAST.recursion.get.name.tok.name -> stAST )
    }

    // замена IdentifierAST на StackedArgumentAST
    var bodyAst = lambdaAST.body
    var bodyIdentifiersCount = bodyIdentifiers.size
    while( bodyIdentifiers.nonEmpty ){
      if( debug ) {
        println(s"bodyIdentifiers size: ${bodyIdentifiers.size}")
        println(s"before")
        ASTDump.dump(bodyAst)
      }

      val from = bodyIdentifiers.head
      val to = stackArgs(from.tok.name)
      if( debug ){
        println( s"replace $from ---> $to" )
      }

      bodyAst = bodyAst.replace(from,to)
      if( debug ) {
        println(s"after")
        ASTDump.dump(bodyAst)
      }

      bodyIdentifiers = bodyAst.tree.map(_.last).
        filter( a => a.isInstanceOf[IdentifierAST] && !a.isInstanceOf[StackedArgumentAST] ).
        map( _.asInstanceOf[IdentifierAST] ).toList

      val bodyIdentifiersCountChanged = bodyIdentifiers.size
      if( bodyIdentifiersCountChanged >= bodyIdentifiersCount )
        throw ToasterError("internal bug!, check implementations of AST.replace in children")

      bodyIdentifiersCount = bodyIdentifiersCountChanged
    }

    // тело
    val bodyTast = compile(bodyAst)

    val fnImpl: Seq[Any] =>Any = args => {
      require(args!=null)

      require(
        lambdaAST.params.size == args.size
      )

      val stackArgs : Map[String,Any] = args.indices.map(argi => {
        val argName = lambdaAST.params(argi).name.tok.name
        val argValue = args(argi)
        argName -> argValue
      }).toMap

      val selfArg : Map[String,Any] = if( lambdaAST.recursion.isDefined ){
        Map( lambdaAST.recursion.get.name.tok.name -> selfFn )
      }else {
        Map()
      }

      val passArgs : Map[String,Any] = stackArgs ++ selfArg

      callStack.push(passArgs)
      try {
        bodyTast.supplier.get()
      } finally {
        callStack.pop()
      }
    }

    val retType: Type = bodyTast.supplierType
    val params: List[Param] = lambdaAST.params.map( p => {
      val stArg = stackArgs(p.name.tok.name)
      Param(stArg.argumentName, stArg.argumentType)
    })

    val fn = Fn( Params(params), retType ).invoking(fnImpl)

    if( lambdaAST.recursion.isDefined ){
      selfFn = fn
      val retType = typeScope.get(lambdaAST.recursion.get.typeName.name)
      if( retType.isEmpty ){
        throw ToasterError("return type not found")
      }else if( !retType.get.assignable(fn.returns) ){
        throw ToasterError("return type not matched")
      }
    }

    TAST( lambdaAST, fn,()=>fn, List(bodyTast))
  }

  private var pojoIdSeq = 0
  
  def compile( pojoAST: PojoAST ):TAST = {
    require(pojoAST!=null)
    pojoIdSeq+=1
  
    //val mmap = new java.util.LinkedHashMap[String,Any]()
    val typeObj = new TObject(s"Pojo$pojoIdSeq")
    val fields = pojoAST.items.map( itm => {
      val fldValue = compile(itm.value)
      val fld = new WriteableField(
        itm.key.tok.name, fldValue.supplierType,
        obj => {
          val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
          val name = itm.key.tok.name
          if( mmap.containsKey(name) ){
            mmap.get(name)
          } else {
            val computed = fldValue.supplier.get()
            mmap.put(name,computed)
            computed
          }
        },
        (obj,vl) => {
          val mmap = obj.asInstanceOf[java.util.Map[String,Any]]
          val name = itm.key.tok.name
          mmap.put(name,vl)
        }
      )
      (fld,fldValue,itm)
    })

    fields.foreach( fld => typeObj.fields.append(fld._1) )

    TAST(pojoAST, typeObj, ()=>{
      val mapObj = new util.LinkedHashMap[String,Any]()
      fields.foreach( f => mapObj.put(f._1.name, f._1.reading(mapObj)) )
      mapObj
    }, fields.map(f =>f._2) )
  }
}
