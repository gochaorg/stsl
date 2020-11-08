package xyz.cofe.sel

import org.junit.jupiter.api.Test
import xyz.cofe.sel.cmpl.rt.Scope
import xyz.cofe.sel.types.Fun._
import xyz.cofe.sel.types.Type._
import xyz.cofe.sel.types._

class ExtMethodsTest {
  class User( val name:String )
  class Person ( name:String, val age:Int ) extends User(name)

  lazy val userType: ObjectType = {
    new ObjectType("User", None, Properties(
      Property("name", STRING, _.asInstanceOf[User].name )
    ))
  }

  object Person {
    def apply(name: String, age: Int): Person = new Person(name, age)
  }

  lazy val personType: ObjectType = {
    new ObjectType("Person", Some(userType),
      Properties(
        Property("age", INT, _.asInstanceOf[Person].age )
      )
    )
  }

  @Test
  def readPerson():Unit = {
    val persons = List( Person("bob",10), Person("john",12) )
    persons.foreach( p => {
      println("person")
      println("  name="+personType.properties("name").read(p))
      println("  age="+personType.properties("age").read(p))
    })

    println("-"*40)
    println( TypeDescriber.describe(personType) )
  }

  //noinspection TypeAnnotation
  lazy val User_toString = fn( "user", userType, STRING, (usr:User)=>usr.toString)

  //noinspection TypeAnnotation
  lazy val Person_toString = fn( "person", personType, STRING, (usr:Person)=>usr.toString)

  //noinspection TypeAnnotation
  lazy val Person_getName = fn( "person", personType, STRING, (usr:Person)=>usr.name)

  @Test
  def methodTest():Unit = {
    val scope = Scope.default
    scope.types = scope.types + (userType.name -> userType)
    scope.types = scope.types + (personType.name -> personType)
    scope.vars.define("toString",User_toString,User_toString)
    scope.vars.define("getName",Person_getName,Person_getName)

    List(userType, personType).foreach( utype => {
      println(s"methods of ${utype.name}")
      scope.funs.methodsOf(utype).foreach({ case (fname, funs) =>
        funs.foreach(fun => {
          println(s"${fname} ${fun}")
        })
      })
    })
  }
}
