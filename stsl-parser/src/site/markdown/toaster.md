Toaster
===============

"Тостер" - это способ компиляции абстрактного синтаксического дерева (AST), 
в результате компиляции будет вычислены типы данных для каждого узла AST,
а так же способ вычисления результата исходного текста - 
будет возвращена функция для получения результата.

На вход в тостер подается AST дерево, 
на выходе из тостера получается TAST дерево 
и функция вычисления расположенная в корне дерева.

Пример использования "тостера" приведено в [readme](readme.md).

Далее в документе будет показано 
как преобразовывается исходный код (сценарий) в TAST дерево.

Литералы и бинарные операции
-----------------------------

Сценарий

    20 + 20 / 2

AST дерево

    AST
    BinaryAST +
    -| LiteralAST IntNumberTok 20
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 20
    -|-| LiteralAST IntNumberTok 2

TAST дерево

    BinaryAST + :: int
    -| LiteralAST IntNumberTok 20 :: int
    -| BinaryAST / :: int
    -|-| LiteralAST IntNumberTok 20 :: int
    -|-| LiteralAST IntNumberTok 2 :: int

Результат вычисления

    exec
    ------------------------------
    int
    30

Комментарии

- `exec` - это вывод из теста, а так же все что следует за ним.
- Вывод типов происходит от листовых элементов AST к корню дерева.
- Для литеральных значений (LiteralAST) тип данных определяется лексемой (в примере это IntNumberTok).
- Для всех лексем представляющих литеральное значение тип данных определен
- Для операторов (в примере BinaryAST) определен конечный тип данных, который зависит от типов аргументов
  - Так бинарный оператор `+` с аргументами типов `int`, `int` представлен функцией: `+( a:int, b:int):int`, где:
    - `+` - это название функции
    - `a:int` - это левый операнд и его тип
    - `b:int` - это правый операнд и его тип
    - `):int` - это тип результата
  - Таких операторов может быть несколько, например:
    - `+(a:string,b:string):string`
    - `+(a:string,b:int):string`
    - `+(a:int,b:string):string`
  - `int` - этот тип предопределен в классе `xyz.cofe.stsl.tast.JvmType`
  - операторы над типом `int` представлены как методы класса `int` в следующей форме:
    - `INT.methods += "+" -> Fn(Params("self" -> THIS, "value" -> BYTE),THIS).invoke[Int,Byte,Int]((self,value)=>self + value)`
      - `INT` - это тип `int`
      - `"+""` - это оператор сложения
      - `THIS` - это ссылка на "сам" тип, т.е. на `INT`
      - `"self" -> THIS` - это первый аргумент (левый операнд) с указанием типа `THIS` - т.е. `INT`.
      - `"value" -> BYTE` - это второй аргумент (правый операнд) с указанием типа `BYTE` - т.е. `byte`
      - `"value" -> BYTE),THIS` - в данном случае `THIS` - указывает на тип возвращаемого результата, т.е. на `INT`  
      - `self` играют такую же роль как и ключевое слово `this` в языках: Java, JS, C++, ...
      - `(self,value)=>self + value` - это реализация оператора.
      - В более простой и условной форме это можно записать так:
        - `+(self:THIS,value:BYTE):THIS`, а после замены `THIS` на `INT` будет следующее:
        - `+(self:INT,value:INT):INT`
    - Реализация бинарного оператора зависит от типа (класса) левого операнда, для которого есть одноименный метод реализующий операцию.
    - В пределах типа (класса) возможны полиморфные методы, которые уникальны по типам данных аргументов. 
      - То есть можно встретить для класса `int` следующие операторы:
        - `+(self:INT, value:BYTE):BYTE`
        - `+(self:INT, value:INT):INT`
      - А для класса `byte` такие операторы:
        - `+(self:BYTE, value:BYTE):INT` 
        - `+(self:BYTE, value:INT):INT` 

Скобочные конструкции
------------------------

Сценарий

    ( 10 - 5 ) * 5

AST дерево

    AST
    BinaryAST *
    -| DelegateAST
    -|-| BinaryAST -
    -|-|-| LiteralAST IntNumberTok 10
    -|-|-| LiteralAST IntNumberTok 5
    -| LiteralAST IntNumberTok 5

