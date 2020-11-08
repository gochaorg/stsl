package xyz.cofe.sel.types

class GenericInstance(
                       val name:String,
                       val owner:Type,
                       val recepie:Map[String,Type]
                     ) extends Type with TypeReplace with BakeGenerics
{
  require(owner!=null)
  require(name!=null)

  /**
   * Расширяет тип
   */
  override val extend: Option[Type] = None

  override def bakeGenerics(recipe: Map[String, Type]): GenericInstance = {
    require(recipe!=null)
    val rcpt : Map[String,Type] = this.recepie.map({case (k,t)=>
      //TODO here check assignable
      t match {
        case bk: BakeGenerics => k -> bk.bakeGenerics(recipe)
        case gh: GenericPlaceholder => if( recipe.contains(gh.name) ) k -> recipe(gh.name) else k -> gh
        case gi: GenericInstance => if( recipe.contains(gi.name) ) k -> recipe(gi.name) else k -> gi
        case _ => k -> recipe.getOrElse(k,t)
      }
    })
    new GenericInstance(name,owner,rcpt)
  }

  override def typeReplace(replacement: Type => Option[Type]): Type = {
    require(replacement!=null)
    val newOwner = owner match {
      case gp:GenericPlaceholder =>
        val r = Some(GenericPlaceholder(gp.name, replacement(gp.owner).getOrElse(gp.owner)))
        r
      case tr:TypeReplace => {
        val r = Some(tr.typeReplace(replacement))
        r
      }
      case _ => replacement(owner)
    }
    val newRecipie:Map[String,Type] = recepie.map({case(k,t)=>
      val newT : Option[Type] = t match {
        case tr:TypeReplace => Some(tr.typeReplace(replacement))
        case _ => replacement(t)
      }
      k -> newT.getOrElse(t)
    })

    //TODO Не должно быть nnOwner
    val nnOwner = newOwner match {
      case Some(Type.THIS) => Some(owner)
      case _ => newOwner
    }
    new GenericInstance(
      owner.name+GenericInstance.nextId,

      //TODO Это правильный
      //newOwner.getOrElse(owner),

      //TODO Не должно быть nnOwner.getOrElse
      nnOwner.getOrElse(owner),
      newRecipie
    )
  }

  override def toString: String = {
    val sb = new StringBuilder
    sb.append(owner.name)
    if(owner.generics.nonEmpty){
      sb.append("[")
      sb.append(
        owner.generics.map( g =>
          if( recepie.contains(g.name) ){
            s"${g}>>${recepie(g.name)}"
          } else {
            s"${g}"
          }
        ).reduce((a,b)=>a+","+b)
      )
      sb.append("]")
    }
    sb.toString
  }
}

object GenericInstance {
  private var idSeq : Long = 0
  def nextId : Long = { idSeq += 1; idSeq }
  def apply( ownerType:Type, recepie:(String,Type)* ):GenericInstance = {
    require(ownerType!=null)
    val name = ownerType.name+nextId
    new GenericInstance(name, ownerType, recepie.toMap)
  }
}
