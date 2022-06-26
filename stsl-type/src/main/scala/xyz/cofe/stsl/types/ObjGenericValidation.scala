package xyz.cofe.stsl.types

/**
 * Проверка Generic параметров
 */
trait ObjGenericValidation extends Obj {
  self =>
  
  protected var fieldsTypeVariablesMapCache:Option[Map[String, TypeVariable]] = None
  
  /** список полей которые содержат TypeVariable */
  protected def fieldsTypeVariablesMap: Map[String, TypeVariable] = {
    self match {
      case freezing: Freezing if fieldsTypeVariablesMapCache.isDefined && freezing.freezed =>
        fieldsTypeVariablesMapCache.get
      case _ =>
        val computedValue =
          fields
            .filter(f => f.tip.isInstanceOf[TypeVariable])
            .map(f => f.name -> f.tip.asInstanceOf[TypeVariable])
            .toMap
        fieldsTypeVariablesMapCache = Some(computedValue)
        computedValue
    }
  }
  
  protected def fieldsTypeVariables: Iterable[TypeVariable] =
    fieldsTypeVariablesMap.values
  
  protected def methodsTypeVariables: Iterable[TypeVariable] =
    methods.funs.values.flatMap(f => f.funs).flatMap(f => f.typeVariables)
  
  protected def typeVariables: Iterable[TypeVariable] =
    fieldsTypeVariables ++ methodsTypeVariables
  
  /**
   * Проверка что указанные типы-переменных соответствуют объявленным
   */
  protected def validateTypeVariables():Unit = {
    typeVariables
      .filter( tv => tv.owner==Type.THIS )
      .foreach( vname =>
        if( !generics.params.map(_.name).contains(vname.name) ){
          throw TypeError(s"bind undeclared type variable $vname into Object")
        }
      )
  }
}
