package xyz.cofe.stsl.eval.map;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.eval.CommonForTest;
import xyz.cofe.stsl.conf.TastCompiler;

public class MapTest extends CommonForTest {
    @Test
    public void test_compaund(){
        var tastCompiler = new TastCompiler();
        var source =
            "{ \n" +
                "  first: {\n" +
                "    sum: a + b,\n" +
                "    mul: a * b\n" +
                "  }, \n" +
                "  second: {\n" +
                "    sum: b + c,\n" +
                "    mul: b * c\n" +
                "  } \n" +
                "}";
        out.println(source);

        tastCompiler.varScope().put("a",xyz.cofe.stsl.tast.JvmType.INT(),1);
        tastCompiler.varScope().put("b",xyz.cofe.stsl.tast.JvmType.INT(),2);
        tastCompiler.varScope().put("c",xyz.cofe.stsl.tast.JvmType.INT(),3);

        var tast = tastCompiler.compile(source);
        out.println(tast);

        var rootType = tast.supplierType();
        out.println("type:");
        describe(rootType);

        var root = tast.supplier().get();
        out.println(root.getClass());
        out.println(root);
        out.flush();
    }
}