TAST дерево

    TAST
    BinaryAST * :: int
    -| BinaryAST - :: int
    -|-| LiteralAST IntNumberTok 10 :: int
    -|-| LiteralAST IntNumberTok 5 :: int
    -| LiteralAST IntNumberTok 5 :: int

Результат вычисления

    exec
    ------------------------------
    int
    25

Комментарии

- `exec` - это вывод из теста, а так же все что следует за ним.
- `DelegateAST` присутствует в AST и обозначает скобочную конструкцию, но отсутствует в TAST, т.к. он ничего не делает, а только передает результат вычислений вверх по дереву.

Передача переменных
-------------------------

Следующий пример связан с передачей переменных в сценарий.

Сценарий

    a + b * c

Подготовка + парсинг + toaster. Пример на языке Scala.

    // Перед компиляцией необходимо определить переменные и их тип данных
    // Компилятор попытаеться вывисти конечный тип данных 
    // и если не обнаружит переменные
    //   то сгенерирует ошибку компиляции
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

    // Попытка компиляции
    val tast = tst.compile(ast.get)

    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (12 + 13 * 2) == computedValue )

AST дерево

    AST
    BinaryAST +
    -| IdentifierAST IdentifierTok a
    -| BinaryAST *
    -|-| IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok c

TAST дерево

    TAST
    BinaryAST + :: int
    -| IdentifierAST IdentifierTok a :: int
    -| BinaryAST * :: int
    -|-| IdentifierAST IdentifierTok b :: int
    -|-| IdentifierAST IdentifierTok c :: int

Результат вычисления

    exec
    ------------------------------
    int
    38

- `exec` - это вывод из теста, а так же все что следует за ним.
- Переменные и их тип должны быть известны, до начала компиляции, наличие реальных значений не является необходимым.
- Компилятор проверяет наличие всех указанных переменных в области видимости переменных (`VarScope`)
- Если переменные не будут обнаружены, то компилятор откажется компилировать
- Если после компиляции удалить (или изменить тип) переменные из области видимости, тогда при выполнении `tast.supplier.get()` будет гарантирована ошибка - не определенное поведение.

Свойства объекта
-----------------------

В следующем сценарии передается пара объектов в сценарий  

Сценарий

    per1.age + per2.age

Подготовка + парсинг + toaster. Пример на языке Scala.

    // Определяем класс Персона
    // с одним полем age : Int
    class Person( var age:Int = 10 ) {}

    // Создаем описание класса
    // Название класса
    val userType = new TObject("Person");

    // Добавляем в класс поле age типа int
    userType.fields ++=
      "age" -> INT ->
        // прописываем способ чтения значения поля
        ((p:Any) => p.asInstanceOf[Person].age) ->
        // прописываем способ записи значения поля
        ((thiz:Any, pvalue:Any) => pvalue)

    // Создаем пару объектов
    val per1 = new Person(5)
    val per2 = new Person(7)

    // регистрируем их как переменные
    val vs = new VarScope();
    vs.put("per1" -> userType -> per1 )
    vs.put("per2" -> userType -> per2 )

    // создаем область типов 
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    // импорт класса в область типов 
    ts.imports(userType)

    // парсинг исходника
    val ast = Parser.parse("per1.age + per2.age")

    val tst = new Toaster(ts,vs)

    // компиляция
    val tast = tst.compile(ast.get)

    // интерпретация результата
    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == INT )
    assert( computedValue!=null && computedValue.isInstanceOf[Int] )
    assert( (per1.age + per2.age) == computedValue )

AST дерево

    AST
    BinaryAST +
    -| PropertyAST age
    -|-| IdentifierAST IdentifierTok per1
    -| PropertyAST age
    -|-| IdentifierAST IdentifierTok per2

TAST дерево

    TAST
    BinaryAST + :: int
    -| PropertyAST age :: int
    -|-| IdentifierAST IdentifierTok per1 :: Person
    -| PropertyAST age :: int
    -|-| IdentifierAST IdentifierTok per2 :: Person

Результат вычисления

    exec
    ------------------------------
    int
    12

- `exec` - это вывод из теста, а так же все что следует за ним.
- `IdentifierAST` - ссылается на переменную (per1, per2)
- `PropertyAST` - доступ к полю `age` класса `Person`
  - Процесс чтения немного не логичен на взгляд, но по сути нормальный.
    - С начала будет вычислена ссылка `IdentifierAST` на объект `per1`
    - Затем будет вычислено его поле `PropertyAST` ( `age` )

