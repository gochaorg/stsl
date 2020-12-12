package xyz.cofe.stsl.types

class GenericInstance[A <: Type with TypeVarReplace[A]]( val recipe:Map[String,Type], val source:A ) extends Type with TypeVarReplace[GenericInstance[A]] {
  require(recipe!=null)
  require(source!=null)
  recipe.foreach({case(name,trgt)=>
    if( source.generics.get(name).isEmpty ){
      throw TypeError(s"type variable ${name} not defined in ${source}")
    }
  })

  override def toString: String = {
    val sb = new StringBuilder()
    if( recipe.isEmpty ){
      sb.append(source)
    }else{
      sb.append(source match {
        case n:Named => n.name
        case _ => source.toString
      })
      sb.append("[")
      sb.append(source.generics.map(gp=>{
        if(recipe.contains(gp.name)){
          gp.toString + "=" + recipe(gp.name)
        }else{
          gp.toString
        }
      }).reduce((a,b)=>a+","+b))
      sb.append("]")
    }
    sb.toString()
  }

  override def typeVarReplace(trecipe: TypeVariable => Option[Type]): GenericInstance[A] = {
    require(trecipe!=null)
    new GenericInstance(
      recipe.map({case(key,src)=>
        key -> (src match {
          case tv:TypeVariable =>
            trecipe(tv).getOrElse(src match {
              case tvr:TypeVarReplace[_] => tvr.typeVarReplace(trecipe).asInstanceOf[Type]
              case _ => src
            })
          case _ => src match {
            case tvr:TypeVarReplace[_] => tvr.typeVarReplace(trecipe).asInstanceOf[Type]
            case _ => src
          }
        })
      }),
      source
    )
  }
}
