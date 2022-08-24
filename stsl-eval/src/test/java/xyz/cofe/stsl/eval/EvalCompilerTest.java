package xyz.cofe.stsl.eval;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.conf.CommonForTest;
import xyz.cofe.stsl.eval.sample.ScriptCompute1;
import xyz.cofe.stst.eval.EvalCompiler;
import xyz.cofe.stst.eval.eval;
import xyz.cofe.stst.eval.export;

public class EvalCompilerTest extends CommonForTest {
    public static abstract class Sample1 {
        @export public int a;
        @export public int b;
        @eval public abstract int sum();
    }

    @Test
    public void test01(){
        var sc1 = new EvalCompiler().compile(Sample1.class, "{ sum: a + b }");
        sc1.a = 1;
        sc1.b = 2;
        var res1 = sc1.sum();
        assert res1==3;

        sc1.a = 3;
        sc1.b = 2;
        var res2 = sc1.sum();
        assert res2==5;
    }

    public static abstract class Sample2 {
        @export public String a;
        @export public String b;
        @eval public abstract String sum();
    }

    @Test
    public void test02(){
        var sc1 = new EvalCompiler()
            //.traceByteCode(this::dump)
            .compile(Sample2.class, "{ sum: a + b }");

        sc1.a = "Hello";
        sc1.b = "World";
        var res1 = sc1.sum();
        System.out.println(res1);
    }
}
