package xyz.cofe.sel.types

object TypeDescriber {
  implicit class StringBuild( val sb : StringBuilder ) {
    def <<( str:String ):StringBuild = {
      sb.append(str)
      this
    }

    def endl:StringBuild = {
      sb.append("\n")
      this
    }
  }

  def describe(describedType: Type):String = {
    require(describedType!=null)
    describedType match {
      case fn:Fun =>describeFun(fn)
      case _ => describeObject(describedType)
    }
  }

  private def describeFun(describedFun: Fun):String = {
    val sb = new StringBuilder
    if( describedFun.generics.nonEmpty ){
      sb << "["
      sb << describedFun.generics.map( gp => gp.toString ).reduce( (a,b) => a+","+b )
      sb << "]"
    }

    sb << "("
    sb << describedFun.params.map( p => p.name+":"+
      (p.paramType match {
        case gh:GenericPlaceholder => gh.name
        //case _ => name(p.paramType)
        case _ => p.paramType.toString
      })
    ).reduce((a,b) => a+","+b)
    sb << ")"
    sb << ":"
    sb << (describedFun.returnType match {
      case gh:GenericPlaceholder => gh.name
      case _ => name(describedFun.returnType)
    })

    sb.toString()
  }

  private def describeObject(describedType: Type):String = {
    require(describedType!=null)
    val sb = new StringBuilder

    sb << name(describedType)
    if( describedType.extend.isDefined ){
      sb << " extend "
      sb << name(describedType.extend.get)
    }

    sb.endl;
    sb << "{" endl;

    var dtype = describedType
    var dtypeIdx = -1
    while( dtype!=null ){
      dtypeIdx += 1

      if( dtypeIdx>0 ){
        sb.endl;
        sb.append(s"  // inherit from $dtype") endl;
      }

      if( dtype.properties.nonEmpty ){
        sb << dtype.properties.map( prop => {
          s"  ${prop.name}:${name(prop.propertyType)}"
        }).reduce((a,b)=>a+"\n"+b)
        sb.endl;
      }

      if( dtype.methods.map.nonEmpty ){
        dtype.methods.map.foreach({case(name, funs)=>
          funs.foreach( fn=>{
            sb << s"  ${name}${fn}" endl
          })
        })
      }

      dtype = if( dtype.extend.isDefined ){
        dtype.extend.get
      }else{
        null
      }
    }

    sb << "}" endl;

    sb.toString()
  }

  def name(describedType: Type): String = {
    require(describedType!=null)
    describedType match {
      case gi:GenericInstance => nameGenInstance(gi)
      case _ => nameObj(describedType)
    }
  }

  private def nameGenInstance( ginst: GenericInstance ):String = {
    val sb = new StringBuilder
    sb.append(ginst.owner.name)
    if( ginst.owner.generics.nonEmpty ){
      sb.append("[")
      sb.append(ginst.owner.generics.map( gi=>{
        if( ginst.recepie.contains(gi.name) ){
          gi+">>"+ginst.recepie(gi.name).toString
        }else{
          gi.toString
        }
      }).reduce((a,b)=>a+","+b))
      sb.append("]")
    }
    sb.toString()
  }

  private def nameObj(describedType: Type): String = {
    require(describedType != null)

    val sb = new StringBuilder
    sb.append(s"${describedType.name}")
    if (describedType.generics != null && describedType.generics.nonEmpty) {
      val paramDesc = describedType.generics.zipWithIndex.map({ case (gp, pi) =>
        val str = s"${gp.name}${
          gp match {
            case inv:InVariant => ":"+inv.genericType
            case cov:CoVariant => ":"+cov.genericType
            case cnt:ContraVariant => ":"+cnt.genericType
            case _ => ""
          }
        }${
          gp match {
            case _: InVariant => ""
            case _: CoVariant => "+"
            case _: ContraVariant => "-"
            case _ => ""
          }
        }"
        if (pi > 0) "," + str else str
      })
      sb.append("[")
      sb.append(paramDesc.reduce((a, b) => a + b))
      sb.append("]")
    }
    sb.toString()
  }
}
