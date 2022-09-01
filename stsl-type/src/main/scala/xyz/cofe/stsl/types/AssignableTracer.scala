package xyz.cofe.stsl.types

/**
 * Трессировка операции [[Assignable]]
 */
trait AssignableTracer {
  def apply(message: String)(code: => Boolean): Boolean

  def apply[L, R <: Type](left: L, right: R)(code: => Boolean): Boolean

  def apply[L, R <: Type](message: String, left: L, right: R)(code: => Boolean): Boolean
}

object AssignableTracer {
  implicit val defaultTracer: AssignableTracer = new AssignableTracer {
    override def apply(message: String)(code: => Boolean): Boolean = code

    override def apply[L, R <: Type](left: L, right: R)(code: => Boolean): Boolean = code

    override def apply[L, R <: Type](message: String, left: L, right: R)(code: => Boolean): Boolean = code
  }

  def apply(out: Appendable): AssignableTracer = new AssignableTracer {
    var indent = 0

    override def apply(message: String)(code: => Boolean): Boolean = {
      out.append("  " * indent).append(message).append("{").append(System.lineSeparator())
      var res = false
      try {
        indent += 1
        res = code
        res
      } finally {
        indent -= 1
        out.append("  " * indent).append("} ").append(res.toString).append(System.lineSeparator())
      }
    }

    override def apply[L, R <: Type](left: L, right: R)(code: => Boolean): Boolean = {
      out.append("  " * indent).append(s"$left ?= $right {").append(System.lineSeparator())
      var res = false
      try {
        indent += 1
        res = code
        res
      } finally {
        indent -= 1
        out.append("  " * indent).append("} ").append(res.toString).append(System.lineSeparator())
      }
    }

    override def apply[L, R <: Type](message: String, left: L, right: R)(code: => Boolean): Boolean = {
      out.append("  " * indent).append(s"$message $left ?= $right {").append(System.lineSeparator())
      var res = false
      try {
        indent += 1
        res = code
        res
      } finally {
        indent -= 1
        out.append("  " * indent).append("} ").append(res.toString).append(System.lineSeparator())
      }
    }
  }
}
