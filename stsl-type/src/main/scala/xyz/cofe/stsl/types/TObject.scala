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
