package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.ast.ASTDump
import xyz.cofe.sel.cmpl.rt.{Funs, Scope, TASTDump}
import xyz.cofe.sel.types.Fun.fn
import xyz.cofe.sel.types.{Fun, Methods, ObjectType, Param, Properties, Property, TypeDescriber}
import xyz.cofe.sel.types.Type._

class ToasterTest {
  @Test
  def test01(): Unit ={
    val ast = Parser.parse("2 * 3 + 4")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def decimal01(): Unit ={
    val ast = Parser.parse("2w * 3w + 4w")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def test02(): Unit ={
    val ast = Parser.parse("a * b + c")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    ws.vars.define("a",INT,10)
    ws.vars.define("b",INT,5)
    ws.vars.define("c",INT,3)

    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def test03(): Unit ={
    val ast = Parser.parse("a * b + c")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    ws.vars.define("a",SHORT,10.toShort)
    ws.vars.define("b",INT,5.toInt)
    ws.vars.define("c",LONG,3.toLong)

    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def cmp01(): Unit ={
    val ast = Parser.parse("a < b")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    ws.vars.define("a",INT,5)
    ws.vars.define("b",INT,10)

    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def ifOp1(): Unit ={
    val ast = Parser.parse("5 < 10 ? \"5<10=true\" : \"5<10=false\"")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  case class Person ( val name:String, val age:Int )

  lazy val personType: ObjectType = {
    new ObjectType("Person", None,
      Properties(
        Property("name", STRING, _.asInstanceOf[Person].name ),
        Property("age", INT, _.asInstanceOf[Person].age ),
      )
    )
  }

  @Test
  def prop01(): Unit ={
    val persons = List( Person("bob",10), Person("john",12) )

    val ast = Parser.parse("p.name")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    ws.types = ws.types + (personType.name -> personType)
    ws.vars.setOrDef( "p", personType, persons.head )

    println("first")
    val tast = Toaster(ws).compile(ast.get)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )

    println("next")
    ws.vars.setOrDef("p",personType,persons.drop(1).head)
    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def call01(): Unit ={
    val ast = Parser.parse("sum( 10, 12 )")
    ast.foreach( ASTDump.dump )

    val ws = Scope.default
    val sum = new Funs(List(
      Fun.fn("a",INT,"b",INT,INT,(a:Int,b:Int)=>a+b)
    ))
    ws.vars.setOrDef("sum",ARRAY,sum)

    val tast = Toaster(ws).compile(ast.get)
    TASTDump.dump(tast)

    println( s"type ${tast.supplierType}" )
    println( s"value ${tast.supplier.get()}" )
  }

  @Test
  def lambda01():Unit = {
    val ast = Parser.parse("a:int , b:int => a+b")
    ast.foreach( ASTDump.dump )

    val tast = Toaster(Scope.default).compile(ast.get)
    println( s"type ${tast.supplierType}" )

    val fun = tast.supplier.get()
    println( s"value ${fun}" )

    fun match {
      case fn: Fun =>
        val args = List(1, 2)
        println(s"calling fun with: $args")

        val res = fn.call(args)
        println(s"result: $res")
      case _ =>
    }
  }

  @Test
  def lambda02():Unit = {
    val ast = Parser.parse("a:int , r :: int => a < 0 ? 0 : r(a-1) + a")
    println("ast:")
    ast.foreach( ASTDump.dump )

    val tast = Toaster(Scope.default).compile(ast.get)
    println("tast:")
    TASTDump.dump(tast)

    println( s"type ${tast.supplierType}" )

    val fun = tast.supplier.get()
    println( s"value ${fun}" )

    fun match {
      case fn: Fun =>
        var args = List(-1)
        println(s"calling fun with: $args")
        var res = fn.call(args)
        println(s"result: $res")

        args = List(0)
        println(s"calling fun with: $args")
        res = fn.call(args)
        println(s"result: $res")

        args = List(1)
        println(s"calling fun with: $args")
        res = fn.call(args)
        println(s"result: $res")

        args = List(2)
        println(s"calling fun with: $args")
        res = fn.call(args)
        println(s"result: $res")

        args = List(3)
        println(s"calling fun with: $args")
        res = fn.call(args)
        println(s"result: $res")

      case _ => throw new Error("value not Fun")
    }
  }

  class User( val name:String, val locked:Boolean=false )
  val User_toString = fn( "user", THIS, STRING, (usr:User)=>usr.toString)
  val User_toString2 = fn(
    "user", THIS,
    "dummy", STRING,
    STRING,
    (usr:User, dummy:String)=>usr.name)
  val userType: ObjectType = {
    new ObjectType("User", None,
      Properties(
        Property("name", STRING, _.asInstanceOf[User].name ),
        Property("locked", BOOL, _.asInstanceOf[User].locked ),
      ),
      List(),
      Methods(
        "toString" -> User_toString,
        "getName" -> fn( "user", THIS, STRING, (usr:User)=>usr.name),
        "repeatName" -> fn(
          "user", THIS,
          "count", INT,
          STRING,
          (usr:User,cnt:Int)=>{
            val sb = new StringBuilder
            if( cnt>0 ){
              (0 until cnt).foreach( _ => sb.append(usr.name))
            }
            sb.toString()
          }),
      )
    )
  }

  @Test
  def methodCall01():Unit = {
    println( "methodCall01" )

    val scope = Scope.default
    scope.types = scope.types + (userType.name -> userType)

    val usr1 = new User("Igor")
    scope.vars.define("u",userType,usr1)

    val ast = Parser.parse("u.toString()")
    ast.foreach( ASTDump.dump )

    val tast = Toaster(scope).compile(ast.get)
    println( s"type ${tast.supplierType}" )

    val res = tast.supplier.get()
    println( s"value ${res}" )
  }

  @Test
  def methodCall02():Unit = {
    println( "methodCall02" )

    val scope = Scope.default
    scope.types = scope.types + (userType.name -> userType)

    val usr1 = new User("Igor")
    scope.vars.define("u",userType,usr1)

    val ast = Parser.parse("u.getName()")
    ast.foreach( ASTDump.dump )

    val tast = Toaster(scope).compile(ast.get)
    println( s"type ${tast.supplierType}" )

    val res = tast.supplier.get()
    println( s"value ${res}" )

    assert(res!=null)
    assert(res.isInstanceOf[String])
    assert("Igor"==res)
  }

  @Test
  def methodCall03():Unit = {
    println( "methodCall03" )

    val scope = Scope.default
    scope.types = scope.types + (userType.name -> userType)

    val usr1 = new User("Igor")
    scope.vars.define("u",userType,usr1)

    val ast = Parser.parse("u.repeatName(3)")
    ast.foreach( ASTDump.dump )

    val tast = Toaster(scope).compile(ast.get)
    println( s"type ${tast.supplierType}" )

    val res = tast.supplier.get()
    println( s"value ${res}" )

    assert(res!=null)
    assert(res.isInstanceOf[String])
    assert("IgorIgorIgor"==res)
  }

  val userListType: ObjectType = new ObjectType("UserList", None,
    Properties(
      Property("size", INT, ls => ls.asInstanceOf[List[User]].length)
    ), List(),
    Methods(
      "get" -> fn( "list", THIS, "idx", INT, userType, (ls:List[User],idx:Int) => ls.apply(idx)),
      "filter" -> fn(
        "list", THIS,
        "condition", new Fun((args=>{???}), List(Param("user",userType)), BOOL),
        THIS,
        (ls:List[User],fn:Fun)=>ls.filter( usr => {
          val condRes = fn.call(List(usr));
          condRes.asInstanceOf[Boolean]
        })
      )
    )
  )

  @Test
  def userList01():Unit = {
    println("userList01")
    val list = List[User](
      new User("Bob", false),
      new User("John", true),
      new User("Oscar", true),
      new User("Charlie", false),
      new User("William", false),
    )

    val scope = Scope.default
    scope.types = scope.types + (userType.name -> userType)
    scope.types = scope.types + (userListType.name -> userListType)

    println(TypeDescriber.describe(userType))
    println(TypeDescriber.describe(userListType))

    scope.vars.define("ls",userListType,list)

    def tryEval(code:String)(result: Any=>Any ) = {
      println( s"tryEval $code" )

      val ast = Parser.parse(code)
      assert(ast.isDefined)
      println("ast")
      ASTDump.dump(ast.get)

      val tast = Toaster(scope).compile(ast.get)
      println("tast")
      TASTDump.dump(tast)

      val getRes = tast.supplier.get()
      result(getRes)
    }

    tryEval("ls.get(1)") { getRes =>
      assert(getRes!=null)
      assert(getRes.isInstanceOf[User])

      println("fetched:")
      println(getRes.asInstanceOf[User].name)
      assert(getRes.asInstanceOf[User].name==list(1).name)
    }

    tryEval("ls.filter( u:User => u.locked )") { filterResult =>
      println( s"fetched: $filterResult" )
      assert( filterResult!=null )
      assert( filterResult.isInstanceOf[List[User]] )

      val fetchedList = filterResult.asInstanceOf[List[User]]
      assert(fetchedList.size==2)
    }
  }
}
