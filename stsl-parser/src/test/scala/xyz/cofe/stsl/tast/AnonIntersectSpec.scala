package xyz.cofe.stsl.tast

import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.stsl.types.{AnyVariant, Field, Fields, Fn, Funs, GenericInstance, GenericParams, InheritedFields, Methods, Named, Params, TAnon, TObject, Type, WriteableField}
import xyz.cofe.stsl.tast.JvmType.{INT, NUMBER, STRING}
import xyz.cofe.stsl.tast.isect._

/**
 * пересечение несколько TAnon типов
 */
class AnonIntersectSpec extends AnyFlatSpec {
  
  "commonType between int, int" should "Some(int)" in {
    val ct = commonType(JvmType.INT,JvmType.INT)
    assert(ct.isDefined)
    assert(ct.get == JvmType.INT)
  }
  
  "commonType between int, number" should "Some(number)" in {
    val ct = commonType(JvmType.INT,JvmType.NUMBER)
    assert(ct.isDefined)
    assert(ct.get == JvmType.NUMBER)
  }
  
  "commonType between number, int" should "Some(number)" in {
    val ct = commonType(JvmType.NUMBER,JvmType.INT)
    assert(ct.isDefined)
    assert(ct.get == JvmType.NUMBER)
  }
  
  "commonType between double, int" should "None" in {
    val ct = commonType(JvmType.DOUBLE,JvmType.INT)
    assert(ct.isEmpty)
  }
  
  // val anon0 = TAnon( Fields("fld"->NUMBER) )
  // val anon1 = TAnon( Fields(), new Methods(Map("some" -> Funs(Fn(Params("a"->NUMBER),NUMBER)))) )
  
  "merge [{a:NUMBER,c:INT},{a:NUMBER,b:NUMBER,c:STRING}]" should "{a:NUMBER,b:Opt[NUMBER]}" in {
    val optType = TObject("Opt").extend(Type.ANY).build
    optType.generics.append.any("A")
    optType.freeze
    
    val a0 = TAnon( Fields("a"->NUMBER, "c"->INT) )
    val a1 = TAnon( Fields("a"->NUMBER, "b"->NUMBER, "c"->STRING) )
    
    val fc = FieldsCollector(List(a0,a1))
    val fr = FieldsReductor(OptionalField(optType,"A"),OptGenInstance())
    val fields = fr.reduce(fc)
    fields.foreach( fld => {
      println(s"${fld.name} : ${fld.tip}")
    })
    
    val fieldNames = fields.map(_.name).toSeq
    assert(fieldNames.length==2)
    assert(fieldNames.contains("a"))
    assert(fieldNames.contains("b"))
    
    val field_a = fields.find(_.name=="a").get
    val field_b = fields.find(_.name=="b").get
    assert(field_a.tip == NUMBER)
    assert(field_b.tip.isInstanceOf[GenericInstance[_]])
    
    val gi = field_b.tip.asInstanceOf[GenericInstance[_]]
    println(gi.source)
    println(gi.source==optType)
    
    println(gi.recipe.contains("A"))
    println(gi.recipe.get("A"))
    
    assert(gi.source==optType)
    assert(gi.recipe("A")==NUMBER)
  }
}
