package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.{ASTDump, Parser}
import xyz.cofe.stsl.tast.JvmType._
import xyz.cofe.stsl.types.{AnyVariant, CallableFn, Fn, Fun, GenericInstance, GenericParams, LocatorItem, LocatorItemFunParam, LocatorItemFunResult, LocatorItemGenericInstance, Params, TObject, Type, TypeDescriber, TypeVarLocator, TypeVariable}
import xyz.cofe.stsl.types.Type._
import xyz.cofe.stsl.types.TypeDescriber.describe

class TypeInferenceTest {
  //region User type

  class User(var name:String = "unnamed") {
    override def toString: String = {
      "User(name=\""+name+"\")"
    }
  }
  //noinspection TypeAnnotation
  val userType = TObject("User").build
  userType.fields ++= "name" -> STRING ->
    ((usr:Any)=>usr.asInstanceOf[User].name) ->
    ((usr:Any,value:Any)=>usr.asInstanceOf[User].name = value.asInstanceOf[String])
  userType.freeze
  //endregion
  //region Person type

  class Person(name:String="unnamedPerson", var age:Int = 10) extends User(name)
  //noinspection TypeAnnotation
  val personType = TObject("Person").build
  personType.extend(userType)
  personType.fields ++= "age" -> INT ->
    ((usr:Any)=>usr.asInstanceOf[Person].age) ->
    ((usr:Any,value:Any)=>usr.asInstanceOf[Person].age = value.asInstanceOf[Int])
  personType.freeze
  //endregion
  //region List type

  //region List Scala source

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
  //endregion
  //region List wrap impl

  //noinspection TypeAnnotation
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
  //endregion
  //endregion

  @Test
  def test01():Unit = {
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
  }

  @Test
  def test02():Unit = {
    println( "test02()" )
    println("="*40)

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
  }

  @Test
  def map01(): Unit ={
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

    //    typeVarLocators.foreach( tvl => {
    //      println(s"locator ${tvl} t.var ${tvl.typeVar}")
    //      println(s"  resolve ${tvl.resolve(usrListMapFn)}")
    //    })
  }

  @Test
  def map02(): Unit = {
    println("map02()")
    println("="*40)

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
  }
  
  def findGeneral( types:Seq[Type] ):Either[String,Type]= {
    def general_ab( a:Type, b:Type ):Either[String,Type] = {
      if( a==b ){
        Right(a)
      }else if( a.assignable(b) && b.assignable(a) ){
        Right(a)
      }else{
        ???
      }
    }
    
    def general( types:Seq[Type] ):Either[String,Type] = {
      ???
    }

    if( types.isEmpty ){
      Left("empty list")
    } else if( types.size==1 ){
      Right(types.head)
    } else {
      general(types)
    }
    
  }
  
  @Test
  def commonType_str_str():Unit = {
    println("commonType() - вывод общего типа для нескольких")
    println("="*40)
    
    val types = List( JvmType.STRING, JvmType.STRING )
  }
}
