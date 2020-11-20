package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Invoke, Type}

/**
 * "Перекрытие" мера схожести между типами данных функции (типами параметрами и возможно результатом)
 * @param exists фактический тип данных
 * @param expected ожидаемый тип данных
 * @param implicitConvertion неявное преобразование
 */
class TypeOverlap( val exists: Type
                 , val expected: Type
                 , val implicitConvertion:Option[Invoke] = None ) {
}
