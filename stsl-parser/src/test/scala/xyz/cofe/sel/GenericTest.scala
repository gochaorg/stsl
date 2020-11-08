package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.types.{AnyVariant, CoVariant, Fun, GenericInstance, GenericPlaceholder, InVariant, Methods, ObjectType, Param, Properties, Property, Type, TypeDescriber}
import xyz.cofe.sel.types.Type.{BOOL, FN, INT, OBJECT, STRING, THIS, VOID}
import xyz.cofe.sel.types.Fun.fn

class GenericTest {
  class User( val name:String, val locked:Boolean=false )
  val userType = new ObjectType(
    name="User",
    extend=Some(OBJECT),
    props = Properties(
      Property("name", STRING, _.asInstanceOf[User].name),
      Property("locked", BOOL, _.asInstanceOf[User].locked),
    )
  )

  val genericList = new ObjectType(
    name = "List",
    extend = Some(OBJECT),
    genericParams = List(CoVariant("A",OBJECT)),
    props = Properties(
      Property("size", INT, _.asInstanceOf[List[_]].length)
    ),
    objectMethods = Methods(
      "get" -> fn(
        "list", THIS,
        "index", INT,
        new GenericPlaceholder("A", THIS),
        (list:Any, idx:Any) => {
          list.asInstanceOf[List[_]](idx.asInstanceOf[Int])
        }
      ),
      "add" -> fn(
        "list", THIS,
        "item", new GenericPlaceholder("A", THIS),
        THIS,
        (list:Any, el:Any) => {
          val lst : List[_] = el :: list.asInstanceOf[List[_]]
          lst
        }
      ),
      "each" -> fn(
        "list", THIS,
        "accept", new Fun(args => ???,
          List(Param("item",new GenericPlaceholder("A", THIS))),
          VOID
        ),
        THIS,
        (list:Any, accept:Any)=>{
          if( accept!=null && accept.isInstanceOf[Fun] ){
            val fn = accept.asInstanceOf[Fun]
            if( list!=null && list.isInstanceOf[List[_]] ){
              list.asInstanceOf[List[_]].foreach( itm => fn.call(List(itm)) )
            }else{
              throw new RuntimeException("list not List ("+list+")")
            }
          }else{
            throw new RuntimeException("accept not Fun ("+accept+")")
          }
          list
        }
      ),
//      "map" -> fn(
//        "list", THIS,
//        "mapping", new Fun(args => ???,
//          List(Param("item",new GenericPlaceholder("A", THIS))),
//          new GenericPlaceholder("R", FN)
//        ),
//        new GenericPlaceholder("R", FN),
//        (list:Any, fnMapper:Any)=>{
//          ???
//        }
//      )
      "map" -> new Fun(
        fn = args => ???,
        genericsParams = List(
          CoVariant("T",OBJECT)
        ),
        parameters = List(
          Param("list", THIS),
          Param("mapper",
          new Fun(
            fn = args => ???,
            parameters = List(Param("a",GenericPlaceholder("A", THIS))),
            retType = GenericPlaceholder("T",FN)
          ))
        ),
        retType = GenericInstance(THIS, "A" -> GenericPlaceholder("T", FN))
      )
    )
  )

  @Test
  def desc01():Unit = {
    //println( TypeDescriber.describe(genericList) )

    //val userList = genericList.bakeGenerics(Map("A" -> userType))
    //println( TypeDescriber.describe(userList) )

    val mapfn = genericList.methods.map("map").head
    println( "source map fn:" )
    println( TypeDescriber.describe(mapfn) )

    mapfn.returnType match {
      case gi:GenericInstance => {
        println( "return GenericInstance" )
        if( gi.recepie.contains("A") ){
          val giR = gi.recepie("A")
          println( "  contains A -> "+giR )
          giR match {
            case gh:GenericPlaceholder =>
              println( s"    GenericPlaceholder, owner=${gh.owner} o.hc=${gh.owner.hashCode()} f=${mapfn.hashCode()} ${gh.owner == mapfn}" )
          }
        }
      }
    }

    val bmapfn = mapfn.bakeGenerics(Map(
      "T"->userType
    ))
//    println( "backed map fn:" )
//    println( TypeDescriber.describe(bmapfn) )
//
//    val retType = bmapfn.returnType
//    println( "return type" )
//    println( TypeDescriber.describe(retType) )
//
//    val giRetType = retType.asInstanceOf[GenericInstance]
//    println( "bake return")
//    val backed = giRetType.owner.asInstanceOf[ObjectType].bakeGenerics(Map("A"->userType))
//    println( TypeDescriber.describe(backed) )
  }
}
