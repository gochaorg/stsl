package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;
import xyz.cofe.stst.eval.TastCompiler;

import java.util.List;
import java.util.Map;

public class ArrayTest {
    @SuppressWarnings("unchecked")
    @Test
    public void simpleAnon(){
        var compile = new TastCompiler();
        var tast = compile.compile("{ a: 1, b: \"abc\", c: true }");
        var value = tast.supplier().get();
        System.out.println(value);

        assert( value instanceof Map );
        var map = (Map<Object,Object>)value;

        assert( map.containsKey("a") );
        assert( map.get("a") instanceof Number );
        assert( ((Number)map.get("a")).intValue()==1 );

        assert( map.containsKey("b") );
        assert( map.get("b") instanceof String );
        assert( map.get("b").equals("abc") );

        assert( map.containsKey("c") );
        assert( map.get("c") instanceof Boolean );
        assert( map.get("c").equals(true) );
    }

    @Test
    public void numberArray(){
        var compile = new TastCompiler();
        var tast = compile.compile("[ 1, 2, 3 ]");
        var value = tast.supplier().get();
        System.out.println(value);

        assert( value instanceof List );
        //noinspection unchecked
        var lst = (List<Object>)value;

        assert( lst.size()==3 );
        assert( lst.get(0).equals(1) );
        assert( lst.get(1).equals(2) );
        assert( lst.get(2).equals(3) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void anonArray(){
        var compile = new TastCompiler();
        var tast = compile.compile("[ {a:1}, {a:2}, {a:3} ]");
        var value = tast.supplier().get();
        System.out.println(value);

        assert( value instanceof List );
        var lst = (List<Object>)value;

        assert( lst.size()==3 );

        assert( lst.get(0) instanceof Map );
        assert( ((Map<Object,Object>)lst.get(0)).containsKey("a") );
        assert( ((Map<Object,Object>)lst.get(0)).get("a").equals(1) );

        assert( lst.get(1) instanceof Map );
        assert( ((Map<Object,Object>)lst.get(1)).containsKey("a") );
        assert( ((Map<Object,Object>)lst.get(1)).get("a").equals(2) );

        assert( lst.get(2) instanceof Map );
        assert( ((Map<Object,Object>)lst.get(2)).containsKey("a") );
        assert( ((Map<Object,Object>)lst.get(2)).get("a").equals(3) );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void anonAndNumArray(){
        var compile = new TastCompiler();
        var tast = compile.compile("[ {a:1}, {a:2}, 1 ]");
        var value = tast.supplier().get();
        System.out.println(value);

        assert( value instanceof List );
        var lst = (List<Object>)value;

        assert( lst.size()==3 );

        assert( lst.get(0) instanceof Map );
        assert( ((Map<Object,Object>)lst.get(0)).containsKey("a") );
        assert( ((Map<Object,Object>)lst.get(0)).get("a").equals(1) );

        assert( lst.get(1) instanceof Map );
        assert( ((Map<Object,Object>)lst.get(1)).containsKey("a") );
        assert( ((Map<Object,Object>)lst.get(1)).get("a").equals(2) );

        assert( lst.get(2) instanceof Integer );
        assert( lst.get(2).equals(1) );
    }
}
