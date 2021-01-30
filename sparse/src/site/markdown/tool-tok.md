Лексема
===========

Определение из википедии

Лексе́ма (от др.-греч. λέξις — слово, выражение, оборот речи) в лингвистике — слово как абстрактная единица морфологического анализа.

В одну лексему объединяются разные парадигматические формы (словоформы) одного слова. Например, словарь, словарём, словарю — это формы одной и той же лексемы, по соглашению пишущейся как СЛОВАРЬ

В данном случае это вот такой интерфейс:

```scala
package xyz.cofe.sparse

/**
  * Указатель на конец лексемы
 *
  * @tparam P Указатель
  */
trait Tok[P <: Pointer[_,_,_] ] {
  /**
    * Возвращает указатель на конец лексемы
    * @return Указатель
    */
  def end():P
}
```

Примеры
-----------------------------

Для удобства использования введен дополнительный класс `CToken`

```scala
class CToken( val begin : CharPointer
            , val end : CharPointer 
            ) extends Tok[CharPointer] {
  lazy val text : String
}
```

Пример лексемы обозначающей цифру:

```scala
class DigitToken( begin:CharPointer
                  , end:CharPointer
                  , val value:Int )
  extends CToken(begin,end)
```

Пример лексемы обозначающей число:

```scala
class IntergerTok(begin:CharPointer
                  , end:CharPointer
                  , val value:Int = 0 )
  extends CToken(begin,end)
```