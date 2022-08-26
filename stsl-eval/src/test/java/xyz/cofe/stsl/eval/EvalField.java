package xyz.cofe.stsl.eval;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class EvalField {
    public final String name;
    public final java.lang.reflect.Type type;

    public EvalField( String name, Type type ){
        this.name = name;
        this.type = type;
    }

    public static List<EvalField> of( java.lang.reflect.Type type ){
        if( type==null )throw new IllegalArgumentException( "type==null" );

        var list = new ArrayList<EvalField>();
        return list;
    }
}
