Type inference
===============================

    class User(var name:String = "unnamed") {
    override def toString: String = {
    "User(name=\""+name+"\")"
    }
    }

    val userType = TObject("User").build
    userType.fields ++= "name" -> STRING ->
    ((usr:Any)=>usr.asInstanceOf[User].name) ->
    ((usr:Any,value:Any)=>usr.asInstanceOf[User].name = value.asInstanceOf[String])
    userType.freeze


    class Person(name:String="unnamedPerson", var age:Int = 10) extends User(name)
    
    val personType = TObject("Person").build
    personType.extend(userType)
    personType.fields ++= "age" -> INT ->
    ((usr:Any)=>usr.asInstanceOf[Person].age) ->
    ((usr:Any,value:Any)=>usr.asInstanceOf[Person].age = value.asInstanceOf[Int])
    personType.freeze

    class AList[A]( init:List[A]=List() ) {
    require(init!=null)
    protected var list : List[A] = init
    
        def size():Int = list.size
        def append(a:A):Unit = {
          require(a!=null)
          list = (a :: list).reverse
        }
        def clear():Unit = {
          list = List()
        }
        def filter(f:A=>Boolean):AList[A] = {
          require(f!=null)
          new AList[A]( list.filter(f) )
        }
        def map[B](f:A=>B):AList[B] = {
          require(f!=null)
          new AList[B]( list.map(f) )
        }
    
        override def toString: String = {
          val sb = new StringBuilder
          sb.append("AList(")
          if( list.nonEmpty ){
            sb.append(list.map(_.toString).reduce((a,b)=>a+","+b))
          }
          sb.append(")")
          sb.toString()
        }
    }

    val listType = TObject("List").build
    listType.generics.append.any("A")
    //noinspection NotImplementedCode
    listType.fields ++= "size" -> INT ->
    ((usr:Any) => usr.asInstanceOf[AList[_]].size() ) ->
    ((usr:Any,_) => ???)
    listType.methods += "add" -> Fn(
    Params(
    "self" -> THIS,
    "item" -> TypeVariable("A", THIS)
    ),
    VOID
    ).invoking(args=>{
    args.head.asInstanceOf[AList[Any]].append(args(1))
    })
    listType.methods += "clear" -> Fn(
    Params(
    "self" -> THIS
    ),
    VOID
    ).invoking(args=>{
    args.head.asInstanceOf[AList[_]].clear()
    })
    listType.methods += "filter" -> Fn(
    Params(
    "self" -> THIS,
    "f" -> Fn(Params("a" -> TypeVariable("A", THIS)),BOOLEAN)
    ),
    THIS
    ).invoking(args=>{
    //args.head.asInstanceOf[AList[_]].clear()
    args.head.asInstanceOf[AList[Any]].filter( el => {
    args(1).asInstanceOf[CallableFn].invoke(List(el)).asInstanceOf[Boolean]
    })
    })
    listType.methods += "map" -> Fn(
    GenericParams(AnyVariant("B")),
    Params(
    "self" -> THIS,
    "f" -> Fn(
    GenericParams(AnyVariant("B")),
    Params("a" -> TypeVariable("A", THIS)),
    TypeVariable("B",FN)
    )
    ),
    new GenericInstance[TObject](Map("A"->TypeVariable("B",FN)),listType)
    ).invoking(args=>{
    //args.head.asInstanceOf[AList[_]].clear()
    args.head.asInstanceOf[AList[Any]].map( el => {
    args(1).asInstanceOf[CallableFn].invoke(List(el))
    })
    })
    listType.freeze

Вызов лямбды, для фильтрации данных
------------------------------------

сценарий

    x : User => x.name.length > 2

