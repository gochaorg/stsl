package xyz.cofe.stsl

object StringExt {
  implicit class Align(val string:String) {
    def pad(len:Int, padding:String=" ", left:Boolean=false, right:Boolean=true):String = {
      require(padding!=null)
      require(padding.length>0)

      //noinspection SimplifyBoolean
      require(!(left==false && right==false))

      if( len<=0 ){
        ""
      }else if( len==string.length ){
        string
      } else {
        if( len<string.length ){
          string.substring(0,len)
        }else{
          val sb = new StringBuilder

          val addsTotal = len - string.length
          val addLeft = if( left && !right ) addsTotal else
            if( !left && right ) 0 else addsTotal / 2
          val addRight = addsTotal - addLeft

          (0 until addLeft).foreach(ci=>{
            val c = padding.charAt(ci % padding.length)
            sb.append(c)
          })

          sb.append(string)

          (0 until addRight).foreach(ci=>{
            val c = padding.charAt(ci % padding.length)
            sb.append(c)
          })

          sb.toString()
        }
      }
    }
  }
}
