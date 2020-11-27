package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.tast.JvmType._
import xyz.cofe.stsl.types.TObject

class TypeInferenceTest {
  //region User type

  class User(var name:String = "unnamed")
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

  @Test
  def test01():Unit = {
    println("test01()")
    println("="*40)
  }
}