Вызов метода
--------------------------------------

Сценарий

    str.substring( 0, 3 )

Подготовка + парсинг + toaster. Пример на языке Scala.

    // Определяем переменную str
    // и регистрируем ее
    val vs = new VarScope();
    vs.put("str" -> STRING -> "string" )

    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion

    // парсинг исходника
    val ast = Parser.parse("str.substring( 0, 3 )")
    val tst = new Toaster(ts,vs)

    // компиляция
    val tast = tst.compile(ast.get)

    // интерпретация результата
    val computedValue =  tast.supplier.get()

    assert( tast.supplierType == STRING )
    assert( computedValue!=null && computedValue.isInstanceOf[String] )
    assert( "str" == computedValue )

AST дерево

    AST
    CallAST
    -| PropertyAST substring
    -|-| IdentifierAST IdentifierTok str
    -| LiteralAST IntNumberTok 0
    -| LiteralAST IntNumberTok 3

TAST дерево

    TAST
    CallAST substring() :: string
    -| IdentifierAST IdentifierTok str :: string
    -| LiteralAST IntNumberTok 0 :: int
    -| LiteralAST IntNumberTok 3 :: int

Результат вычисления

    exec
    ------------------------------
    string
    str

- Для типа STRING (`xyz.cofe.stsl.tast.JvmType$.STRING`) уже предопределены следующие методы и поля
  - `length:INT`
  - `substring(beginIndex:INT):STRING`
  - `substring(beginIndex:INT, endIndex:INT):STRING`
  - `+(value:STRING)`
  - `==(value:STRING)`
  - `!=(value:STRING)`
  - `<(value:STRING)`
  - `>(value:STRING)`
  - `<=(value:STRING)`
  - `>=(value:STRING)`

Вызов функции
-------------------------------------

Сценарий

    repeat( str, 2 )

Подготовка + парсинг + toaster. Пример на языке Scala.

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

AST дерево

    AST
    CallAST
    -| IdentifierAST IdentifierTok repeat
    -| IdentifierAST IdentifierTok str
    -| LiteralAST IntNumberTok 2

TAST дерево

    TAST
    CallAST :: string
    -| IdentifierAST IdentifierTok str :: string
    -| LiteralAST IntNumberTok 2 :: int

Результат вычисления

    exec
    ------------------------------
    string
    stringstring

Вызов лямбды
----------------------------------

Сценарий

    a:int , b:int => a+b

Подготовка + парсинг + toaster. Пример на языке Scala.

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

AST дерево

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

TAST дерево

    TAST
    LambdaAST :: (a:int,b:int):int
    -| BinaryAST + :: int
    -|-| StackedArgumentAST IdentifierTok a :: int
    -|-| StackedArgumentAST IdentifierTok b :: int

Результат вычисления

    exec
    ------------------------------
    (a:int,b:int):int
    (a:int,b:int):int
    call 1,2
    result = 3

Вызов рекурсивной лямбды
---------------------------------------

Сценарий

    a:int , r :: int => a < 0 ? 0 : r(a-1) + a

Подготовка + парсинг + toaster. Пример на языке Scala.

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

AST дерево

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

TAST дерево

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

Результат вычисления

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

Замыкания на данный момент не поддерживаются

Сценарий

    () => a+b

Подготовка + парсинг + toaster. Пример на языке Scala.

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

AST дерево

    AST
    LambdaAST
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b

TAST дерево

    xyz.cofe.stsl.tast.ToasterError: body contains external identifiers

Создание объекта
-----------------------------------------

Сценарий

    { k1: 1, k2: "abc" }

Подготовка + парсинг + toaster. Пример на языке Scala.

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

AST дерево

    AST
    PojoAST
    -| PojoItemAST k1
    -|-| LiteralAST IntNumberTok 1
    -| PojoItemAST k2
    -|-| LiteralAST StringTok abc

TAST дерево

    TAST
    PojoAST :: Pojo1
    -| LiteralAST IntNumberTok 1 :: int
    -| LiteralAST StringTok abc :: string

Результат вычисления

    ------------------------------
    Pojo1 extends any {
      k2 : string
      k1 : int
      /* extends any */
    }

