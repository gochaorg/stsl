package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type

class VarScope {
  protected var vars : Map[String,Variable] = Map()
  def variables:Map[String,Variable] = vars
  def get(name:String):Option[Variable] = vars.get(name)
  def apply(name:String):Variable = vars(name)
  def exists(name:String):Boolean = vars.contains(name)
  def define(name:String,tip:Type,init:Any):VarScope = {
    require(name!=null)
    require(tip!=null)
    require(!exists(name))
    val v = Variable(tip, init)
    vars = vars + (name -> v)
    this
  }
  def set(name:String, newValue:Any):VarScope = {
    require(name!=null)
    require(exists(name))
    vars(name).write(newValue)
    this
  }
  def put(name:String, tip:Type, newValue:Any):VarScope = {
    require(name!=null)
    val v = vars.get(name)
    if( v.isDefined ){
      if( v.get.tip==tip ){
        v.get.write(newValue)
      }else{
        val v = Variable(tip, newValue)
        vars = vars + (name -> v)
      }
    }else{
      val v = Variable(tip, newValue)
      vars = vars + (name -> v)
    }
    this
  }
  def put( variables:((String,Type),Any)* ):VarScope = {
    require(variables!=null)
    variables.foreach( vdef => put(vdef._1._1, vdef._1._2, vdef._2))
    this
  }
}
