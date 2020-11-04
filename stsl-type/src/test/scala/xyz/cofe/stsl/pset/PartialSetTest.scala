package xyz.cofe.stsl.pset

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.types.pset.PartialSet

class PartialSetTest {
  case class Item( name:String, parent:List[Item]=List() ) {
    override def toString: String = name
    lazy val paths:List[List[Item]] = {
      if( parent.isEmpty ){
        List(List(this))
      }else{
        parent.flatMap(prnt =>
          prnt.paths.map(path =>
            path ++ List(this)
          ))
      }
    }
  }
  def Item(name:String, parents:Item* ):Item = {
    Item(name, parents.toList)
  }

  val any = Item("any")
  val obj = Item("obj", any)
  val string = Item("string", obj)
  val number = Item("number", obj)
  val int = Item("int", number)
  val long = Item("long", number)
  val void = Item("void")
  val none = Item("none", void)
  val items = Set(any,obj,string,number,int,long,void,none)

  def relation(a:Item, b:Item):Boolean = {
    val p = b.paths.map(path=>path.contains(a))
    if( p.isEmpty ) {
      false
    } else {
      p.reduce((a,b)=>a || b)
    }
  }

  @Test
  def pathTest():Unit = {
    println(s"paths of string:")
    string.paths.foreach( path =>
      println("  "+path.map(n=>n.name).reduce((a,b)=>a+","+b))
    )
    assert(string.paths.size==1)
    assert(string.paths.head.size==3)
    assert(string.paths.head.map(p=>List(any.name, obj.name, string.name).contains(p.name)).reduce((a,b)=>a&&b))

    assert(relation(any,any))
    assert(relation(any,obj))
    assert(relation(any,string))

    assert(!relation(string,any))
    assert(!relation(obj,any))
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

  @Test
  def test02():Unit = {
    val a = Item("a")
    val b = Item("b",a)
    val c = Item("c",b)
    val d = Item("d",b)
    val f = Item("f",a)
    val e = Item("e",d,f)

    val i = Item("i")
    val j = Item("j",i)
    val k = Item("k",i)

    val set = Set(a,b,c,d,f,e,i,j,k)

    val pset = PartialSet[Item](
      set,
      (a,b)=>a.name.equals(b.name),
      relation
    )

    val print = (dir:String)=>{
      (path:List[Item])=>println( path.map(_.name).reduce((a,b)=>a+dir+b) )
    }

    pset.descending(a,e).foreach(print(" -> "))
    pset.ascending(e,a).foreach(print(" <- "))
  }
}
