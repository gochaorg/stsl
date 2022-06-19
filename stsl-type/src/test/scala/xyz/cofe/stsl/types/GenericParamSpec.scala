package xyz.cofe.stsl.types

import Type._
import JvmType._
import org.junit.jupiter.api.Test
import org.scalatest.flatspec.AnyFlatSpec

class GenericParamSpec extends AnyFlatSpec {
  "CoVariant проверки" should "ANY+ = ANY # успешно" in assert(CoVariant("a",ANY).assignable(ANY))
  it should "ANY+ = NUMBER # успешно" in assert(CoVariant("a",ANY).assignable(NUMBER))
  it should "NUMBER+ = NUMBER # успешно" in assert(CoVariant("b",NUMBER).assignable(NUMBER))
  it should "NUMBER+ = INT # успешно" in assert(CoVariant("b",NUMBER).assignable(INT))
  it should "NUMBER = NUMBER+ # успешно" in assert(NUMBER.assignable(CoVariant("b",NUMBER)))
  it should "ANY = NUMBER+" in assert(ANY.assignable(CoVariant("b",NUMBER)))
  
  implicit class StrExt( val str:String ) {
    def pad(len:Int):String = {
      if(len<=0){
        ""
      }else{
        if( len<str.length ){
          str.substring(0,len)
        }else{
          if( len==str.length ){
            str
          }else{
            val sb = new StringBuilder
            sb.append(str)
            (0 until (len-str.length)).foreach( _ =>
              sb.append(" ")
            )
            sb.toString()
          }
        }
      }
    }
  }
  
  val coObj = CoVariant("a",ANY)
  val coNum = CoVariant("a",NUMBER)
  val coInt = CoVariant("a",INT)
  
  val ctrObj = ContraVariant("a",ANY)
  val ctrNum = ContraVariant("a",NUMBER)
  val ctrInt = ContraVariant("a",INT)
  
  "assignable для CoVariant, ContraVariant" should "ok: ANY+ = ANY+" in
    assert(coObj.assignable(coObj))
  it should "  ok: ANY+ = NUM+" in assert(coObj.assignable(coNum))
  it should "  ok: ANY+ = INT+" in assert(coObj.assignable(coInt))
  
  it should "fail: NUM+ = ANY+" in assert(!coNum.assignable(coObj))
  it should "  ok: NUM+ = NUM+" in assert( coNum.assignable(coNum))
  it should "  ok: NUM+ = INT+" in assert( coNum.assignable(coInt))
  
  it should "fail: INT+ = ANY+" in assert(!coInt.assignable(coObj))
  it should "fail: INT+ = NUM+" in assert(!coInt.assignable(coNum))
  it should "  ok: INT+ = INT+" in assert( coInt.assignable(coInt))
  
  it should "  ok: ANY- = ANY-" in assert( ctrObj.assignable(ctrObj))
  it should "fail: ANY- = NUM-" in assert(!ctrObj.assignable(ctrNum))
  it should "fail: ANY- = INT-" in assert(!ctrObj.assignable(ctrInt))
  
  it should "  ok: NUM- = ANY-" in assert( ctrNum.assignable(ctrObj))
  it should "  ok: NUM- = NUM-" in assert( ctrNum.assignable(ctrNum))
  it should "fail: NUM- = INT-" in assert(!ctrNum.assignable(ctrInt))
  
  it should "  ok: INT- = ANY-" in assert( ctrInt.assignable(ctrObj))
  it should "  ok: INT- = NUM-" in assert( ctrInt.assignable(ctrNum))
  it should "  ok: INT- = INT-" in assert( ctrInt.assignable(ctrInt))

  it should "fail: NUM- = NUM+" in assert(!ctrNum.assignable(coNum))
  it should "fail: NUM+ = NUM-" in assert(!coNum.assignable(ctrNum))
  
  it should "fail: NUM- = ANY"  in assert(!ctrNum.assignable(ANY), s"${ctrNum} = $ANY ==> must false")
  it should "  ok: NUM- = NUM"  in assert( ctrNum.assignable(NUMBER), s"${ctrNum} = $NUMBER ==> must true")
  it should "  ok: NUM- = INT"  in assert( ctrNum.assignable(INT), s"${ctrNum} = $INT ==> must true")
}
