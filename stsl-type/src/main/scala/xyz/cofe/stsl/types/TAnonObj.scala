package xyz.cofe.stsl.types

/**
 * Анонимный объект
 * @param ogenerics Параметры типа
 * @param oextend Какой тип расширяет
 * @param ofields Атрибуты/поля класса
 */
class TAnonObj(
               val generics:MutableGenericParams=new MutableGenericParams(),
               val fields:MutableFields=new MutableFields(),
               val methods:MutableMethods=new MutableMethods()
             ) extends Obj {
  override type GENERICS = MutableGenericParams
  override type FIELDS = MutableFields
  override type METHODS = MutableMethods
}

