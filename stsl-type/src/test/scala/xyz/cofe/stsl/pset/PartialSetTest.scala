package xyz.cofe.stsl.pset

import org.junit.jupiter.api.Test

class PartialSetTest {
  case class Item( name:String, parent:Option[Item]=None ) {
    override def toString: String = name
    lazy val path:List[Item] = {
      var x = this
      var l = List(x)
      while( x.parent.isDefined ){
        x = x.parent.get
        l = x :: l
      }
      l
    }
  }
  val any = Item("any")
  val obj = Item("obj", Some(any))
  val string = Item("string", Some(obj))
  val number = Item("number", Some(obj))
  val int = Item("int", Some(number))
  val long = Item("long", Some(number))
  val void = Item("void")
  val none = Item("none", Some(void))
  val items = Set(any,obj,string,number,int,long,void,none)

  def relation(a:Item, b:Item):Boolean = {
    b.path.contains(a)
  }

  @Test
  def test01():Unit = {
    val pset = PartialSet[Item](
      items,
      (a,b)=>a.name.equals(b.name),
      relation
    )

    pset.descending(obj,int).foreach( path => {
      println( s"descending path" )
      if(path.isEmpty){
        throw new Error("bug!")
      }else{
        println( path.map(_.toString).reduce((a,b)=>a+" -> "+b) )
      }
    })

    pset.ascending(int,obj).foreach( path => {
      println( s"ascending path" )
      if(path.isEmpty){
        throw new Error("bug!")
      }else{
        println( path.map(_.toString).reduce((a,b)=>a+" -> "+b) )
      }
    })
  }
}
