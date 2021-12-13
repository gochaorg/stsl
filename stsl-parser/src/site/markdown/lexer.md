Лексемы и лексический анализ
=============================

Пробельные символы и комментарии
------------------------------------

### Формальные правила

    WS // Пробельные символы

    // Комментарий 
    comment ::= singleLineComment | multilLineComment  
    singleLineComment ::= '//' { любой_символ_кроме_пееревода_строк } [символ_пееревода_строк]
    multilLineComment ::= '/*' { любой_символ } '*/' 

### Описание

К пробельным символам относятся все символы для которых функция 
[Character.isWhitespace()](https://docs.oracle.com/javase/7/docs/api/java/lang/Character.html#isWhitespace(char)) 
вернет ответ `true`

* Это пробел Unicode (SPACE_SEPARATOR, LINE_SEPARATOR или PARAGRAPH_SEPARATOR), 
  но также не неразрывный пробел (`\u00A0`, `\u2007`, `\u202F`).
* Это `\t`, ГОРИЗОНТАЛЬНАЯ ТАБЛИЦА U + 0009.
* Это `\n`, U + 000A LINE FEED.
* Это `\u000B`, ВЕРТИКАЛЬНАЯ ТАБЛИЦА U + 000B.
* Это `\f`, U + 000C FORM FEED.
* Это `\r`, ВОЗВРАТ ПЕРЕВОЗКИ U + 000D.
* Это `\u001C`, U + 001C FILE SEPARATOR.
* Это `\u001D`, U + 001D ГРУППОВОЙ РАЗДЕЛИТЕЛЬ.
* Это `\u001E`, U + 001E ЗАПИСЬ СЕПАРАТОР.
* Это `\u001F`, U + 001F UNIT SEPARATOR.

Комментарий - может быть однострочный или многострочный.

Однострочный комментарий начинается с двух косых черт и продолжается до конца строки.

Многострочный начинается с `/*` и и продолжается до `*/`

Пробельные символы и комментарии не используются в анализе исходного текста и при описании синтаксиса языка.

#### Примеры

    // Однострочный комментарий
    /* Многострочный
       коментарий */

Строки, строчные литералы
------------------------------------

Все символы представлены в оперативной памяти 2мя байтами - UTF-16

### Формально

    string ::= string1 | string2

    string1 ::= strBegin1 + strInnerChar1*0 + strBegin1
    strBegin1 ::= '"'
    strInnerChar1 ::= strEncUnicodeChar | strEncChar | strNonEncChar1
    strEncChar ::= '\' anyChar 
    strEncUnicodeChar ::= '\u' hexDigitChars hexDigitChars hexDigitChars hexDigitChars
    strNonEncChar1 ::= символ не '"' и не '\'

    string2 ::= strBegin2 + strInnerChar2*0 + strBegin2
    strBegin2 ::= "'"
    strInnerChar2 ::= strEncUnicodeChar | strEncChar | strNonEncChar2
    strEncChar ::= '\' anyChar 
    strEncUnicodeChar ::= '\u' hexDigitChars hexDigitChars hexDigitChars hexDigitChars
    strNonEncChar2 ::= символ не "'" и не '\'

### Описание

- Строка может быть зада двумя формами
  - В одиночных кавычках - `'string'`
  - В двойных кавычках - `"string"`
- Строка может содержать unicode символы `'\u0055\u004e\u0049\u0043\u004f\u0044\u0045'` соответствует `"UNICODE"`
- Строка может содержать символы экранирования `"next\nline"`
  - Символы `\n` - будет заменен на символ перевода строки `\u000D`
  - Символы `\r` - будет заменен на символ возврата каретки `\u000A`
  - Символы `\t` - будет заменен на символ табуляции `\u0009`
  
Числа
------------------------------------

Числа могут быть представлены в несколькими типами данных

- 1 байт,  целое, диапазон значений `[ -128, +127 ]` 
- 2 байта, целое, диапазон значений `[  -32768, +32767 ]`
- 4 байта, целое, диапазон значений `[  -2^31, +2^31-1 ]`
- 8 байта, целое, диапазон значений `[  -2^63, +2^63-1 ]`
- 4 байта, дробное, плавающая запятая, стандарт 32-bit IEEE 754
- 8 байт, дробное, плавающая запятая, стандарт 64-bit IEEE 754
- целое, большой точности - соответствует [BigInteger Java](https://docs.oracle.com/javase/7/docs/api/java/math/BigInteger.html)
- дробное, большой точности - соответствует [BigDecimal Java](https://docs.oracle.com/javase/7/docs/api/java/math/BigDecimal.html)

### Формально

    // Числа
    number ::= decimal 
             | double | float
             | byteNumber | shortNumber | longNumber 
             | bigIntNumber | intNumber

    decimal ::= decimalNumber4
              | decimalNumber5 
              | decimalNumber6
              | decimalNumber7

    float ::= floatNumber4|floatNumber5|floatNumber6|floatNumber7
    double ::= doubleNumber4|doubleNumber5|doubleNumber6|doubleNumber7|doubleNumber1|doubleNumber2|doubleNumber3
    decimalNumber7 ::= digitPoint untypedNum decimalSuf
    decimalNumber6 ::= untypedNum digitPoint decimalSuf
    decimalNumber5 ::= untypedNum digitPoint untypedNum decimalSuf
    decimalNumber4 ::= untypedNum decimalSuf
    floatNumber4 ::= untypedNum floatSuf
    floatNumber5 ::= untypedNum digitPoint untypedNum floatSuf
    floatNumber6 ::= untypedNum digitPoint floatSuf
    floatNumber7 ::= digitPoint untypedNum floatSuf

    digitPoint ::= '.'
    doubleSuf  ::= 'd' | 'D'
    floatSuf   ::= 'f' | 'F'
    decimalSuf ::= 'w' | 'W'
    
    doubleNumber1 ::= untypedNum digitPoint untypedNum
    doubleNumber2 ::= untypedNum digitPoint
    doubleNumber3 ::= digitPoint untypedNum
    doubleNumber4 ::= untypedNum doubleSuf
    doubleNumber5 ::= untypedNum digitPoint untypedNum doubleSuf
    doubleNumber6 ::= untypedNum digitPoint doubleSuf
    doubleNumber7 ::= digitPoint untypedNum doubleSuf

    untypedNum   ::= hexNum | binNum | decNum 
    longNumber   ::= untypedNum ( 'L' | 'l' )
    bigIntNumber ::= untypedNum ( 'N' | 'n' )
    shortNumber  ::= untypedNum ( 'S' | 's' )
    byteNumber   ::= untypedNum ( 'B' | 'b' )
    intNumber    ::= untypedNum
    
    hexNum ::= '0x' hexDigit { hexDigit }
    binNum ::= '0b' binDigit { binDigit }
    decNum ::= digit { digit }

### Примеры

- `123` - Обычное целое число, 4 байта
- `0x0A` - Цело число, 4 байта, задано в 16ом формате
- `0b00101` - Цело число, 4 байта, задано в 2ом формате 
- `12.3` - Дробное, 8 байт
- `.05` - Дробное, 8 байт
- `.15w` - Дробное, BigDecimal
- `23.` - Дробное, 8 байт
- `14N` - Целое, BigInteger
- `15n` - Целое, BigInteger
- `23.45f` - Дробное, 4 байта

Идентификатор
----------------------

Идентификатор используется как в именах переменных, так и именах методов, классов, типов.

Правило описания - очень простое, первый символ - это буква, а дальше может быть буквы, цифры, символы подчеркивания.

### Формально

    identifier ::= Character.isLetter { Character.isLetterOrDigit | '_' }

### Примеры

- Допустимые идентификаторы
  - `a`
  - `abc`
  - `a_bc`
  - `a_123`
  - `a1`
- Не допустимые идентификаторы
  - `1a` - не допустимый идентификатор

Оператор
----------------------

Операторы - это последовательность видимых символов, которые не являются: буквами, цифрами, пробельными символами, кавычками

### Формально

    operator ::= operatorChar { operatorChar }
    operatorChar ::= !Character.isLetterOrDigit & !Character.isWhitespace & !'"' && !"'"

### Примеры

- `+`
- `-`
- `*`
- `/`
- `%`
- `=>`
- `(`
- `)`
- `()`
- `?`
- и любые другие возможные 

Комментарий
----------------------

### Формально

    comment ::= singleLineComment | multilLineComment  
    singleLineComment ::= '//' { любой_символ_кроме_пееревода_строк } [символ_пееревода_строк]
    multilLineComment ::= '/*' { любой_символ } '*/'

### Примеры
  

Формальные правила
------------------------------------

    WS // Пробельные символы

    // 2ые цифры
    binDigitChars ::= '0' | '1'

    // 10ые цифры
    digitChars ::= '0' | '1' | '2' | '3' | '4' |
                   '5' | '6' | '7' | '8' | '9'


    // 16ые цифры
    hexDigitChars ::= '0' | '1' | '2' | '3' | '4' |
                      '5' | '6' | '7' | '8' | '9' |
                      'a' | 'b' | 'c' | 'd' | 'e' | 'f' |
                      'A' | 'B' | 'C' | 'D' | 'E' | 'F' 

    // Строка
    string ::= string1 | string2

    string1 ::= strBegin1 + strInnerChar1*0 + strBegin1
    strBegin1 ::= '"'
    strInnerChar1 ::= strEncUnicodeChar | strEncChar | strNonEncChar1
    strEncChar ::= '\' anyChar 
    strEncUnicodeChar ::= '\u' hexDigitChars hexDigitChars hexDigitChars hexDigitChars
    strNonEncChar1 ::= символ не '"' и не '\'

    string2 ::= strBegin2 + strInnerChar2*0 + strBegin2
    strBegin2 ::= "'"
    strInnerChar2 ::= strEncUnicodeChar | strEncChar | strNonEncChar2
    strEncChar ::= '\' anyChar 
    strEncUnicodeChar ::= '\u' hexDigitChars hexDigitChars hexDigitChars hexDigitChars
    strNonEncChar2 ::= символ не "'" и не '\'

    // Числа
    number ::= decimal 
             | double | float
             | byteNumber | shortNumber | longNumber 
             | bigIntNumber | intNumber

    decimal ::= decimalNumber4
              | decimalNumber5 
              | decimalNumber6
              | decimalNumber7

    float ::= floatNumber4|floatNumber5|floatNumber6|floatNumber7
    double ::= doubleNumber4|doubleNumber5|doubleNumber6|doubleNumber7|doubleNumber1|doubleNumber2|doubleNumber3
    decimalNumber7 ::= digitPoint untypedNum decimalSuf
    decimalNumber6 ::= untypedNum digitPoint decimalSuf
    decimalNumber5 ::= untypedNum digitPoint untypedNum decimalSuf
    decimalNumber4 ::= untypedNum decimalSuf
    floatNumber4 ::= untypedNum floatSuf
    floatNumber5 ::= untypedNum digitPoint untypedNum floatSuf
    floatNumber6 ::= untypedNum digitPoint floatSuf
    floatNumber7 ::= digitPoint untypedNum floatSuf

    digitPoint ::= '.'
    doubleSuf  ::= 'd' | 'D'
    floatSuf   ::= 'f' | 'F'
    decimalSuf ::= 'w' | 'W'
    
    doubleNumber1 ::= untypedNum digitPoint untypedNum
    doubleNumber2 ::= untypedNum digitPoint
    doubleNumber3 ::= digitPoint untypedNum
    doubleNumber4 ::= untypedNum doubleSuf
    doubleNumber5 ::= untypedNum digitPoint untypedNum doubleSuf
    doubleNumber6 ::= untypedNum digitPoint doubleSuf
    doubleNumber7 ::= digitPoint untypedNum doubleSuf

    untypedNum   ::= hexNum | binNum | decNum 
    longNumber   ::= untypedNum ( 'L' | 'l' )
    bigIntNumber ::= untypedNum ( 'N' | 'n' )
    shortNumber  ::= untypedNum ( 'S' | 's' )
    byteNumber   ::= untypedNum ( 'B' | 'b' )
    intNumber    ::= untypedNum
    
    hexNum ::= '0x' hexDigit { hexDigit }
    binNum ::= '0b' binDigit { binDigit }
    decNum ::= digit { digit }

    // Идентификатор
    identifier ::= Character.isLetter { Character.isLetterOrDigit | '_' }

    // Оператор
    operator ::= operatorChar { operatorChar }
    operatorChar ::= !Character.isLetterOrDigit & !Character.isWhitespace & !'"' && !"'"  
    
    // Комментарий 
    comment ::= singleLineComment | multilLineComment  
    singleLineComment ::= '//' { любой_символ_кроме_пееревода_строк } [символ_пееревода_строк]
    multilLineComment ::= '/*' { любой_символ } '*/' 