код

    println("test01()")
    println("="*40)

    println("source types")
    println("-"*30)
    println(describe(userType))
    println(describe(personType))
    println(describe(listType))

    println("\nlist type var replace")
    println("-"*30)
    val userListType = listType.typeVarReplace("A" -> userType)
    println(describe(userListType))

    val typeScope = new TypeScope()
    typeScope.imports(List(userType, personType, listType))

    val varScope = new VarScope()
    val filterSourceLambda = s"x : ${userType.name} => x.name.length > 2"
    val filterAst = Parser.parse(filterSourceLambda)
    assert(filterAst.isDefined)

    val toaster = new Toaster(typeScope, varScope)

    val filterTast = toaster.compile(filterAst.get)
    println(s"filter source: ${filterSourceLambda}")
    println(s"filter type: ${filterTast.supplierType}")

    println("\ntry filter")
    println("-"*30)

    val userList1 = new AList[User](
      List(
        new User("Vova"),
        new User("Yu"),
        new User("Peter"),
      )
    )

    varScope.put("userList1",userListType,userList1)
    val filterUserList1Fun = userListType.methods("filter").funs.head.asInstanceOf[CallableFn]
    println( filterUserList1Fun )

    // direct call
    val filterRes = filterUserList1Fun.invoke(List(userList1, filterTast.supplier.get()))
    println(filterRes)

    // call case
    println("\nfind call case")
    println("-"*30)
    val callCase = toaster.call( userListType, "filter", List(userListType, filterTast.supplierType) )
    println( callCase )

    println("\ninvoking")
    println("-"*30)
    val invk = callCase.invoking()
    val invkRes = invk._1.invoke(List(userList1, filterTast.supplier.get()))
    println(invkRes)

Результат

    source types
    ------------------------------
    User extends any {
    name : string
    /* extends any */
    }
    Person extends User {
    age : int
    /* extends User */
    name : string
    /* extends any */
    }
    List[A] extends any {
    size : int
    add(self:THIS,item:A):void
    clear(self:THIS):void
    filter(self:THIS,f:(a:A):bool):THIS
    map[B](self:THIS,f:[B](a:A):B):List[A=B]
    /* extends any */
    }
    
    list type var replace
    ------------------------------
    List extends any {
    size : int
    add(self:THIS,item:User):void
    clear(self:THIS):void
    filter(self:THIS,f:(a:User):bool):THIS
    map[B](self:THIS,f:[B](a:User):B):List[A=B]
    /* extends any */
    }
    filter source: x : User => x.name.length > 2
    filter type: (x:User):bool
    
    try filter
    ------------------------------
    (self:THIS,f:(a:User):bool):THIS
    AList(User(name="Vova"),User(name="Peter"))
    
    find call case
    ------------------------------
    fun=(self:THIS,f:(a:User):bool):THIS
    callable= true
    passable= true
    cost=     Some(1)
    passing=  Some(List(as-is, co-variant List((a:User):bool)))
    actual=   List(List, (a:User):bool)
    expected= List(List, (x:User):bool)
    result=   List
    
    invoking
    ------------------------------
    AList(User(name="Vova"),User(name="Peter"))
    Disconnected from the target VM, address: '127.0.0.1:53011', transport: 'socket'
    

Передача лямбды в метод, тип результата определен 
------------------------------------------------

сценарий

    lst.filter( x : User => x.name.length > 2 )

код

    val src = s"lst.filter( x : ${userType.name} => x.name.length > 2 )"
    println("source: "+"-"*20)
    println(src)

    val ast = Parser.parse(src)
    println("\nast: "+"-"*20)
    ast.foreach( ASTDump.dump )

    val typeScope = new TypeScope
    typeScope.imports(List(STRING,INT))
    typeScope.imports(List(userType,listType))

    val userListGenInstType = new GenericInstance( Map("A" -> userType), listType )
    typeScope.imports(userListGenInstType)
    println(s"generic instance: $userListGenInstType")

    val userList1 = new AList[User](
      List(
        new User("Vova"),
        new User("Yu"),
        new User("Peter"),
      )
    )

    val varScope = new VarScope
    varScope.put("lst", userListGenInstType, userList1)

    val to = userListGenInstType.source.typeVarBake.thiz(userListGenInstType.recipe)
    println("baked:")
    println(describe(to))

    println("\ntry tast compile:")
    val toaster = new Toaster(typeScope,varScope)
    val tast = toaster.compile(ast.get)
    println("TAST")
    TASTDump.dump(tast)

    println(tast.supplier.get())

