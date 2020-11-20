package xyz.cofe.stsl.tast

import xyz.cofe.stsl.ast.{CallAST, PropertyAST}

object TASTDump {
  def dump(out: Appendable, tast: TAST, level:Int):Unit = {
    require(out!=null)
    require(tast!=null)

    if( level>0 )out.append("-|"*level).append(" ")
    val tastDesc = tast.ast match {
      case call: CallAST => call.callable match {
        case prop:PropertyAST => call.toString + " " + prop.name.tok.name + "()"
        case _ => call.toString
      }
      case _ => tast.ast.toString
    }
    out.append(tastDesc).append(" :: ").append(tast.supplierType.toString).append("\n")

    tast.children.foreach(a => {
      dump(out, a, level+1)
    })
  }

  def dump(out: Appendable, tast: TAST):Unit = {
    require(out!=null)
    require(tast!=null)
    dump(out, tast, 0)
  }

  def dump(tast: TAST):Unit = {
    require(tast!=null)
    dump(System.out, tast, 0)
  }
}