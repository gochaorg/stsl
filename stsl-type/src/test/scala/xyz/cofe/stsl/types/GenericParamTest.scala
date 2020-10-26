package xyz.cofe.stsl.types

import Type._
import org.junit.jupiter.api.Test

class GenericParamTest {
  @Test
  def test01():Unit = {
    val coObj = CoVariant("a",ANY)
    assert(coObj.assignable(ANY))
    assert(coObj.assignable(NUMBER))

    val coNum = CoVariant("b",NUMBER)
    assert(coNum.assignable(NUMBER), "NUMBER+ = NUMBER")
    assert(coNum.assignable(INT), "NUMBER+ = INT")
    assert(NUMBER.assignable(coNum), "NUMBER = NUMBER+")
  }

  @Test
  def test02():Unit = {
    val t2 = CoVariant("b",NUMBER)
    assert(ANY.assignable(t2), "OBJECT = NUMBER+")
  }

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

  @Test
  def test03():Unit = {
    val coObj = CoVariant("a",ANY)
    val coNum = CoVariant("a",NUMBER)
    val coInt = CoVariant("a",INT)

    val ctrObj = ContraVariant("a",ANY)
    val ctrNum = ContraVariant("a",NUMBER)
    val ctrInt = ContraVariant("a",INT)

    List(
      (coObj, coObj, true),
      (coObj, coNum, true),
      (coObj, coInt, true),

      (coNum, coObj, false),
      (coNum, coNum, true),
      (coNum, coInt, true),

      (coInt, coObj, false),
      (coInt, coNum, false),
      (coInt, coInt, true),

      (ctrObj, ctrObj, true),
      (ctrObj, ctrNum, false),
      (ctrObj, ctrInt, false),

      (ctrNum, ctrObj, true),
      (ctrNum, ctrNum, true),
      (ctrNum, ctrInt, false),

      (ctrInt, ctrObj, true),
      (ctrInt, ctrNum, true),
      (ctrInt, ctrInt, true),

      (ctrNum,coNum,false),
      (coNum,ctrNum,false),

    ).foreach({ case(t1, t2, expt) =>
      val asgn = t1.assignable(t2)

      println(
        s"${t1.toString.pad(10)} = ${t2.toString.pad(10)}" +
          s", expected=${expt.toString.pad(5)}, result=${asgn.toString.pad(5)}" +
          s", match=${asgn==expt}")
      assert(asgn==expt,s"expt=${expt}: ${t1}=${t2}, assign enable=${asgn}")
    })
  }
}
