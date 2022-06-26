package xyz.cofe.stsl.tast

import org.junit.jupiter.api.Test
import xyz.cofe.stsl.ast.{ASTDump, Parser}
import xyz.cofe.stsl.ast.AstTest.{literal, pojo, pojoItem, test}
import xyz.cofe.stsl.types.{TObject, TypeDescriber}

class MapObjAsssignTest {
  implicit class FoldErr( errors:Seq[Option[String]] ){
    def foldErr:Option[String] = {
      errors.foldLeft( None:Option[String] )( (a,b) => (a,b) match {
        case (Some(x),Some(y)) => Some( x+"\n"+y )
        case (None,Some(y)) => Some( y )
        case (Some(x),None) => Some( x )
        case _ => a
      })
    }
  }
  
  def assignableByFields( ta:TObject, tb:TObject ):Option[String] = {
    val f_a_bOptList = ta.fields
      .map { fa => (fa, tb.fields.get(fa.name)) }
    
    val nonExistField = f_a_bOptList.map { case (fa, fb) => fb match {
      case Some(_) => None
      case None => Some(s"field ${fa.name} not exists")
    }}
    .filter { err => err.isDefined }
    .foldErr
    
    if( nonExistField.isDefined ){
      nonExistField
    }else{
      val f_a_bList = f_a_bOptList.map { case(fa,fb) => (fa,fb.get) }
      if( f_a_bList.isEmpty ){
        None
      }else{
        val nonAssignFields = f_a_bList
          .map { case(fa,fb) => (fa, fa.tip, fb, fb.tip) }
          .map { case(fa,ta,fb,tb) => (fa,fb, ta.assignable(tb)) }
          .map { case(fa,fb,asgn) => if (asgn) {
            None
          } else {
            Some(s"${fa.name}:${fa.tip} not assignable from ${fb.name}:${fb.tip}")
          }}.foldErr
        
        if( nonAssignFields.isDefined ) {
          nonAssignFields
        } else {
          None
        }
      }
    }
  }
  
  def assignableByMethods( ta:TObject, tb:TObject ):Option[String] = {
    if( ta.methods.isEmpty ){
      None
    }else{
      Some("not impl assignableByMethods")
    }
  }
  def assignableByStruct( ta:TObject, tb:TObject ):Option[String] = {
    List(
      assignableByFields(ta, tb),
      assignableByMethods(ta, tb)
    ).foldErr
  }
  
  @Test
  def test01():Unit = {
    println("test01")
    println("="*30)
  
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
  
    val toast = new Toaster(ts)
    
    val tast1 = toast.compile(Parser.parse("{ k1: 1, k2: \"abc\"}").get)
    val tast2 = toast.compile(Parser.parse("{ k1: 2, k2: \"abcdef\"}").get)
  
    val tip1 = tast1.supplierType
    val tip2 = tast2.supplierType
    
    println(tip1.getClass)
    println(TypeDescriber.describe(tip1))
    
    println(tip2.getClass)
    println(TypeDescriber.describe(tip2))
    
    println( tip1.assignable(tip2) )
    val asgnBySt = assignableByStruct(tip1.asInstanceOf[TObject], tip2.asInstanceOf[TObject])
    println( asgnBySt )
    assert( asgnBySt.isEmpty )
  }
  
  @Test
  def test02():Unit = {
    println("test02")
    println("="*30)
  
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
  
    val toast = new Toaster(ts)
    
    val tast1 = toast.compile(Parser.parse("{ k1: 1, k2: 3}").get)
    val tast2 = toast.compile(Parser.parse("{ k1: 2, k2: \"abcdef\"}").get)
  
    val tip1 = tast1.supplierType
    val tip2 = tast2.supplierType
    
    println(tip1.getClass)
    println(TypeDescriber.describe(tip1))
    
    println(tip2.getClass)
    println(TypeDescriber.describe(tip2))
    
    println( tip1.assignable(tip2) )
    val asgnBySt = assignableByStruct(tip1.asInstanceOf[TObject], tip2.asInstanceOf[TObject])
    println( asgnBySt )
    assert( asgnBySt.isDefined )
  }
  
  @Test
  def test03():Unit = {
    println("test03")
    println("="*30)
  
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
  
    val toast = new Toaster(ts)
    
    val tast1 = toast.compile(Parser.parse("{ k1: 1}").get)
    val tast2 = toast.compile(Parser.parse("{ k1: 2, k2: \"abcdef\"}").get)
  
    val tip1 = tast1.supplierType
    val tip2 = tast2.supplierType
    
    println(tip1.getClass)
    println(TypeDescriber.describe(tip1))
    
    println(tip2.getClass)
    println(TypeDescriber.describe(tip2))
    
    println( tip1.assignable(tip2) )
    val asgnBySt = assignableByStruct(tip1.asInstanceOf[TObject], tip2.asInstanceOf[TObject])
    println( asgnBySt )
    assert( asgnBySt.isEmpty )
  }
  
  @Test
  def test04():Unit = {
    println("test04")
    println("="*30)
    
    val ts = new TypeScope()
    ts.implicits = JvmType.implicitConversion
    
    val toast = new Toaster(ts)
    
    val tast1 = toast.compile(Parser.parse("{ k1: 1, k2: \"abcdef\"}").get)
    val tast2 = toast.compile(Parser.parse("{ k1: 2}").get)
    
    val tip1 = tast1.supplierType
    val tip2 = tast2.supplierType
    
    println(tip1.getClass)
    println(TypeDescriber.describe(tip1))
    
    println(tip2.getClass)
    println(TypeDescriber.describe(tip2))
    
    println( tip1.assignable(tip2) )
    val asgnBySt = assignableByStruct(tip1.asInstanceOf[TObject], tip2.asInstanceOf[TObject])
    println( asgnBySt )
    assert( asgnBySt.isDefined )
  }
}