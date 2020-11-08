package xyz.cofe.sel.types

import xyz.cofe.sel.cmpl.rt.Funs

/**
 * Методы
 * @param map карта методов
 * @param owner владелец методов
 */
class Methods(val map: Map[String,Funs]=Map(), val owner:Option[Type]=None ) {
  require(owner!=null)
  require(map!=null)
  require(
    if( map.isEmpty ){
      true
    }else {
      map.map({ case (name, flist) => name != null && name.length > 0 }).reduce((a, b) => a && b)
    }
  )
  if( map.nonEmpty ){
    map.foreach({ case (name, flist) =>
      flist.foreach(fn =>
        if( fn.params.nonEmpty && owner.nonEmpty ){
          require(
            fn.params.head.paramType.assignable(owner.get),
            s"parameter ${fn.params.head.name}:${fn.params.head.paramType} must assignable from ${owner.get}"
          )
        }
        //fn.params.nonEmpty && (if (owner.isEmpty) true else fn.params.head.paramType.assignable(owner.get))
      )
    })
  }

  /**
   * Клонирует и добавляет в коллекцию методы
   * @param name имя добавляемого метода
   * @param fns метод(ы)
   * @return новая коллекция методов
   */
  def add( name:String, fns:Fun* ):Methods = {
    require(name!=null)
    require(name.length>0)
    fns.zipWithIndex.foreach({case(fn,fnIdx)=>
      require(fn != null, s"fn[$fnIdx] null ref")
      require(fn.params.nonEmpty)
      require(if (owner.isEmpty) true else fn.params.head.paramType.assignable(owner.get))
    })
    var nmap : Map[String,Funs] = map
    if( nmap.contains(name) ){
      var funs = nmap(name)
      funs = if( funs==null ) new Funs(List()) else funs
      fns.foreach( f=> funs = funs.add(f) )
      nmap = nmap + (name -> funs)
    }else{
      nmap = nmap + (name -> new Funs(fns.toList))
    }
    new Methods(nmap,owner)
  }

  /**
   * Клонирует и заменяет тип THIS, на указанный тип
   * @param thisType целевой тип
   * @return новая коллекция методов
   */
  def thisType(thisType:Type):Methods = {
    require(thisType!=null)
    val nmap:Map[String,Funs] = {
      map.map({case(name,funs)=>
        //val nfuns = funs.map( f=> f.typeReplace(Type.THIS, thisType) )
        val nfuns = funs.map( f=> f.typeReplace {
          case Type.THIS => Some(thisType)
          case gp:GenericPlaceholder =>
            if( gp.owner == Type.THIS ){
              Some(new GenericPlaceholder(gp.name, thisType))
            }else {
              None
            }
          case _ => None
        } )
        name -> new Funs(nfuns.toList)
      })
    }
    new Methods(nmap, Some(thisType))
  }

  /**
   * Клонирует и заменяет тип THIS, на указанный тип
   * @param replacement функция замены
   * @return новая коллекция методов
   */
  def typeReplace(replacement:Type=>Option[Type], newOwner:Option[Type]=None):Methods = {
    require(replacement!=null)
    require(newOwner!=null)
    val nmap:Map[String,Funs] = {
      map.map({case(name,funs)=>
        val nfuns = funs.map( f=> f.typeReplace(replacement) )
        name -> new Funs(nfuns.toList)
      })
    }
    new Methods(nmap, newOwner)
  }

  /**
   * Производит поиск метода по имени, с учетем наследственности
   * @param name искомый метод
   * @return найденый метод
   */
  def get(name:String):Option[Funs] = {
    require(name!=null)
    val declared = map.get(name)
    if( declared.isDefined ){
      Some(declared.get)
    }else if( owner.isDefined ){
      var clz = owner.get
      if( clz.extend.isEmpty ){
        None
      }else{
        clz.extend.get.methods.get(name)
      }
    }else{
      None
    }
  }
}

object Methods {
  def apply(map: Map[String, Funs], owner: Option[Type]=None): Methods = new Methods(map, owner)
  def apply(methods:(String,Fun)*):Methods = {
    require(methods!=null)
    if(methods.nonEmpty) {
      require(methods.map({case(name,fn)=>name!=null && fn!=null}).reduce((a,b)=>a&&b))
    }
    new Methods(
      methods.groupBy(_._1).map(e=> e._1 -> new Funs(e._2.map(_._2).toList))
    )
  }
}
