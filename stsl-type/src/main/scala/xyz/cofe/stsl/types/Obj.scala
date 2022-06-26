package xyz.cofe.stsl.types

/**
 * Интерфейс класса,
 * любой клас обладает:
 * <ul>
 *   <li> Списком полей/атрибутов
 *   <li> Список методов
 * </ul>
 */
trait Obj
  extends Type
    with FieldsProperty
    with MethodsProperty
    with InheritedFields
    with InheritedMethods
{
}
