package xyz.cofe.stsl.eval;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.stsl.conf.CommonForTest;
import xyz.cofe.stsl.eval.sample.ScriptCompute1;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl0;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl1;
import xyz.cofe.stst.eval.EvalClassloader;
import xyz.cofe.stst.eval.ImplBuilder;

import java.lang.reflect.InvocationTargetException;

public class ImplBuilderTest extends CommonForTest {
    @Test
    public void dump01(){
        var cbegin = CBegin.parseByteCode(ScriptCompute1.class);
        dump(cbegin);
    }

    @Test
    public void dump_impl_0(){
        var cbegin = CBegin.parseByteCode(ScriptComputeImpl0.class);
        dump(cbegin);
    }

    @Test
    public void dump_impl_1(){
        var cbegin = CBegin.parseByteCode(ScriptComputeImpl1.class);
        dump(cbegin);
    }

    @Test
    public void test01(){
        var genClassName = "some.my_class";

        var impBld = new ImplBuilder();
        var cb = impBld.build(ScriptCompute1.class, genClassName);
        dump(cb);
        out.flush();

        var evalCl = new EvalClassloader(this.getClass().getClassLoader());
        evalCl.put(genClassName, cb.toByteCode());

        try{
            Class<?> cls = Class.forName(genClassName,true,evalCl);
            var inst = cls.getConstructor().newInstance();

            out.println("====================");
            var sumMeth = cls.getMethod("sum");
            var res = sumMeth.invoke(inst);

            out.println("sum() "+res);
            out.flush();
        } catch( ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e ){
            e.printStackTrace();
        }
    }
}
