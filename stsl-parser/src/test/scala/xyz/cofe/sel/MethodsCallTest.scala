package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.types.Fun.fn
import xyz.cofe.sel.types.{BasicType, Methods, ObjectType, Properties, Property, TypeDescriber}
import xyz.cofe.sel.types.Type.{INT, STRING, THIS}

class MethodsCallTest {
  class User( val name:String )
  class Person ( name:String, val age:Int ) extends User(name)

  val User_toString = fn( "user", THIS, STRING, (usr:User)=>usr.toString)
  val User_shortName = fn( "user", THIS, STRING, (usr:User)=>usr.toString)

  val userType: ObjectType = {
    new ObjectType("User", None,
      Properties(
        Property("name", STRING, _.asInstanceOf[User].name )
      ),
      List(),
      new Methods().
        add("toString", User_toString).
        add("shortName",User_shortName)
    )
  }

  val Person_toString = fn( "person", THIS, STRING, (usr:Person)=>usr.toString);
  val Person_getName = fn( "person", THIS, STRING, (usr:Person)=>usr.name);

  val personType = new ObjectType("Person", Some(userType),
    Properties(
      Property("age", INT, _.asInstanceOf[Person].age )
    ),
    List(),
    new Methods().
      add("toString", Person_toString).
      add("getName", Person_getName)
  )


  @Test
  def test01(): Unit = {
    println( TypeDescriber.describe(userType) )
    println( TypeDescriber.describe(personType) )

    val persons = List(new Person("a",10), new Person("b",20))
    persons.foreach( p => {
      personType.methods.map("toString").foreach( f => {
        println(s"call toString for ${p.name}")
        println(f.call(List(p)))
      })
    })
  }
}
