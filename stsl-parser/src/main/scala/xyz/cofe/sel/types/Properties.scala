package xyz.cofe.sel.types

class Properties(private val fields: List[Property], val inherit:List[Properties]=List() ) extends Seq[Property] {
  require(fields != null)
  require(inherit!=null)

  override def length: Int = fields.length

  override def apply(idx: Int): Property = fields(idx)

  override def iterator: Iterator[Property] = fields.iterator

  lazy val map: Map[String, Property] = {
    var m: Map[String, Property] = Map()
    foreach(fld => {
      m = m + (fld.name -> fld)
    })
    m
  }

  def add(field: Property): Properties = {
    require(field != null)
    new Properties(field :: fields, inherit)
  }

  def add(field: Seq[Property]): Properties = {
    require(field != null)
    new Properties(fields ++ field, inherit)
  }

  override def filter(f: Property => Boolean): Properties = {
    require(f != null)
    new Properties(fields.filter(f), inherit)
  }

  private var cached:Map[String,Option[Property]] = Map()

  def get(name:String):Option[Property] = {
    require(name!=null)
    if( cached.contains(name) ){
      cached(name)
    }else {
      val cacheVal = {
        val tProp = map.get(name)
        if (tProp.isDefined) {
          tProp
        } else {
          val pProp = inherit.find(p => p.map.contains(name))
          if (pProp.isDefined) {
            pProp.get.get(name)
          } else {
            None
          }
        }
      }
      cached = cached + ( name -> cacheVal )
      cacheVal
    }
  }

  def apply(name:String):Property = {
    require(name!=null)
    val prop = get(name)
    if( prop.isEmpty ){
      throw new RuntimeException(s"property $name not found")
    }
    prop.get
  }
}

object Properties {
  lazy val empty: Properties = new Properties(List())

  def apply(fields: Property *): Properties = new Properties(fields.toList)
}