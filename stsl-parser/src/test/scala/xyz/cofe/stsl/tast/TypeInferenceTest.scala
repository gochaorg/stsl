package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.Parser
import xyz.cofe.stsl.tast.JvmType._
import xyz.cofe.stsl.types.{CallableFn, Fn, Fun, Params, TObject, TypeVariable}
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
}
