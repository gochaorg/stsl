Парсер
=================

Парсер - эта часть, которая анализирует [лексемы](lexer.md) и формирует абстрактное синтаксическое дерево.

Парсер оперирует лексемами, согласно набору формальных правил (грамматики). 

Для примера возьмем правило сложения чисел

    add ::= num ( '+' | '-' ) add

по сути, это правило разворачивается в такой (псевдо) код:

```java
import xyz.cofe.sparse.CharPointer;

import java.util.Optional;

public class Parser {
    public Optional<Tuple2<Num, CharPointer>> num(CharPointer pointer) {
        //...
    }

    public Optional<Tuple2<Add, CharPointer>> add(CharPointer pointer) {
        // Ожидаем лексему числа
        var num = num(pointer);
        if (num.isEmpty()) return Optional.empty();
        
        // Получаем указатель на следующую лексему
        pointer = num.get().b();
        
        // Временный результат
        var addRes = num.get().a().asAdd();
        
        // Ожидаем оператор + или -
        var op = List.of( "+", "-" ).contains( pointer.get() );
        if( op.isEmpty() )return Optional.of( addRes );

        // Получаем указатель на следующую лексему
        var secondArgPtr = pointer.next();
        
        // Ожидаем узел Add, выполняем рекурсивный вызов
        var secondAdd = add(secondArgPtr);
        
        if( secondAdd.isEmpty() )return Optional.of( addRes );
        
        // объединяем результат
        addRes = addRes.join( op, secondAdd );
        
        return Optional.of( addRes );
    }
}
```

Как можно видеть, из простой строчки грамматики, получается очень много строк Java.

Если согласно данной грамматики для входной последовательности: `1 + 2 - 3 + 4` будет создано
такое дерево:

- Add +
  - Add - 
    - Add +
      - Num 3 
      - Num 4
    - Num 2
  - Num 1

Ниже в примерах подобное дерево будет представлено по другому:

    BinaryAST +
    -| BinaryAST -
    -|-| BinaryAST +
    -|-|-| LiteralAST IntNumberTok 3
    -|-|-| LiteralAST IntNumberTok 4
    -|-| LiteralAST IntNumberTok 2
    -| LiteralAST IntNumberTok 1

- `BinaryAST` - это AST узел бинарного оператора
  - `BinaryAST +` - это в частности оператор `+`
- `LiteralAST` - это узел AST который указывает на лексему
  - `LiteralAST IntNumberTok 4` - в частности указывает на лексему `IntNumberTok`, ее значение: `4`

Примеры простых выражений
-------------------------

Исходник

    123 + 234 * 345

AST

    BinaryAST +
    -| LiteralAST IntNumberTok 123
    -| BinaryAST *
    -|-| LiteralAST IntNumberTok 234
    -|-| LiteralAST IntNumberTok 345

### Замечание

- Операторы `-` и `+` имеют ниже приоритет, чем `*`, `/`, `%`. Более подробно определяется формальной грамматикой. 

Очередность
------------------

Исходник

    123 / 234 - 345

AST

    BinaryAST -
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 123
    -|-| LiteralAST IntNumberTok 234
    -| LiteralAST IntNumberTok 345

Исходник

    123 - 234 / 345

AST

    BinaryAST -
    -| LiteralAST IntNumberTok 123
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 234
    -|-| LiteralAST IntNumberTok 345

### Замечание

- В пределах своего приоритета бинарные операторы следуют слева на право.

Круглые скобки
------------------------

Круглые скобки задают очередность. Круглые скобки представлены узлом `DelegateAST`

Исходник

    ( 123 - 234 ) * 2

AST

    BinaryAST *
    -| DelegateAST
    -|-| BinaryAST -
    -|-|-| LiteralAST IntNumberTok 123
    -|-|-| LiteralAST IntNumberTok 234
    -| LiteralAST IntNumberTok 2

Переменные
---------------------

Исходник

    a - b / c

    BinaryAST -
    -| IdentifierAST IdentifierTok a
    -| BinaryAST /
    -|-| IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok c

Операции сравнения
-------------------------

Исходник

    a < b

AST

    BinaryAST <
    -| IdentifierAST IdentifierTok a
    -| IdentifierAST IdentifierTok b

Тренарный оператор
--------------------------

Исходник

    a ? b : c

AST

    TernaryAST ? :
    -| IdentifierAST IdentifierTok a
    -| IdentifierAST IdentifierTok b
    -| IdentifierAST IdentifierTok c