результат

    source: --------------------
    lst.filter( x : User => x.name.length > 2 )
    
    ast: --------------------
    CallAST
    -| PropertyAST filter
    -|-| IdentifierAST IdentifierTok lst
    -| LambdaAST
    -|-| ParamAST IdentifierAST IdentifierTok x
    -|-|-| IdentifierAST IdentifierTok x
    -|-|-| TypeNameAST User
    -|-| BinaryAST >
    -|-|-| PropertyAST length
    -|-|-|-| PropertyAST name
    -|-|-|-|-| IdentifierAST IdentifierTok x
    -|-|-| LiteralAST IntNumberTok 2
    generic instance: List[A=User]
    baked:
    List extends any {
    size : int
    add(self:THIS,item:User):void
    clear(self:THIS):void
    filter(self:THIS,f:(a:User):bool):THIS
    map[B](self:THIS,f:[B](a:User):B):List[A=B]
    /* extends any */
    }
    
    try tast compile:
    TAST
    CallAST filter() :: List[A=User]$
    -| IdentifierAST IdentifierTok lst :: List[A=User]$
    -| LambdaAST :: (x:User):bool
    -|-| BinaryAST > :: bool
    -|-|-| PropertyAST length :: int
    -|-|-|-| PropertyAST name :: string
    -|-|-|-|-| StackedArgumentAST IdentifierTok x :: User
    -|-|-| LiteralAST IntNumberTok 2 :: int
    AList(User(name="Vova"),User(name="Peter"))
Передача лямбды в метод, тип результата зависит от лямбды
---------------------------------------------------------

сценарий
    
    1. x : User => x.name
    2. lst.map( x : User => x.name )

код

    println("map01()")
    println("="*40)

    println("types:")
    println(describe(listType))
    println(describe(userType))

    println("\nmap:")
    val mapFn = listType.methods("map").head
    println(mapFn)

    val userList1 = new AList[User](
      List(
        new User("Vova"),
        new User("Yu"),
        new User("Peter"),
      )
    )

    val userListGenInstType = new GenericInstance( Map("A" -> userType), listType )

    val typeScope = new TypeScope
    typeScope.imports(List(STRING,INT))
    typeScope.imports(List(userType,listType))

    val varScope = new VarScope
    varScope.put("lst", userListGenInstType, userList1)

    val toaster = new Toaster(typeScope,varScope)

    /////////////////
    val mapLmbSrc = "x : User => x.name"
    println(s"\nlambda source: $mapLmbSrc")
    val mapLmbAST = Parser.parse(mapLmbSrc)

    println("ast:")
    mapLmbAST.foreach(ASTDump.dump)

    val mapLmbTAST = toaster.compile(mapLmbAST.get)
    println("tast:")
    TASTDump.dump(mapLmbTAST)

    println("\ngeneric instance:")
    val userListType = userListGenInstType.source.typeVarBake.thiz("A"->userType)
    println(describe(userListType))

    val listMapFn = userListType.methods("map").head

    println("\nlist map fn:")
    println( "  "+listMapFn )
    println( "  generics:"+listMapFn.generics )
    println( "  returns:"+listMapFn.returns )
    println( "  typeVarFetch:" )

    val typeVarLocators = listMapFn.typeVarFetch()
    typeVarLocators.foreach( tvl => {
      println(s"  locator ${tvl} t.var ${tvl.typeVar}")
      println(s"    resolve ${tvl.resolve(listMapFn)}")
    })

    val mapParam = mapLmbTAST.supplierType.asInstanceOf[Fun]
    println("\nmap param: "+mapParam)

    val tvlParam1 = typeVarLocators.head
    val tvlParam2 = new TypeVarLocator(tvlParam1.typeVar, tvlParam1.path.dropRight(2))
    println( s"  type param relative fn param: $tvlParam2" )

    val trgtType = tvlParam2.resolve(mapParam)
    println(s"  target type: $trgtType")

    println("\ntypeVarBake:")
    val usrListMapFn = listMapFn.typeVarBake.fn("B" -> trgtType.get )
    println( "  "+usrListMapFn )

    println("\nLocatorItem parse:")
    val tvlItms = LocatorItem.parse(tvlParam1.path)
    tvlItms.get.toList.zipWithIndex.foreach { case(li,lii) =>
      val ident = ("_"*(lii+1))
      li match {
        case fp: LocatorItemFunParam => println( ident+ s"fp -> ${fp.param.name}")
        case fr: LocatorItemFunResult => println( ident+ s"fr -> ${fr.fun.returns}")
        case gi: LocatorItemGenericInstance[_] => println( ident+ s"gi -> ${gi.gi.recipe(gi.param)}")
      }
    }

    if( tvlItms.isDefined ){
      println(s"Locator item resolving from ${listMapFn}")
      println(s"  generics: ${listMapFn.generics}")

      val trgt = tvlItms.get.resolve(listMapFn,List(userListType, mapParam))
      println(s"resolved as ${trgt}")
    }

    typeVarLocators.map(tvl => LocatorItem.parse(tvl.path))
      .filter(li=>li.isDefined)
      .map(li=>li.get)
      .foreach(li=>{
        val trgt = li.resolve(listMapFn,List(userListType, mapParam))
        println(s"Locator ${li}")
        println(trgt)
      })

    println("- "*30)
    val mapListSrc = "lst.map( x : User => x.name )"

    println("source: "+mapListSrc)
    val mapListAst = Parser.parse(mapListSrc)

    println("ast:")
    mapListAst.foreach(ASTDump.dump)

    val mapListTAST = toaster.compile(mapListAst.get)
    println("tast:")
    TASTDump.dump(mapListTAST)

    val res = mapListTAST.supplier.get()
    println("\ncall")
    println(res)

