Язык stsl
=============================================

stsl - это аббревиатура от Strong Typed Simple Language

Это "домашний" проект, в рамках изучения основ построения языков программирования.

Основные цели, которые я выдвинул что должен уметь язык:

- Строгая типизация, не должно быть, не одной инструкции, которая не имела бы тип результата своего выполнения. Тип результата должен быть известен до начала выполнения
- Язык должен поддерживать определение лямбда-функций
- Язык должен поддерживать вычисления, которые могут быть записаны в одну строку
- Язык должен поддерживать определение собственных операторов
- Язык должен уметь работать с параметризованными типами данных
- Параметризация типа должна различать случаи Ко/Контр/Ин вариантность
- Язык не должен использовать средства рефлексии JVM
- Лямбда функции можно передавать в качестве аргумента метода
- Типы данных должны поддерживать объекты (ООП)
- Лямбды могут быть рекурсивными

В текущей реализации это не совсем полноценный язык, т.к. не достигнуты цели (которые первоначально и не ставились):

- Определение группы функций
- Определение классов ООП
- Изменяемые данные, формально в языке нет операции присвоения значения переменной
- Последовательности действий, то что обычно в языка является блоком инструкций
- Ветвления и циклы

Эти цели небыли поставлены, по той причине, что ранее они решались в рамках других проектов, других не типизированных языков. А для задачи строгой типизации напрямую, сразу их решать - нет надобности, необходимо сначала разобраться с более простыми конструкциями языка.

Текущая реализация не мешает появлению, не реализованных функций (классов, функций, ветвления ... и т.д.)

Фазы компиляции и выполнения
----------------------------

Транслятор stsl в себе содержит несколько частей, которые работают в разных фазах.

Входными данными является

- Исходный текст
- Переменные/данные
- Реализации типов данных

Результирующими данными являются

- данные результата
- тип данных результата

Между входными данными и результатом выполняется следующая работа

1. Лексический анализ. Входные данные: исходный текст; результат: список лексем 
2. Синтаксический анализ. Входные данные: список лексем; результат: Абстрактное синтаксическое дерево (AST)
3. Анализ типов данных. Входные данные: AST; результат: Типизированное синтаксическое дерево (TAST)

Простой пример
----------------------------

```java
// Импорт классов
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scala.Option;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.*;
import xyz.cofe.stsl.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SampleTest {
    @Test
    public void test01(){
        // Начало теста
        System.out.println("test01");
        System.out.println("============");

        // Определяем область типов
        // Базовые типы уже предопределены
        //   1. В классе xyz.cofe.stsl.tast.JvmType
        //   2. В методе xyz.cofe.stsl.tast.Toaster.compile
        TypeScope ts = new TypeScope();
        
        // Импортируем имплицитные преобразования типов данных
        // из BYTE в SHORT
        // из BYTE в INT
        // из ... в ...
        // из SHORT в INT
        // из ... в ...
        ts.setImplicits(JvmType.implicitConversion());

        // Парсинг исходного кода в AST дерево
        Option<AST> ast = Parser.parse("20 + 20 / 2");
        
        // Вывод дерева на экран
        ASTDump.dump(ast.get());

        // Определение области переменных
        VarScope varScope = new VarScope();

        // Создание "тостера" для получения типизированного дерева
        // По сути компиляция
        Toaster toaster = new Toaster(ts,varScope);
        
        // Компиляция AST дерева
        TAST tast = toaster.compile(ast.get());
        
        // Вывод дерева на экран
        TASTDump.dump(tast);

        // Получение типа данных результата вычислений
        System.out.println("compiled result type="+tast.supplierType());

        // Вычисление
        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
}
```

Результат теста

    test01
    ============
    BinaryAST +
    -| LiteralAST IntNumberTok 20
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 20
    -|-| LiteralAST IntNumberTok 2
    BinaryAST + :: int
    -| LiteralAST IntNumberTok 20 :: int
    -| BinaryAST / :: int
    -|-| LiteralAST IntNumberTok 20 :: int
    -|-| LiteralAST IntNumberTok 2 :: int
    compiled result type=int
    compiled result value=30

Разберем результат, первоначально исходник был такой: `20 + 20 / 2`, в результате работы `ast = Parser.parse("20 + 20 / 2")` было создано такое дерево:

    BinaryAST +
    -| LiteralAST IntNumberTok 20
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 20
    -|-| LiteralAST IntNumberTok 2

А после работы тостера `tast = toaster.compile(ast.get())` было создано новое дерево, в котором уже вычислены типы данных и функция вычисления результата. Дерево вот такое:

    BinaryAST + :: int
    -| LiteralAST IntNumberTok 20 :: int
    -| BinaryAST / :: int
    -|-| LiteralAST IntNumberTok 20 :: int
    -|-| LiteralAST IntNumberTok 2 :: int

Теперь для каждого узла проставлен тип данных: `:: int`

Тип результата можно получить через такой код: `tast.supplierType()`, а результат так: `tast.supplier().get()`

- `tast.supplierType()` - возвращает тип, он уже вычислен
- `tast.supplier().get()` - вычисляет значение в момент вызова `.get()`

Дополнительные темы
----------------------

- [Лексер и лексический анализ](lexer.md)
- [Парсер и синтаксис](parser.md)
- ["Тостер" и типизированное дерево](toaster.md)
- [Вывод типов](type-inference.md)
- [Метаинформация и типы данных](https://github.com/gochaorg/stsl/blob/master/stsl-type/src/site/markdown/types.md)