postFix
---------------------------------

Формальная грамматика

    atom ::= lambdaWithoutParams 
           | lambdaWithParams 
           | objDef 
           | parenthes
           | unary
           | literal
           | identifier

    fieldAccessOp ::= '.'
    callStart ::= '('
    callEnd ::= ')'
    callStartEnd ::= '()'
    argDelim ::= ','

    postFix ::= atom { fieldAccessOp identifier [ call ] |  call }

    call ::= callStartEnd
           | callStart [ expression { argDelim expression } ] callEnd

    mul ::= binary( postFix,  operator("*","/","%"), postFix )

В пределах грамматики выделяется понятие атома - где атом это некое не делимое значение:
число, идентификатор, лямбда...

А postFix - это доступ к свойству атома или вызов метода атома.

Ниже будет приводиться примеры postFix.


### Доступ к свойству объекта

Исходник

    x.a + x.b.c

AST

    BinaryAST +
    -| PropertyAST a
    -|-| IdentifierAST IdentifierTok x
    -| PropertyAST c
    -|-| PropertyAST b
    -|-|-| IdentifierAST IdentifierTok x

### Вызов метода и вызов функции

Исходник

    a( 10, 12 ) + b.a( 1, 2, 3 )

AST

    BinaryAST +
    -| CallAST
    -|-| IdentifierAST IdentifierTok a
    -|-| LiteralAST IntNumberTok 10
    -|-| LiteralAST IntNumberTok 12
    -| CallAST
    -|-| PropertyAST a
    -|-|-| IdentifierAST IdentifierTok b
    -|-| LiteralAST IntNumberTok 1
    -|-| LiteralAST IntNumberTok 2
    -|-| LiteralAST IntNumberTok 3

### Вызов метода без аргументов

Исходник

    obj.m()

AST

    CallAST
    -| PropertyAST m
    -|-| IdentifierAST IdentifierTok obj

Определение лямбды
-----------------------------

Формальный синтаксис

    typeName ::= identifier

    lambdaParam ::= identifier ':' typeName
    lambdaRecusionParam ::= identifier '::' typeName

    lambdaArrow ::= '=>'
    lambdaArgDelim ::= ','

    lambdaEmptyParams1 ::= '(' ')' '=>'
    lambdaEmptyParams2 ::= '()' '=>'
    lambdaEmptyParams3 ::= '()=>'
    lambdaEmptyParams4 ::= '( ' ')=>'
    lambdaEmptyParamsN ::= lambdaEmptyParams1 | lambdaEmptyParams2 | lambdaEmptyParams3 | lambdaEmptyParams4
    lambdaWithoutParams ::= lambdaEmptyParamsN expression

    lambdaWithParams ::= lambdaParam { ',' lambdaParam } [ ',' lambdaRecusionParam ] '=>' expression


Исходник

    a:int , b:int => a+b

AST

    LambdaAST
    -| ParamAST IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok a
    -|-| TypeNameAST int
    -| ParamAST IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok b
    -|-| TypeNameAST int
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b

### Определение рекурсивной лямбды

Исходник

    a:int , b:int , r :: int => a+b

AST

    LambdaAST recursion: ParamAST IdentifierAST IdentifierTok r
    -| ParamAST IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok a
    -|-| TypeNameAST int
    -| ParamAST IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok b
    -|-| TypeNameAST int
    -| ParamAST IdentifierAST IdentifierTok r
    -|-| IdentifierAST IdentifierTok r
    -|-| TypeNameAST int
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b

### Определение лямбды без параметров

Исходник

    () => a+b

AST

    LambdaAST
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b

Определение объекта
-----------------------------

Формальный синтаксис

    emptyObj1 ::= '{}'
    emptyObj2 ::= '{' '}'
    objKeyVal ::= identifier ':' expression
    objNonEmpty ::= '{' objKeyVal { [ ',' objKeyVal ] } '}'
    objDef ::= emptyObj1 | emptyObj2 | objNonEmpty


Исходник

    { k1: 1, k2: 2 }

AST

    PojoAST
    -| PojoItemAST k1
    -|-| LiteralAST IntNumberTok 1
    -| PojoItemAST k2
    -|-| LiteralAST IntNumberTok 2
    PojoAST
    -| PojoItemAST k1
    -|-| LiteralAST StringTok abc
    -| PojoItemAST k2
    -|-| LiteralAST IntNumberTok 2

