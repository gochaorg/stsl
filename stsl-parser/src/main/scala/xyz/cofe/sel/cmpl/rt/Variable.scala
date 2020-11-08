package xyz.cofe.sel.cmpl.rt

import xyz.cofe.sel.types.Type

/**
 * Переменная
 * @param value Значение переменной
 * @param varType Тип переменной
 * @param readOnly true - Только для чтения / false - Для чтения и записи
 */
class Variable(var value : Any, var varType : Type, var readOnly : Boolean=false, var overridable:Boolean=true ) {
}