Результат

    types:
    List[A] extends any {
    size : int
    add(self:THIS,item:A):void
    clear(self:THIS):void
    filter(self:THIS,f:(a:A):bool):THIS
    map[B](self:THIS,f:[B](a:A):B):List[A=B]
    /* extends any */
    }
    User extends any {
    name : string
    /* extends any */
    }
    
    map:
    [B](self:THIS,f:[B](a:A):B):List[A=B]
    
    lambda source: x : User => x.name
    ast:
    LambdaAST
    -| ParamAST IdentifierAST IdentifierTok x
    -|-| IdentifierAST IdentifierTok x
    -|-| TypeNameAST User
    -| PropertyAST name
    -|-| IdentifierAST IdentifierTok x
    tast:
    LambdaAST :: (x:User):string
    -| PropertyAST name :: string
    -|-| StackedArgumentAST IdentifierTok x :: User
    
    generic instance:
    List extends any {
    size : int
    add(self:THIS,item:User):void
    clear(self:THIS):void
    filter(self:THIS,f:(a:User):bool):THIS
    map[B](self:THIS,f:[B](a:User):B):List[A=B]
    /* extends any */
    }
    
    list map fn:
    [B](self:THIS,f:[B](a:User):B):List[A=B]
    generics:[B]
    returns:List[A=B]
    typeVarFetch:
    locator TVarLocator[fn [B](self:THIS,f:[B](a:User):B):List[A=B]|param f:[B](a:User):B|fn [B](a:User):B|returns] t.var B
    resolve Some(B)
    locator TVarLocator[fn [B](self:THIS,f:[B](a:User):B):List[A=B]|returns|gi List[A=B]|A] t.var B
    resolve Some(B)
    
    map param: (x:User):string
    type param relative fn param: TVarLocator[fn [B](a:User):B|returns]
    target type: Some(string)
    
    typeVarBake:
    (self:THIS,f:(a:User):string):List[A=string]
    
    LocatorItem parse:
    _fp -> f
    __fr -> B
    Locator item resolving from [B](self:THIS,f:[B](a:User):B):List[A=B]
    generics: [B]
    resolved as Some(string)
    Locator LocatorItemFunParam([B](self:THIS,f:[B](a:User):B):List[A=B],f:[B](a:User):B,Some(LocatorItemFunResult([B](a:User):B,None)))
    Some(string)
    Locator LocatorItemFunResult([B](self:THIS,f:[B](a:User):B):List[A=B],Some(LocatorItemGenericInstance(List[A=B],A,None)))
    Some(B)
    - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - 
    source: lst.map( x : User => x.name )
    ast:
    CallAST
    -| PropertyAST map
    -|-| IdentifierAST IdentifierTok lst
    -| LambdaAST
    -|-| ParamAST IdentifierAST IdentifierTok x
    -|-|-| IdentifierAST IdentifierTok x
    -|-|-| TypeNameAST User
    -|-| PropertyAST name
    -|-|-| IdentifierAST IdentifierTok x
    tast:
    CallAST map() :: List[A=string]
    -| IdentifierAST IdentifierTok lst :: List[A=User]$
    -| LambdaAST :: (x:User):string
    -|-| PropertyAST name :: string
    -|-|-| StackedArgumentAST IdentifierTok x :: User
    
    call
    AList(Vova,Yu,Peter)