### Определение пустого объекта

Исходник

    { }

AST

    PojoAST

Исходник

    {}

AST

    PojoAST

Бинарные операторы
----------------------

В формальной грамматике будет встречаться например такая конструкция:

    add ::= binary( mul,  operator("+","-"), mul )

по сути, это сокращение этой конструкции:

    add ::= mul { ( '+' | '-' ) mul }

Что означает, что операция сложения представляет последовательность операций
умножения разделенных оператором `+` или `-`

Генерируемое дерево для `a + b + c + d` будет:

    -| BinaryAST +
    -|-| BinaryAST +
    -|-|-| BinaryAST +
    -|-|-|-| IdentifierAST IdentifierTok a
    -|-|-|-| IdentifierAST IdentifierTok b
    -|-|-| IdentifierAST IdentifierTok c
    -|-| IdentifierAST IdentifierTok d

Вычисления будут слева на право:

1. `r = a + b`
2. `r = r + c`
3. `r = r + d`

Операторы
------------------------------

- На уровне лексики, различия операторов между собой не большое.
- На уровне лексики не выделяются бинарные или унарные операторы.
- На уровне грамматики операторы начинают играть роль и они задаются например так: `operator("+","-")`, это равнозначно этой записи: `('+' | '-')`

Формальный синтаксис (грамматика)
-------------------------------------

    // Это начальное правило, и любая программа stsl сводиться к этому правилу
    expression ::= ifOp

    // литиральное значение (число, строка, ...), определяется лексикой
    literal ::= ...

    // идентификатор, определяется лексикой
    identifier ::= ...

    // оператор, определяется лексикой
    // operator в данном случае это функция, с переменным кол-вом аргументов
    // каждый аргумент задает возможно ожидаемый оператор на входе
    operator ::= fn( String* ) : OpLiteral { ... }

    binary ::= fn( AstGR, OpLiteral, AstGR ) : AstGR { ... }

    parenthes ::= '(' expression ')'

    unary ::= ( '-' | '!' ) expression

    typeName ::= identifier

    lambdaParam ::= identifier ':' typeName
    lambdaRecusionParam ::= identifier '::' typeName

    lambdaArrow ::= '=>'
    lambdaArgDelim ::= ','

    lambdaEmptyParams1 ::= '(' ')' '=>'
    lambdaEmptyParams2 ::= '()' '=>'
    lambdaEmptyParams3 ::= '()=>'
    lambdaEmptyParams4 ::= '( ' ')=>'
    lambdaEmptyParamsN ::= lambdaEmptyParams1 | lambdaEmptyParams2 | lambdaEmptyParams3 | lambdaEmptyParams4
    lambdaWithoutParams ::= lambdaEmptyParamsN expression

    lambdaWithParams ::= lambdaParam { ',' lambdaParam } [ ',' lambdaRecusionParam ] '=>' expression

    emptyObj1 ::= '{}'
    emptyObj2 ::= '{' '}'

    objKeyVal ::= identifier ':' expression

    objNonEmpty ::= '{' objKeyVal { [ ',' objKeyVal ] } '}'

    objDef ::= emptyObj1 | emptyObj2 | objNonEmpty

    atom ::= lambdaWithoutParams 
           | lambdaWithParams 
           | objDef 
           | parenthes
           | unary
           | literal
           | identifier

    fieldAccessOp ::= '.'
    callStart ::= '('
    callEnd ::= ')'
    callStartEnd ::= '()'
    argDelim ::= ','

    postFix ::= atom { fieldAccessOp identifier [ call ] |  call }

    call ::= callStartEnd
           | callStart [ expression { argDelim expression } ] callEnd

    mul ::= binary( postFix,  operator("*","/","%"), postFix )
    add ::= binary( mul,  operator("+","-"), mul )
    cmp ::= binary( add,  operator("==","!=","<",">","<=",">="), add )
    bool ::= binary( cmp,  operator("&","|"), cmp )

    // это функция тренарного оператора
    ternary( 
       // это что будет условием
       condition:AstGR, 

       // разделитель между условием и вариантом решения
       question:OpLiteral,

       // описывает позитивный вариант
       success:AstGR, 

       // разделитель между позитивным и негативным вариантом
       elseOp:OpLiteral, 

       // описывает негативный вариант
       failure:AstGR ): AstGR

    ifOp ::= ternary( bool, operator("?"), bool, operator(":"), bool )