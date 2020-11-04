package xyz.cofe.stsl.types

class TObject( Name:String,
               ogenerics:GenericParams=GenericParams(),
               oextend:Option[Type] = Some(Type.ANY),
               ofields:Fields=Fields(),
               omethods:Methods=Methods()
             ) extends Obj {
  require(Name!=null)
  require(ogenerics!=null)
  require(ofields!=null)
  require(omethods!=null)
  require(oextend!=null)

  override val name: String = Name

  override lazy val extend: Option[Type] = oextend
  override lazy val generics: GenericParams = ogenerics
  override lazy val fields: Fields = ofields
  override lazy val methods: Methods = omethods

  private val fieldsTypeVariablesMap = fields.filter(f => f.tip.isInstanceOf[TypeVariable]).map(f => f.name -> f.tip.asInstanceOf[TypeVariable]).toMap
  private val fieldsTypeVariables = fieldsTypeVariablesMap.values
  private val methodsTypeVariables = methods.funs.values.flatMap(f => f.funs).flatMap(f => f.typeVariables)
  private val typeVariables = fieldsTypeVariables ++ methodsTypeVariables
  typeVariables
    .filter( tv => tv.owner==Type.THIS )
    .foreach( vname =>
      if( !generics.params.map(_.name).contains(vname.name) ){
        throw TypeError(s"bind undeclared type variable $vname into Object")
      }
    )
}

object TObject {
  def apply( Name: String
           , ogenerics: GenericParams
           , oextend:Option[Type]
           , ofields: Fields
           , omethods: Methods): TObject = new TObject(Name, ogenerics, oextend, ofields, omethods)

  class Builder( val name:String ) {
    private var oextend:Option[Type] = Some(Type.ANY)
    def extend(ext:Option[Type]):Builder = {
      require(ext!=null)
      oextend = ext
      this
    }
    def extend(ext:Type):Builder = {
      require(ext!=null)
      oextend = Some(ext)
      this
    }

    private var ogenerics = GenericParams()
    def generics(newGenerics:GenericParams):Builder = {
      require(newGenerics!=null)
      ogenerics = newGenerics
      this
    }
    def generics(newGenerics:GenericParam*):Builder = {
      require(newGenerics!=null)
      ogenerics = GenericParams(newGenerics.toList)
      this
    }
    private var ofields = Fields()
    def fields(newFields:Fields):Builder = {
      require(newFields!=null)
      ofields = newFields
      this
    }
    def fields(newFields:(String,Type)*):Builder = {
      require(newFields!=null)
      ofields = Fields( newFields.map(f=>Field(f._1,f._2)).toList )
      this
    }
    private var omethods = Methods()
    def methods(newMethods:Methods):Builder = {
      require(newMethods!=null)
      omethods = newMethods
      this
    }
    def methods(newMethods:(String,Fun)*):Builder = {
      require(newMethods!=null)
      val map1:Map[String,Seq[Fun]] = newMethods.toList.groupBy(_._1).map({case(name,ls)=> name -> ls.map(_._2)}).toMap
      val map2 = map1.map({case(name,ls)=> name -> Funs(ls.toList)})
      omethods = Methods(map2)
      this
    }
    def build:TObject = {
      new TObject(name,ogenerics,oextend,ofields,omethods)
    }
  }

  def apply(Name: String) = new Builder(Name)
}