Генерация нового типа из лямбды
-------------------------------------

код

    val typeScope = new TypeScope
    typeScope.implicits = JvmType.implicitConversion
    typeScope.imports(List(STRING,INT))
    typeScope.imports(List(userType,listType))

    val userListGenInstType = new GenericInstance( Map("A" -> userType), listType )
    typeScope.imports(userListGenInstType)
    println(s"generic instance: $userListGenInstType")
    println(TypeDescriber.describe(userListGenInstType))

    val userList1 = new AList[User](
      List(
        new User("Vova"),
        new User("Yu"),
        new User("Peter"),
      )
    )

    val varScope = new VarScope
    varScope.put("lst", userListGenInstType, userList1)

    val src = s"lst.map( x : ${userType.name} => { nm: x.name, len: x.name.length } )"
    val ast = Parser.parse(src)
    println("\nast: "+"-"*20)
    ast.foreach( ASTDump.dump )

    val toaster = new Toaster(typeScope,varScope)
    val tast = toaster.compile(ast.get)
    println( describe(tast.supplierType) )

    val computed = tast.supplier.get()
    println(computed)

Результат

    generic instance: List[A=User]    generic instance: List[A=User]
    List[A=User]
    
    ast: --------------------
    CallAST
    -| PropertyAST map
    -|-| IdentifierAST IdentifierTok lst
    -| LambdaAST
    -|-| ParamAST IdentifierAST IdentifierTok x
    -|-|-| IdentifierAST IdentifierTok x
    -|-|-| TypeNameAST User
    -|-| PojoAST
    -|-|-| PojoItemAST nm
    -|-|-|-| PropertyAST name
    -|-|-|-|-| IdentifierAST IdentifierTok x
    -|-|-| PojoItemAST len
    -|-|-|-| PropertyAST length
    -|-|-|-|-| PropertyAST name
    -|-|-|-|-|-| IdentifierAST IdentifierTok x
    List[A=Pojo1]
    AList({nm=Vova, len=4},{nm=Yu, len=2},{nm=Peter, len=5})

    List[A=User]
    
    ast: --------------------
    CallAST
    -| PropertyAST map
    -|-| IdentifierAST IdentifierTok lst
    -| LambdaAST
    -|-| ParamAST IdentifierAST IdentifierTok x
    -|-|-| IdentifierAST IdentifierTok x
    -|-|-| TypeNameAST User
    -|-| PojoAST
    -|-|-| PojoItemAST nm
    -|-|-|-| PropertyAST name
    -|-|-|-|-| IdentifierAST IdentifierTok x
    -|-|-| PojoItemAST len
    -|-|-|-| PropertyAST length
    -|-|-|-|-| PropertyAST name
    -|-|-|-|-|-| IdentifierAST IdentifierTok x
    List[A=Pojo1]
    AList({nm=Vova, len=4},{nm=Yu, len=2},{nm=Peter, len=5})
