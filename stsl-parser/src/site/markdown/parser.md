Парсер
=================



Простые выражения
---------------------

    123 + 234 * 345

    -| LiteralAST IntNumberTok 123
    -| BinaryAST *
    -|-| LiteralAST IntNumberTok 234
    -|-| LiteralAST IntNumberTok 345

    123 / 234 - 345

    BinaryAST -
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 123
    -|-| LiteralAST IntNumberTok 234
    -| LiteralAST IntNumberTok 345

    123 - 234 / 345

    BinaryAST -
    -| LiteralAST IntNumberTok 123
    -| BinaryAST /
    -|-| LiteralAST IntNumberTok 234
    -|-| LiteralAST IntNumberTok 345

    ( 123 - 234 ) * 2

    BinaryAST *
    -| DelegateAST
    -|-| BinaryAST -
    -|-|-| LiteralAST IntNumberTok 123
    -|-|-| LiteralAST IntNumberTok 234
    -| LiteralAST IntNumberTok 2

    a - b / c

    BinaryAST -
    -| IdentifierAST IdentifierTok a
    -| BinaryAST /
    -|-| IdentifierAST IdentifierTok b
    -|-| IdentifierAST IdentifierTok c

    a < b

    BinaryAST <
    -| IdentifierAST IdentifierTok a
    -| IdentifierAST IdentifierTok b

    a ? b : c

    TernaryAST ? :
    -| IdentifierAST IdentifierTok a
    -| IdentifierAST IdentifierTok b
    -| IdentifierAST IdentifierTok c

    x.a + x.b.c
    
    BinaryAST +
    -| PropertyAST a
    -|-| IdentifierAST IdentifierTok x
    -| PropertyAST c
    -|-| PropertyAST b
    -|-|-| IdentifierAST IdentifierTok x

    a( 10, 12 ) + b.a( 1, 2, 3 )

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

    obj.m()

    CallAST
    -| PropertyAST m
    -|-| IdentifierAST IdentifierTok obj

    a:int , b:int => a+b

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

    a:int , b:int , r :: int => a+b

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

    () => a+b

    LambdaAST
    -| BinaryAST +
    -|-| IdentifierAST IdentifierTok a
    -|-| IdentifierAST IdentifierTok b

    { k1: 1, k2: 2 }

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

    { }

    PojoAST

    {}

    PojoAST


Формальный синтаксис
----------------------

    expression ::= ifOp

    literal ::= ...

    identifier ::= ...

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

    ternary( condition:AstGR, question:OpLiteral, success:AstGR, elseOp:OpLiteral, failure:AstGR ): AstGR

    ifOp ::= ternary( bool, operator("?"), bool, operator(":"), bool )