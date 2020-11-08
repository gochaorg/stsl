package xyz.cofe.sel

import xyz.cofe.sel.ast._
import xyz.cofe.sel.cmpl.rt._
import xyz.cofe.sel.tok._
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.types.{Fun, Param, Type}
import xyz.cofe.stsl.tok.{BigIntNumberTok, ByteNumberTok, DecimalNumberTok, DoubleNumberTok, FloatNumberTok, IntNumberTok, LongNumberTok, NumberTok, ShortNumberTok, StringTok}

/**
 * "Тостер" - Компиляция AST выражений
 */
class Toaster( val scope: Scope ) {
  require(scope!=null)

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
    }
  }
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
  def compile( identifierAST: IdentifierAST ):TAST = {
    require(identifierAST!=null)
    val variable = scope.vars.map.get(identifierAST.tok.name)
    if( variable.isEmpty )throw CompileError(s"undefined variable ${identifierAST.tok.name}")
    TAST( identifierAST, variable.get.varType, ()=>variable.get.value )
  }
  def compile( binaryAST: BinaryAST ):TAST = {
    require(binaryAST!=null)
    val left = compile(binaryAST.left)
    val right = compile(binaryAST.right)

    val opName = binaryAST.operator.tok.name
    val opFuns = scope.vars.map.get(opName)
    if( opFuns.isEmpty )throw CompileError(s"operator ${opName} not defined")

    val funs = opFuns.get.value match {
      case funs1: Funs => funs1
      case _ => throw CompileError(s"variable ${opName} not instance of Funs (collections of functions)")
    }

    compile(binaryAST,opName,funs,List(left,right)).tast
  }
  def compile( ternaryAST: TernaryAST ):TAST = {
    require(ternaryAST!=null)
    val cond = compile(ternaryAST.first)
    if( !BOOL.assignable(cond.supplierType) )throw CompileError("condition not "+BOOL.name, ternaryAST.first);

    val succ = compile(ternaryAST.second)
    val fail = compile(ternaryAST.third)
    if( !succ.supplierType.assignable(fail.supplierType) )throw CompileError(
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
            throw new RuntimeException("condition return not " + BOOL.name)
        }
      }else{
        fail.supplier.get()
      }
    }, List(cond,succ,fail))
  }
  def compile( delegateAST: DelegateAST ):TAST = compile( delegateAST.target )
  def compile( propertyAST: PropertyAST ):TAST = {
    require(propertyAST!=null)

    val obj = compile(propertyAST.obj)
    val pname = propertyAST.name.tok.text
    val objType = obj.supplierType
    val prop = objType.properties(pname)
    if( prop==null ){
      throw CompileError(s"property ${pname} not found in ${objType}", propertyAST.name)
    }

    TAST(propertyAST, prop.propertyType, ()=>{
      val objInst = obj.supplier.get()
      prop.read(objInst)
    },List(obj))
  }
  def compile( callAST: CallAST ):TAST = {
    require(callAST!=null)
    callAST.callable match {
      case fname:StackedArgumentAST => compileFunCall(callAST,fname)
      case fname:IdentifierAST => compileFunCall(callAST,fname.tok.text)
      case prop:PropertyAST => compileMethCall(callAST,prop.obj,prop.name.tok.text)
    }
  }
  def compile( ast:AST, functionName:String, funs:Funs, arguments:List[TAST] ) = {
    require(ast!=null)
    require(funs!=null)
    require(functionName!=null)
    require(arguments!=null)
    new {
      lazy val implFuns : Funs = {
        val implVar = scope.vars.map.get(PredefFun.IMPLICIT_PARAM_TYPE_CONV)
        if( implVar.isDefined && implVar.get.value.isInstanceOf[Funs] ){
          implVar.get.value.asInstanceOf[Funs]
        }else{
          null
        }
      }
      lazy val invokes: immutable.Seq[Invokable] = {
        val argumentsType = arguments.map( _.supplierType )
        funs.find.sameArgs(argumentsType)(implFuns).preferred
      }
      lazy val invoke: Invokable = {
        val argumentsType = arguments.map( _.supplierType )
        if( invokes.isEmpty ){
          throw CompileError(s"function ${functionName} not found for arguments: ${argumentsType}")
        } else if( invokes.size>1 ){
          val invokesDesc = invokes.map(_.toString).reduce((a,b)=>a+"\n"+b)
          throw CompileError(s"found ambiguous functions ${functionName} not found for operands: ${argumentsType}\n${invokesDesc}")
        }

        invokes.head
      }
      def callArgs( readArg:(Int,TAST)=>Any ):List[Any] = {
        require(readArg!=null)
        if( invoke.implicitParams.isEmpty ) {
          arguments.zipWithIndex.map { case (tast, argi) =>
            readArg(argi,tast)
          }
        }else{
          arguments.zipWithIndex.map { case (tast, argi) =>
            if( argi<invoke.implicitParams.length && invoke.implicitParams(argi)!=null ){
              invoke.implicitParams(argi).call(List(readArg(argi,tast)))
            }else{
              readArg(argi,tast)
            }
          }
        }
      }
      lazy val tast: TAST = {
        TAST( ast, invoke.fn.returnType, ()=>invoke.fn.call(
          callArgs(
            (argi,tast) => tast.supplier.get()
          )
        ), arguments )
      }
    }
  }
  private def compileFunCall( callAST: CallAST, callable:StackedArgumentAST ):TAST = {
    def call = compile(callable)

    if( !call.supplierType.isInstanceOf[Fun] )
      throw CompileError("callable not function",callable)

    val funs1:Funs = new Funs(List(call.supplierType.asInstanceOf[Fun]))
    val arguments = callAST.arguments.map( a => compile(a) )

    val fcompile = compile( callAST, callable.argumentName, funs1, arguments )

    TAST( callAST, fcompile.tast.supplierType,
      () => {
        val fun:Fun = callable.value.asInstanceOf[Fun]
        fun.call(
          fcompile.callArgs( (argi,tast) => tast.supplier.get() )
        )
      }, fcompile.tast.children
    )
  }
  private def compileFunCall( callAST: CallAST, funtionName:String ):TAST = {
    val fnVar = scope.vars.map.get(funtionName)
    if( fnVar.isEmpty ) throw CompileError(s"function ${funtionName} not defined")
    if( fnVar.get.value==null ) throw CompileError(s"function ${funtionName} reference is null")
    if( !fnVar.get.value.isInstanceOf[Funs] ) throw CompileError(s"not function ${funtionName}")

    val funs : Funs = fnVar.get.value.asInstanceOf[Funs]
    val arguments = callAST.arguments.map( a => compile(a) )
    compile( callAST, funtionName, funs, arguments ).tast
  }
  private def compileMethCall( callAST: CallAST, objAst:AST, methodName:String ):TAST = {
    val obj = compile(objAst)
    val funs = obj.supplierType.methods.get(methodName)
    if( funs.isEmpty ){
      throw CompileError(s"method $methodName not defined in ${obj.supplierType}")
    }
    val arguments = obj :: callAST.arguments.map( a => compile(a) )
    compile( callAST, methodName, funs.get, arguments ).tast
  }
  def compile( stackedArgumentAST: StackedArgumentAST ):TAST = {
    require(stackedArgumentAST!=null)
    TAST( stackedArgumentAST, stackedArgumentAST.argumentType, ()=>stackedArgumentAST.value )
  }
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
    val undefinedTypes = (lambdaAST.params.map( p => (p.typeName, scope.types.get(p.typeName.name)) ) ++ (
      if( lambdaAST.recursion.isEmpty ) {
        List()
      } else {
        List((lambdaAST.recursion.get.typeName, scope.types.get(lambdaAST.recursion.get.typeName.name)))
      }
    )).filter( p => p._2.isEmpty )

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
      p.name.tok.name -> StackedArgumentAST(callStack, p.name, scope.types(p.typeName.name))
    ).toMap

    var selfFn : Fun = null

    if( lambdaAST.recursion.isDefined ){
      //noinspection NotImplementedCode
      val fn = new Fun(
        args => ???,
        lambdaAST.params.map( p => {
          val stArg = stackArgs(p.name.tok.name)
          Param(stArg.argumentName, stArg.argumentType)
        }),
        scope.types(lambdaAST.recursion.get.typeName.name)
      )

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
        throw CompileError("internal bug!, check implementations of AST.replace in children")

      bodyIdentifiersCount = bodyIdentifiersCountChanged
    }

    // тело
    val bodyTast = compile(bodyAst)

    val fnImpl:(List[Any])=>Any = (args) => {
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

    val fn = new Fun( fnImpl, params, retType  )

    if( lambdaAST.recursion.isDefined ){
      selfFn = fn
      val retType = scope.types.get(lambdaAST.recursion.get.typeName.name)
      if( retType.isEmpty ){
        throw CompileError("return type not found")
      }else if( !retType.get.assignable(fn.returnType) ){
        throw CompileError("return type not matched")
      }
    }

    TAST( lambdaAST, fn,()=>fn, List(bodyTast))
  }
}

object Toaster {
  def apply(ws: Scope): Toaster = new Toaster(ws)
}
