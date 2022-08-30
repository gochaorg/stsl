package xyz.cofe.opt

object Sample {
  trait Log {
    def log[R](msg:String)(code: =>R):R
  }
  object Log {
    implicit val default = LogImpl.none
  }
  object LogImpl {
    implicit val none:Log = new Log {
      override def log[R](msg: String)(code: => R): R = {
        code
      }
    }
    implicit val simple:Log = new Log {
      override def log[R](msg: String)(code: => R): R = {
        println(msg)
        code
      }
    }
  }
  def sum(a:Int, b:Int)(implicit log:Log):Int = {
    log.log(s"sum of $a, $b")(a+b)
  }

  def main(args:Array[String]):Unit = {
    import LogImpl.simple
    println("hello"+sum(1,2))
  }
}
