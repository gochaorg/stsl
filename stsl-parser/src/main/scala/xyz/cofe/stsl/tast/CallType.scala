package xyz.cofe.stsl.tast

import xyz.cofe.stsl.types.{Fun, Type}

/**
 * Типы используемые при вызове метода
 * @param fun вызываемый метод
 * @param actual актуальный список типов параметров метода
 * @param expected ожидаемый список типов параметров метода
 * @param result результат вызова
 */
class CallType( val fun:Fun
                , val actual:List[Type]
                , val expected:List[Type]
                , val result:Type )
