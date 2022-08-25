package xyz.cofe.stsl.eval;

import org.junit.jupiter.api.Test;
import xyz.cofe.stst.eval.EvalCompiler;
import xyz.cofe.stst.eval.TastCompiler;
import xyz.cofe.stst.eval.eval;
import xyz.cofe.stst.eval.export;

public class EvalCompilerTest extends CommonForTest {
    public static abstract class Sample_int {
        @export public int a;
        @export public int b;
        @eval public abstract int sum();
    }

    @Test
    public void test_int(){
        var sc1 = new EvalCompiler().compile(Sample_int.class, "{ sum: a + b }");
        sc1.a = 1;
        sc1.b = 2;
        var res1 = sc1.sum();
        assert res1==3;

        sc1.a = 3;
        sc1.b = 2;
        var res2 = sc1.sum();
        assert res2==5;
    }

    public static abstract class Sample_String {
        @export public String a;
        @export public String b;
        @eval public abstract String sum();
    }

    @Test
    public void test_String(){
        var sc1 = new EvalCompiler()
            //.traceByteCode(this::dump)
            .compile(Sample_String.class, "{ sum: a + b }");

        sc1.a = "Hello";
        sc1.b = "World";
        var res1 = sc1.sum();
        System.out.println(res1);
    }

    public static abstract class Sample_inner {
        @eval public abstract int sum();
        @eval public abstract int mul();
    }

    public static abstract class Sample_Compaund {
        @export public int a;
        @export public int b;
        @export public int c;
        @eval public abstract Sample_inner first();
        @eval public abstract Sample_inner second();
    }

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
