package xyz.cofe.stsl.types

object TypeDescriber {
  def describe(t:Type):String = {
    require(t!=null)
    t match {
      case pt:Type.Primitive => primitive(pt)
      case fn:Fun => fun(fn)
      case obj:Obj => descObj(obj)
    }
  }

  private def primitive(pt:Type.Primitive):String = {
    pt.name
  }

  private def fun(f:Fun):String = {
    f.toString
  }

  private def descObj(o:Obj):String = {
    val sb = new StringBuilder
    sb.append(o.name)
    sb.append(o.generics)
    if( o.extend.isDefined ){
      sb.append(" extends ").append(o.extend.get match {
        case n:Named => n.name
        case _ => "?"
      })
    }
    sb.append("\n")
    sb.append("{")

    var ti:Type = o
    var stop = false
    while(!stop) {
      ti match {
        case oi:Obj =>
          oi.fields.fields.foreach(fld => {
            sb.append("  ")
            sb.append(fld.toString).append("\n")
          })
          oi.methods.funs.foreach({ case (name, funs) =>
            funs.funs.foreach(fn => {
              sb.append("  ");
              sb.append(name).append(fn.toString).append("\n")
            })
          })
        case _ =>
      }

      if( ti.extend.isDefined  ){
        ti = ti.extend.get
        ti match {
          case oi:Obj =>
            if( oi.methods.funs.nonEmpty || oi.fields.fields.nonEmpty ){
              sb.append("  // extends ").append(oi.name).append(oi.generics).append("\n")
            }
          case n:Named =>
            sb.append("  // extends ").append(n.name).append(n.generics).append("\n")
          case _ =>
        }
      }else{
        stop=true
      }
    }

    sb.append("}")
    sb.toString()
  }
}