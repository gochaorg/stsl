package xyz.cofe.stsl.types

import org.scalatest.flatspec.AnyFlatSpec
import xyz.cofe.stsl.types.JvmType.NUMBER
import xyz.cofe.stsl.types.pset.PartialSet

class IntersectionSpec extends AnyFlatSpec {
  /**
   * Поиск общего типа среди заданных
   * @param at первый тип
   * @param bt второй тип
   * @return общий тип
   */
  def commonType(at:Type, bt:Type):Option[Type] = {
    (at.assignable(bt), bt.assignable(at)) match {
      case (true, true) => Some(at)
      case (true, false) => Some(at)
      case (false, true) => Some(bt)
      case _ => None
    }
  }
  
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
  
  case class FieldCollector( fields: Map[String,List[(Any,Field)]] )
  object FieldCollector {
    def collect[FIELDS <: InheritedFields](fieldOwners: Seq[(Any,FIELDS)] ): FieldCollector = {
      fieldOwners.foldLeft(FieldCollector(Map()))( (collector0, fieldOwner) => {
        fieldOwner._2.publicFields.foldLeft(collector0)( (collector,field) => {
          collector.copy(
            fields = collector.fields + (
              field.name ->
                ((fieldOwner._1,field) :: collector.fields.getOrElse(field.name,List()))
              )
          )
        })
      })
    }
  }
  
  val anon0 = TAnon(
    Fields(
      //"fld_a" -> NUMBER
      List(
        new WriteableField(
          name="fld_a",
          tip=NUMBER,
          reading=(inst:Any)=>{
            println("read anon0.fld_a")
            inst.asInstanceOf[java.util.Map[Any,Any]].get("fld_a")
          },
          writing=(inst,newVal)=>inst
        )
      )
    )
  )
  
  val anon1 = TAnon(
    Fields(
//      "fld_a" -> NUMBER,
//      "fld_b" -> NUMBER
      List(
        new WriteableField(
          name="fld_a",
          tip=NUMBER,
          reading=(inst:Any)=>{
            println("read anon1.fld_a")
            inst.asInstanceOf[java.util.Map[Any,Any]].get("fld_a")
          },
          writing=(inst,newVal)=>inst
        ),
        new WriteableField(
          name="fld_b",
          tip=NUMBER,
          reading=(inst:Any)=>{
            println("read anon1.fld_b")
            inst.asInstanceOf[java.util.Map[Any,Any]].get("fld_b")
          },
          writing=(inst,newVal)=>inst
        )
      )
    )
  )
  
  "anon0, anon1" should "2 fields (fld_a - 2 inst, fld_b - 1 inst)" in {
    val fcollector = FieldCollector.collect(List((null,anon0), (null,anon1)))
    assert(fcollector.fields.size==2)
    assert(fcollector.fields.contains("fld_a"))
    assert(fcollector.fields.contains("fld_b"))
    assert(fcollector.fields("fld_a").size==2)
    assert(fcollector.fields("fld_b").size==1)
  }
  
  trait UnionFieldContext {
    def readInstanceNotMatched( inst:Any ):Any
    def readFieldCantCast2Writeable( inst:Any, field:Field ):Any
    def writeInstanceNotMatched( inst:Any, newValue:Any ):Any
    def writeFieldCantCast2Writeable( inst:Any, newValue:Any, field:Field ):Any
  }
  
  def unionField( name:String, tip:Type, fields:List[(Any,Field)] )(implicit ctx:UnionFieldContext):WriteableField = {
    val instances: Map[Any, Field] = fields.toMap
    new WriteableField(
      name = name,
      tip = tip,
      reading = (inst:Any) => instances.get(inst) match {
        case Some(fld) => fld match {
          case wf:WriteableField => wf.reading(inst)
          case _ => ctx.readFieldCantCast2Writeable(inst,fld)
        }
        case None => ctx.readInstanceNotMatched(inst)
      },
      writing = (inst:Any, newValue:Any) => instances.get(inst) match {
        case Some(fld) => fld match {
          case wf: WriteableField => wf.writing(inst, newValue)
          case _ => ctx.writeFieldCantCast2Writeable(inst,newValue,fld)
        }
        case None =>
          ctx.writeInstanceNotMatched(inst, newValue)
      }
    )
  }
}
