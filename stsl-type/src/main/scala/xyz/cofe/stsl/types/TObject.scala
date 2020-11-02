package xyz.cofe.stsl.types

class TObject( Name:String,
               ogenerics:GenericParams=GenericParams(),
               ofields:Fields=Fields(),
               omethods:Methods=Methods()
             ) extends Obj {
  require(Name!=null)
  require(ogenerics!=null)
  require(ofields!=null)
  require(omethods!=null)
  override val name: String = Name
  override lazy val generics: GenericParams = ogenerics
  override lazy val fields: Fields = ofields
  override lazy val methods: Methods = omethods
}

object TObject {
  def apply(Name: String, ogenerics: GenericParams, ofields: Fields, omethods: Methods): TObject = new TObject(Name, ogenerics, ofields, omethods)

  class Builder( val name:String ) {
    private var ogenerics = GenericParams()
    def generics(newGenerics:GenericParams) = {
      require(newGenerics!=null)
      ogenerics = newGenerics
      this
    }
    private var ofields = Fields()
    def fields(newFields:Fields) = {
      require(newFields!=null)
      ofields = newFields
      this
    }
    def fields(newFields:(String,Type)*) = {
      require(newFields!=null)
      ofields = Fields( newFields.map(f=>Field(f._1,f._2)).toList )
      this
    }
    private var omethods = Methods()
    def methods(newMethods:Methods) = {
      require(newMethods!=null)
      omethods = newMethods
      this
    }
    def build:TObject = {
      new TObject(name,ogenerics,ofields,omethods)
    }
  }

  def apply(Name: String) = new Builder(Name)
}