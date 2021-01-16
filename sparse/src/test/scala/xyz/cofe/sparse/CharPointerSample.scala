package xyz.cofe.sparse

import org.junit.jupiter.api.Test

class CharPointerSample {
  @Test
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
