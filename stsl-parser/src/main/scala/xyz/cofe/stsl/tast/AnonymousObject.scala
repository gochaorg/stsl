package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.Type.THIS
import xyz.cofe.stsl.types.{CallableFn, Fun, Param, Params, TAnon}

/**
 * Работа с TAnon
 */
object AnonymousObject {
  /**
   * Ключ в Map
   */
  val TypeDefinition: Object = new Object {
    override def toString: String = "TypeDefinition"
  }
  
  /**
   * Получает тип для Map
   * @param anonObj экземпляр Map
   * @return тип
   */
  def definitionOf( anonObj:Any ):Option[TAnon] = {
    require(anonObj!=null)
    anonObj match {
      case m:java.util.Map[Any,Any] =>
        m.get(TypeDefinition) match {
          case d: TAnon => Some(d)
          case _ => None
        }
      case _ => None
    }
  }
  
  /**
   * Генерирует вызываемый метод (функцию) на основании существующей функции для вызова методов анонимного объекта (TAnon)
   *
   * Новый метод может генерировать в runTime следующие исключения
   *
   * <ul>
   *  <li> RuntimeException "can't call $fun" если нет ни одного параметра, соответственно нет ссылки this
   *  <li> RuntimeException "undefined type of this arg ($self)" первый аргумент не является экземпляром TAnon
   *  <li> RuntimeException "method $methName not found in $anonType" отсутствует метод
   *  <li> RuntimeException "method $methName($fun) not found in $anonType" отсутствует метод с заданными аргументами
   *  <li> RuntimeException "method $methName($fun) not callable" заданный метод найден, но он не является CallableFn
   * </ul>
   * @param methName имя вызываемого метода
   * @param fun функция (сигнатура)
   * @param insertThisArg добавить this параметр в генерируемый метод
   * @param sendThisArg передавать this параметр в целевой метод
   * @return новый метод.
   */
  def anonCallable( methName:String, fun:Fun, insertThisArg:Boolean=true, sendThisArg:Boolean=false ):CallableFn = {
    new CallableFn(
      fun.generics,
      if( insertThisArg ){
        Params((Param("this",THIS)) :: fun.parameters.toList)
      }else{
        fun.parameters
      },
      fun.returns,
      call = args => {
        if( args.isEmpty )throw new RuntimeException(s"can't call $fun")
        val self = args.head
        AnonymousObject.definitionOf(self) match {
          case Some(anonType) => anonType.methods.get(methName) match {
            case Some(funs) =>
              funs.find( f => f.sameTypes(fun) ) match {
                case Some(targetFun) => targetFun match {
                  case callFn: CallableFn =>
                    callFn.invoke(if(sendThisArg) args else args.tail)
                  case _ => throw new RuntimeException(s"method $methName($fun) not callable")
                }
                case None => throw new RuntimeException(s"method $methName($fun) not found in $anonType")
              }
            case None => throw new RuntimeException(s"method $methName not found in $anonType")
          }
          case None => throw new RuntimeException(s"undefined type of this arg ($self)")
        }
      }
    )
  }
  
  sealed trait MethodBuilder {
    def build( name:String, impl:Fun ):Fun
  }
  
  object MethodBuilder {
    object AsIs extends MethodBuilder {
      override def build(name: String, impl: Fun): Fun = impl
    }
    object AnonCallable extends MethodBuilder {
      override def build(name: String, impl: Fun): Fun = anonCallable(name, impl)
    }
    object ThisCallable extends MethodBuilder {
      //noinspection SimplifyBooleanMatch
      override def build(name: String, impl: Fun): Fun = {
        impl.parameters.headOption match {
          case Some(firstParam) => firstParam.tip == THIS match {
            case true => anonCallable(name, impl, insertThisArg=false, sendThisArg=true)
            case false => anonCallable(name, impl)
          }
          case None => anonCallable(name, impl)
        }
      }
    }
  }
  
  def asIsMethodBuilder():MethodBuilder = MethodBuilder.AsIs
  def anonCallableMethodBuilder():MethodBuilder = MethodBuilder.AnonCallable
  def thisCallableMethodBuilder():MethodBuilder = MethodBuilder.ThisCallable
}
