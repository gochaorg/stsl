Указатель
====================

**Указатель** - указывает на позицию в исходном тексте.

Указатель представлен `trait Pointer[TOKEN,PTR,SELF <: Pointer[TOKEN,PTR,SELF]]`

- `TOKEN` - класс/тип соответствующий лексеме / токену
- `PTR` - класс/тип соответствующий позиции в тексте (например индекс символа или номер строки + номер символа в строке)
- `SELF` - конкретный тип указателя производного от `trait`

Случаи использования

- В случае если исходный текст представлен **строкой** - то, указатель это индекс символа в строке,
  см `trait CharPointer extends Pointer[Char,Int,CharPointer]`
- В случае если исходный текст представлен **списком** - то, указатель это индекс элемента в списке,
  см `class LPointer[TOKEN]( ptr:Int, val list: List[TOKEN] ) extends Pointer[TOKEN,Int,LPointer[TOKEN]]`

Пример использования
----------------------

```scala
package xyz.cofe.sparse

import org.junit.jupiter.api.Test

class CharPointerSample {
  def sample1():Unit = {
    val source = "Hello world"

    var ptr = new BasicCharPointer(0,source)

    // Отобразит текущую позицию - 0
    println(ptr.pointer())

    // Вернет первый символ строки - Some(H)
    println(ptr.lookup(0))

    // Вернет второй символ строки - Some(e)
    println(ptr.lookup(1))

    // Вернет Hell
    println(ptr.text(4))

    // Создание нового указателя который смещен на 3 позиции дальше
    var p2 = ptr.move(3);

    // Сравнение указателей - 1
    println( p2.compare(ptr) )

    // Вернет - lo
    println(p2.text(2))

    // Вернет - false
    println(p2.eof())

    // Перемещение указателя в конец строки
    // Вернет - true
    println(ptr.move(source.length).eof())

    // Чтение значения за пределами строки - None
    println(ptr.move(source.length).lookup(0))
  }
}
```
