package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type

/**
 * Область данных - переменные
 */
class VarScope {
  protected var vars : Map[String,Variable] = Map()
  
  /**
   * Возвращает список переменных
   * @return список переменных
   */
  def variables:Map[String,Variable] = vars
  
  /**
   * Получение переменной по ее имени
   * @param name имя переменной
   * @return переменная
   */
  def get(name:String):Option[Variable] = vars.get(name)
  
  /**
   * Получение переменной по ее имени
   * @param name имя переменной
   * @return переменная
   */
  def apply(name:String):Variable = vars(name)
  
  /**
   * Проверка наличия переменной
   * @param name имя переменной
   * @return true - переменная определена
   */
  def exists(name:String):Boolean = vars.contains(name)
  
  /**
   * Определение переменной, переменная не должна быть ранее определенной {{{ exists(var)==false }}}
   * @param name имя переменной
   * @param tip тип
   * @param init значение
   * @return SELF ссылка
   */
  def define(name:String,tip:Type,init:Any):VarScope = {
    require(name!=null)
    require(tip!=null)
    require(!exists(name))
    val v = Variable(tip, init)
    vars = vars + (name -> v)
    this
  }
  
  /**
   * Указание значения переменной, переменная должна быть ранее определенной {{{ exists(var)==true }}}
   * @param name имя переменной
   * @param tip тип
   * @param init значение
   * @return SELF ссылка
   */
  def set(name:String, newValue:Any):VarScope = {
    require(name!=null)
    require(exists(name))
    vars(name).write(newValue)
    this
  }
  
  /**
   * Определение переменной или установка ее значения
   * @param name имя переменной
   * @param tip тип переменной
   * @param newValue значение
   * @return SELF ссылка
   */
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
  
  /**
   * Определение переменных или установка их значения
   * @param variables переменные
   * @return SELF значение
   */
  def put( variables:((String,Type),Any)* ):VarScope = {
    require(variables!=null)
    variables.foreach( vdef => put(vdef._1._1, vdef._1._2, vdef._2))
    this
  }
}
