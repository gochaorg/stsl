package xyz.cofe.stsl.tok

import xyz.cofe.sparse.{CToken, CharPointer}

/**
 * Не типизированой численный литерал
 *
 * @param b1 начало в тексте
 * @param e1 конец в тексте
 * @param raw представление числа
 * @param base основание системы счисления
 */
class UnTypedNumberTok(b1: CharPointer, e1: CharPointer, val raw:String, val base:Int ) extends CToken(b1,e1) {
  lazy val count = raw.size
  def digit(i:Int):Int = {
    UnTypedNumberTok.digitOf( raw.charAt(i) )
  }

  lazy val toBigInt:BigInt = {
    (0 until count).map( digit ).reverse.zipWithIndex.map { case(dn,dp) =>
      dn * (BigInt(0) to BigInt(dp+1)).reduce( (a,b) => if(a==0) BigInt(1L) else a*base.toLong )
    }.sum
  }
  lazy val toLong:Long = {
    (0 until count).map( digit ).reverse.zipWithIndex.map { case(dn,dp) =>
      dn * (0L to dp+1).reduce( (a,b) => if(a==0) 1L else a*base.toLong )
    }.sum
  }
  lazy val toInt:Int = {
    (0 until count).map( digit ).reverse.zipWithIndex.map { case(dn,dp) =>
      dn * (0 to dp+1).reduce( (a,b) => if(a==0) 1 else a*base )
    }.sum
  }
  lazy val toFloatPart:Double = {
    (0 until count).map( digit ).zipWithIndex.map({
      case (dn, dp) => dn.toDouble / (0L to dp + 2).reduce((a, b) => if (a == 0) 1L else a * base.toLong)
    }).sum
  }
  lazy val toFloatPartDecimal:BigDecimal = {
    (0 until count).map( digit ).zipWithIndex.map({ case (dn, dp) =>
      val dnum = BigDecimal(dn.toDouble)
      val kbase = BigDecimal(base)
      val kof = kbase.pow(dp+1)
      dnum / kof
    }).sum
  }
}

object UnTypedNumberTok {
  def digitOf( char: Char ):Int = {
    char match {
      case '0' => 0
      case '1' => 1
      case '2' => 2
      case '3' => 3
      case '4' => 4
      case '5' => 5
      case '6' => 6
      case '7' => 7
      case '8' => 8
      case '9' => 9
      case 'a' => 10
      case 'A' => 10
      case 'b' => 11
      case 'B' => 11
      case 'c' => 12
      case 'C' => 12
      case 'd' => 13
      case 'D' => 13
      case 'e' => 14
      case 'E' => 14
      case 'f' => 15
      case 'F' => 15
      case 'g' => 16
      case 'G' => 16
      case 'h' => 17
      case 'H' => 17
      case 'i' => 18
      case 'I' => 18
      case 'j' => 19
      case 'J' => 19
      case 'k' => 20
      case 'K' => 20
      case 'l' => 21
      case 'L' => 21
      case 'm' => 22
      case 'M' => 22
      case 'n' => 23
      case 'N' => 23
      case 'o' => 24
      case 'O' => 24
      case 'p' => 25
      case 'P' => 25
      case 'q' => 26
      case 'Q' => 26
      case 'r' => 27
      case 'R' => 27
      case 's' => 28
      case 'S' => 28
      case 't' => 29
      case 'T' => 29
      case 'u' => 30
      case 'U' => 30
      case 'v' => 31
      case 'V' => 31
      case 'w' => 32
      case 'W' => 32
      case 'x' => 33
      case 'X' => 33
      case 'y' => 34
      case 'Y' => 34
      case 'z' => 35
      case 'Z' => 35
      case _ => -1
    }
  }
}