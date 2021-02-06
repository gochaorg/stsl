Парсер
=================

    expression ::= ifOp

    ifOp ::= bool [ '?' bool ':' bool ]
    
    bool ::= binary( cmp, operator('&', '|'), cmp )
    
    cmp ::= binary( add, operator("==","!=","<",">","<=",">="), add )
    
    add ::= binary( mul, operator("+","-"), mul )
    
    mul ::= binary( postFix, operator("*","/","%"), postFix)
    
    postFix ::= atom { propertyFollow | call }
    
    propertyFollow ::= fieldAccessOp [ identifier [ call ] ]

    fieldAccessOp ::= '.'

    call ::= callStartEnd
           | callStart [ expression { argDelim, expression } ] callEnd

    callStartEnd ::= '()'
    callStart    ::= '('
    callEnd      ::= ')'

    atom ::= lambdaWithoutParams
           | lambdaWithParams
           | parenthes
           | unary
           | literal
           | identifier

    lambdaWithParams ::= 
        lambdaParam
        [
          { lambdaArgDelim lambdaParam }
        | lambdaRecusionParam
        ]
        lambdaArrow
        expression 

    lambdaParam ::= identifier ':' typeName

    lambdaRecusionParam ::= identifier '::' typeName

    lambdaArrow ::= '==>'
    
    lambdaArgDelim ::= ','

    lambdaWithoutParams ::= lambdaEmptyParamsN expression

    lambdaEmptyParamsN ::= lambdaEmptyParams1 | lambdaEmptyParams2 | lambdaEmptyParams3 | lambdaEmptyParams4

    lambdaEmptyParams1 ::= '(' ')' '=>'
    lambdaEmptyParams2 ::= '()' '=>'
    lambdaEmptyParams3 ::= '()=>'
    lambdaEmptyParams4 ::= '( ' ')=>'

    typeName ::= identifier

    unary ::= ( '-' | '!' ) expression

    parenthes ::= '(' expression ')'

    binary ::= fn( $init $operator $follow ){
        $init { $operator $follow }
    }

    literal ::= это лексемы из lexer
        string | number