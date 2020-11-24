package xyz.cofe.stsl.types

class WriteableField( name:String
                    , tip:Type
                    , val reading:Any=>Any
                    , val writing:(Any,Any)=>Any
                    ) extends Field( name, tip ) {
  require(reading!=null)
  require(writing!=null)
}
