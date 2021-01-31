Синтаксическое правило языка
==============================

Синтаксическое правило - это функция для распознавания входной последовательности 
и в случае успешного распознавания возвращает лексема соответствующая классу символов.

Синтаксис ее такой:

```scala
trait GR[P <: Pointer[_,_,_],T <: Tok[P]] extends Function1[P,Option[T]] {}
```

- Это функция, которая принимает [указатель](tool-ptr.md) `P` на [символы или другие лексемы](tool-tok.md) `T`
- Функция может вернуть распознанную лексему `T`
- Входные символы в данном случае это лексемы класса `Character`
- Выходные лексемы могут принадлежать к другому классу, нежели входные

Пример использования
---------------------

### Пример с цифрами

[Лексема](tool-tok.md) цифры

```scala
import xyz.cofe.sparse._;

class DigitToken( begin:CharPointer
                  , end:CharPointer
                  , val value:Int )
  extends CToken(begin,end)

object DigitToken {
  def apply(begin: CharPointer, end: CharPointer, value: Int)
  = new DigitToken(begin, end, value)
}
```

- В качестве указателя на начало и конец лексемы в потоке символов используется `CharPointer`
- Значение цифры `value :Int`
- В качестве базового класса используется
  `CToken( val begin : CharPointer, val end : CharPointer ) extends Tok[CharPointer]`

Синтаксическое правило распознавания лексемы `DigitToken`

```scala
import xyz.cofe.sparse._;

object LexerSample {
  // Цифры
  val digits = "0123456789"

  // Правило распознавания цифры
  val digit: GR[CharPointer, DigitToken] =
    ptr => // Указатель на начало распознаваемой последовательности
      ptr.lookup(0).map(chr => { // читаем первый (0) символ относительно указателя
        val i = digits.indexOf(chr) // прочитанный символ входит в искомые ?
        if (i < 0) {
          None // прочитанный символ не соответствует цифре
        } else {
          Some(
            // символ соответствует цифре
            DigitToken(
              ptr, // начало лексемы
              ptr.move(1), // конец лексемы, передвигаем указатель на 1 символ 
              i // значение цифры
            )
          )
        }
      }
    ).get
}
```

### Пример с числами

Лексема числа

```scala
import xyz.cofe.sparse._;

/**
 * Лексема числа
 * @param begin начало лексемы
 * @param end конец лексемы
 * @param value значение
 */
class IntergerTok(begin:CharPointer
                  , end:CharPointer
                  , val value:Int = 0 )
  extends CToken(begin,end) {
  override def toString: String 
    = s"IntergerTok(begin=$begin,end=$end,value=$value)"
}

/**
 * Создание лексемы
 */
object IntergerTok {
  /**
   * Создание лексемы
   * @param begin начало лексемы
   * @param end конец лексемы
   * @param digits цифры
   * @param base основание числа (10 - десятичная система)
   * @return Лексема числа
   */
  def apply( begin:CharPointer
           , end:CharPointer
           , digits:Seq[DigitToken]
           , base:Int = 10 ):IntergerTok = {
    val dgts = digits.reverse.zipWithIndex.map( { case (d,idx) =>
      if( idx==0 ) d.value else {
        var b = base
        (0 until idx-1).foreach( _ => {
          b = b * base
        })
        d.value * b
      }
    })
    new IntergerTok(begin,end,dgts.sum)
  }
}
```

- Лексема имеет начало (`begin:CharPointer`) и конец (`end:CharPointer`) в исходном тексте
- При создании используются лексемы цифр `digits:Seq[DigitToken]`

#### Синтаксическое правило

Синтаксическое правило можно составить из набора правил оперируя ими как объектами детского конструктора.

Так в частности для десятичного целого числа правило можно описать словами так:

1. Число состоит из цифр
2. Число должно содержать одну или более цифр

Формально описание этого правила будет примерно таким

    Число ::= Цифра { Цифра }

- `::=` - определение правила,
    - слева указывается имя правила (`Число`)
    - справа - содержание правила (`Цифра { Цифра }`)
- `{}` - указывает повтор 0 или более раз содержания в скобках - соответствует циклу `while`

![](images/tool-gr-02.png)

#### Реализация правила

```scala
/**
 * Пример лексического анализа
 */
object LexerSample {
  // код digit приведен выше
  
  // Импорт дополнительных операторов
  import xyz.cofe.sparse.GOPS._

  // Правило распознавания числа
  val intNumber: GR[CharPointer, IntergerTok] = digit + digit*0 ==> { (a, b) =>
    IntergerTok(a.begin, b.end, List(a) ++ b.toList )
  }
}
```

* `import xyz.cofe.sparse.GOPS._` - импортирует операторы для манипулирования синтаксическими правилами `GR[_,_]`
* `digit` - это переменная типа `GR[CharPointer, DigitToken]` - лямбда переменная для распознавания цифр
* `digit + digit*0 ==> { (a, b) => ... }` - это комбинация их двух функций
  * первая `digit` - это лямбда для распознавания первой цифры
    * функция возвращает экземпляр `DigitToken`
  * вторая `digit*0` - цикл, который распознает 0 или более цифр. псевдокод `while( condition ){ digit(ptr); ... }`
    * функция возвращает список `List[DigitToken]`
  * оператор `+` комбинирует первую и вторую функцию в функцию которая работает с последовательностью входных данных
    * результат перового операнда `DigitToken` и второго операнда `List[DigitToken]` передаются в функцию следующую за `==>`, в примере `a` будет иметь тип `DigitToken`, `b : List[DigitToken]`
* `IntergerTok(a.begin, b.end, List(a) ++ b.toList )` - создает лексему `IntergerTok`
  * `a.begin` - возвращает указатель на начало лексемы
  * `b.end` - возвращает указатель на конец лексемы
  * `List(a) ++ b.toList` - создает список лексем цифр `List[DigitToken]`, включающих первую цифру (`a`) и последующие цифры (`b`)

Более подробно [смотрите комбинации правил](tool-gops.md)