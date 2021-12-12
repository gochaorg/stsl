Toaster
===============

Литералы и бинарные операции
-----------------------------

Сценарий

    20 + 20 / 2

Результат

    AST
    BinaryAST +
    -| LiteralAST IntNumberTok 20
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 20
    -|-| LiteralAST IntNumberTok 2
    TAST
    BinaryAST + :: int
    -| LiteralAST IntNumberTok 20 :: int
    -| BinaryAST / :: int
    -|-| LiteralAST IntNumberTok 20 :: int
    -|-| LiteralAST IntNumberTok 2 :: int
    exec
    ------------------------------
    int
    30

Скобочные конструкции
------------------------

Сценарий

    ( 10 - 5 ) * 5

Результат

    AST
    BinaryAST *
    -| DelegateAST
    -|-| BinaryAST -
    -|-|-| LiteralAST IntNumberTok 10
    -|-|-| LiteralAST IntNumberTok 5
    -| LiteralAST IntNumberTok 5
    TAST
    BinaryAST * :: int
    -| BinaryAST - :: int
    -|-| LiteralAST IntNumberTok 10 :: int
    -|-| LiteralAST IntNumberTok 5 :: int
    -| LiteralAST IntNumberTok 5 :: int
    exec
    ------------------------------
    int
    25

Передача переменных
-------------------------

Сценарий

    a + b * c

Подготовка + парсинг + toaster

    val vs = new VarScope();
    vs.put(
        "a" -> INT -> 12,
        "b" -> INT -> 13,
        "c" -> INT -> 2,
    )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("a + b * c")
    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)

    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (12 + 13 * 2) == computedValue )

Результат

    AST
    BinaryAST +
    -| IdentifierAST IdentifierTok a
    -| BinaryAST *
    -|-| IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok c
    TAST
    BinaryAST + :: int
    -| IdentifierAST IdentifierTok a :: int
    -| BinaryAST * :: int
    -|-| IdentifierAST IdentifierTok b :: int
    -|-| IdentifierAST IdentifierTok c :: int
    exec
    ------------------------------
    int
    38

Свойства объекта
-----------------------

Сценарий

    per1.age + per2.age

Подготовка + парсинг + toaster

    class Person( var age:Int = 10 ) {}

    val userType = new TObject("Person");
    userType.fields ++=
      "age" -> INT ->
        ((p:Any) => p.asInstanceOf[Person].age) ->
        ((thiz:Any, pvalue:Any) => pvalue)

    val per1 = new Person(5)
    val per2 = new Person(7)

    val vs = new VarScope();
    vs.put("per1" -> userType -> per1 )
    vs.put("per2" -> userType -> per2 )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(userType)

    val ast = Parser.parse("per1.age + per2.age")

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)

    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (per1.age + per2.age) == computedValue )

Результат

    AST
    BinaryAST +
    -| PropertyAST age
    -|-| IdentifierAST IdentifierTok per1
    -| PropertyAST age
    -|-| IdentifierAST IdentifierTok per2
    TAST
    BinaryAST + :: int
    -| PropertyAST age :: int
    -|-| IdentifierAST IdentifierTok per1 :: Person
    -| PropertyAST age :: int
    -|-| IdentifierAST IdentifierTok per2 :: Person
    exec
    ------------------------------
    int
    12

Вызов метода
--------------------------------------

    str.substring( 0, 3 )

    val vs = new VarScope();
    vs.put("str" -> STRING -> "string" )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("str.substring( 0, 3 )")
    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)

    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == STRING )
    assert( computedValue!=null && computedValue.isInstanceOf[String] )
    assert( "str" == computedValue )

    AST
    CallAST
    -| PropertyAST substring
    -|-| IdentifierAST IdentifierTok str
    -| LiteralAST IntNumberTok 0
    -| LiteralAST IntNumberTok 3
    TAST
    CallAST substring() :: string
    -| IdentifierAST IdentifierTok str :: string
    -| LiteralAST IntNumberTok 0 :: int
    -| LiteralAST IntNumberTok 3 :: int
    exec
    ------------------------------
    string
    str

Вызов функции
-------------------------------------

    repeat( str, 2 )

    val repeatFn = Fn(Params("str" -> STRING, "count" -> INT),STRING).invoke[String,Int,String]((str:String,cnt:Int)=>str*cnt)

    val vs = new VarScope();
    vs.put("str" -> STRING -> "string" )
    vs.put("repeat" -> repeatFn -> repeatFn )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("repeat( str, 2 )")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( tast.supplierType == STRING )
    assert( computedValue!=null && computedValue.isInstanceOf[String] )
    assert( "stringstring" == computedValue )

    AST
    CallAST
    -| IdentifierAST IdentifierTok repeat
    -| IdentifierAST IdentifierTok str
    -| LiteralAST IntNumberTok 2
    TAST
    CallAST :: string
    -| IdentifierAST IdentifierTok str :: string
    -| LiteralAST IntNumberTok 2 :: int
    exec
    ------------------------------
    string
    stringstring

