package xyz.cofe.stsl.tast

/**
 * Стек вызовов лямбды
 */
class CallStack {
  private var stackInst : List[Map[String,Any]] = List()
  
  /**
   * Содержание стека
   * @return Содержание стека
   */
  def stack : List[Map[String,Any]] = stackInst
  
  def push(values:Map[String,Any]):Unit = {
    require(values!=null)
    stackInst = values :: stackInst
  }
  def pop():Unit = {
    if( stackInst.isEmpty ){
      throw new Error("empty stack")
    }
    stackInst = stackInst.drop(1)
  }
  def stackSize : Int = stackInst.size
  def get(name:String):Any = {
    require(name!=null)
    if( stackInst.isEmpty ){
      throw new Error("empty stack")
    }
    val value = stackInst.head.get(name)
    if( value.isEmpty )throw new Error(s"variable ${name} not found int stack head")
    value.get
  }
}
