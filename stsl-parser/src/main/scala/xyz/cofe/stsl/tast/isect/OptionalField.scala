package xyz.cofe.stsl.tast.isect

import xyz.cofe.stsl.types.TObject

/**
 * Указывает опциональный generic тип
 *
 * @param genericType generic тип
 * @param typeParamName имя типа-переменной
 */
case class OptionalField( genericType:TObject, typeParamName:String )