Вызов лямбды
----------------------------------

    println("lambdaCall01")
    println("="*30)

    val vs = new VarScope();

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("a:int , b:int => a+b")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( computedValue!=null && computedValue.isInstanceOf[CallableFn] )

    println("call 1,2")
    val lambdaComputedValue = computedValue.asInstanceOf[CallableFn].invoke(List(1,2))

    println(s"result = $lambdaComputedValue")
    assert(lambdaComputedValue!=null)
    assert(lambdaComputedValue.isInstanceOf[Int])
    assert(3 == lambdaComputedValue)

    AST
    LambdaAST
    -| ParamAST IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok a
    -|-| TypeNameAST int
    -| ParamAST IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok b
    -|-| TypeNameAST int
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b
    TAST
    LambdaAST :: (a:int,b:int):int
    -| BinaryAST + :: int
    -|-| StackedArgumentAST IdentifierTok a :: int
    -|-| StackedArgumentAST IdentifierTok b :: int
    exec
    ------------------------------
    (a:int,b:int):int
    (a:int,b:int):int
    call 1,2
    result = 3

Вызов рекурсивной лямбды
---------------------------------------

    println("lambdaCall02")
    println("="*30)

    val vs = new VarScope();

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("a:int , r :: int => a < 0 ? 0 : r(a-1) + a")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    //test(ast, call, property, identifier, literal, literal)

    val tst = new Toaster(ts,vs)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    println("exec")
    println("-"*30)

    println( tast.supplierType )

    val computedValue =  tast.supplier.get()
    println( computedValue )

    assert( computedValue!=null && computedValue.isInstanceOf[CallableFn] )

    List(
      (1,1),
      (2,3),
      (3,6),
      (4,10),
      (5,15),
    ).foreach({case(input,resultExpect)=>
      val lambdaComputedValue = computedValue.asInstanceOf[CallableFn].invoke(List(input))
      println(s"call $input result = $lambdaComputedValue")
      assert(lambdaComputedValue!=null)
      assert(lambdaComputedValue.isInstanceOf[Int])
      assert(resultExpect == lambdaComputedValue)
    })

    AST
    LambdaAST recursion: ParamAST IdentifierAST IdentifierTok r
    -| ParamAST IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok a
    -|-| TypeNameAST int
    -| ParamAST IdentifierAST IdentifierTok r
    -|-| IdentifierAST IdentifierTok r
    -|-| TypeNameAST int
    -| TernaryAST ? :
    -|-| BinaryAST <
    -|-|-| IdentifierAST IdentifierTok a
    -|-|-| LiteralAST IntNumberTok 0
    -|-| LiteralAST IntNumberTok 0
    -|-| BinaryAST +
    -|-|-| CallAST
    -|-|-|-| IdentifierAST IdentifierTok r
    -|-|-|-| BinaryAST -
    -|-|-|-|-| IdentifierAST IdentifierTok a
    -|-|-|-|-| LiteralAST IntNumberTok 1
    -|-|-| IdentifierAST IdentifierTok a
    TAST
    LambdaAST recursion: ParamAST IdentifierAST IdentifierTok r :: (a:int):int
    -| TernaryAST ? : :: int
    -|-| BinaryAST < :: bool
    -|-|-| StackedArgumentAST IdentifierTok a :: int
    -|-|-| LiteralAST IntNumberTok 0 :: int
    -|-| LiteralAST IntNumberTok 0 :: int
    -|-| BinaryAST + :: int
    -|-|-| CallAST :: int
    -|-|-|-| BinaryAST - :: int
    -|-|-|-|-| StackedArgumentAST IdentifierTok a :: int
    -|-|-|-|-| LiteralAST IntNumberTok 1 :: int
    -|-|-| StackedArgumentAST IdentifierTok a :: int
    exec
    ------------------------------
    (a:int):int
    (a:int):int
    call 1 result = 1
    call 2 result = 3
    call 3 result = 6
    call 4 result = 10
    call 5 result = 15


Вызов замыкания
------------------------------

    println("lambdaCall03")
    println("="*30)

    val vs = new VarScope();
    vs.put("a", INT, 1)
    vs.put("b", INT, 1)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    ts.imports(List(INT))

    val ast = Parser.parse("() => a+b")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )

    var catched = false
    try {
      val tst = new Toaster(ts, vs)
      val tast = tst.compile(ast.get)
    } catch {
      case err:ToasterError =>
        println(err)
        catched = true
    }

    assert(catched)

    AST
    LambdaAST
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b
    xyz.cofe.stsl.tast.ToasterError: body contains external identifiers

Создание объекта
-----------------------------------------

    println("objDef01")
    println("="*30)

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    val ast = Parser.parse("{ k1: 1, k2: \"abc\"}")
    println("AST")
    ast.foreach( ASTDump.dump )
    assert( ast.isDefined )
    test(ast, pojo, pojoItem, literal, pojoItem, literal)

    val tst = new Toaster(ts)
    val tast = tst.compile(ast.get)
    assert(tast!=null)
    println("TAST")
    TASTDump.dump(tast)

    //println("exec")
    println("-"*30)
    println(TypeDescriber.describe(tast.supplierType))

    AST
    PojoAST
    -| PojoItemAST k1
    -|-| LiteralAST IntNumberTok 1
    -| PojoItemAST k2
    -|-| LiteralAST StringTok abc
    TAST
    PojoAST :: Pojo1
    -| LiteralAST IntNumberTok 1 :: int
    -| LiteralAST StringTok abc :: string
    ------------------------------
    Pojo1 extends any {
      k2 : string
      k1 : int
      /* extends any */
    }

