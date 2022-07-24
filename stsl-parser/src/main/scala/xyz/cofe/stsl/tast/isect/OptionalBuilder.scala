package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.{GenericInstance, Named, TObject, Type}

/**
 * Создание опционального типа
 */
sealed trait OptionalBuilder {
  def build(of:OptionalField, tip:Type):Type
}

/**
 * Создание типа заменой типа-переменной
 */
case class OptBaker() extends OptionalBuilder {
  override def build(of:OptionalField, tip:Type): Type = {
    of.genericType.typeVarBake.thiz(of.typeParamName -> tip) match {
      case nameTypeResult:Named => tip match {
        case namedParamType:Named =>
          nameTypeResult.withName( s"${nameTypeResult.name}$$${of.typeParamName}=${namedParamType.name}" )
        case _ => nameTypeResult
      }
      case t => t
    }
  }
}

/**
 * Создание типа путем подстановки [[GenericInstance]]
 */
case class OptGenInstance() extends OptionalBuilder {
  override def build(of: OptionalField, tip: Type): Type =
    new GenericInstance[TObject](
      Map(of.typeParamName -> tip),
      of.genericType
    )
}
