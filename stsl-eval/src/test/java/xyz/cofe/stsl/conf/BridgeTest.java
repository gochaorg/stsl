package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.conf.reflect.JvmType;
import xyz.cofe.stsl.conf.reflect.ToStslType;
import xyz.cofe.stsl.conf.sample.SampleConfig1;
import xyz.cofe.stsl.types.AssignableTracer;
import xyz.cofe.stsl.types.AssignableTracer$;
import xyz.cofe.stsl.types.TAnon;

public class BridgeTest extends CommonForTest {
    @Test
    public void anonSimple_01(){
        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: 1 }";

        var tast = ci.compile(script);

        var scriptSType = tast.supplierType();
        assert scriptSType instanceof TAnon;

        var jvmTypeOpt = JvmType.of(SampleConfig1.class);
        assert jvmTypeOpt.isPresent();
        assert jvmTypeOpt.get() instanceof ToStslType;

        var configSType = ((ToStslType) jvmTypeOpt.get()).toStslType();
        assert configSType instanceof TAnon;

        assert configSType.assignable(scriptSType, AssignableTracer.defaultTracer());
    }

    @Test
    public void anonSimple_02(){
        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: \"abc\" }";

        var tast = ci.compile(script);

        var scriptSType = tast.supplierType();
        assert scriptSType instanceof TAnon;

        var jvmTypeOpt = JvmType.of(SampleConfig1.class);
        assert jvmTypeOpt.isPresent();
        assert jvmTypeOpt.get() instanceof ToStslType;

        var configSType = ((ToStslType) jvmTypeOpt.get()).toStslType();
        assert configSType instanceof TAnon;

        out.println(configSType.assignable(scriptSType,AssignableTracer.defaultTracer()));
        out.flush();

        assert !configSType.assignable(scriptSType,AssignableTracer.defaultTracer());
    }

    @Test
    public void anonSimple_03(){
        var tracer = AssignableTracer$.MODULE$.apply(out);

        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: 1, abc: 123 }";

        var tast = ci.compile(script);

        var scriptSType = tast.supplierType();
        assert scriptSType instanceof TAnon;

        var jvmTypeOpt = JvmType.of(SampleConfig1.class);
        assert jvmTypeOpt.isPresent();
        assert jvmTypeOpt.get() instanceof ToStslType;

        var configSType = ((ToStslType) jvmTypeOpt.get()).toStslType();
        assert configSType instanceof TAnon;

        out.println(configSType.assignable(scriptSType,tracer));
        out.flush();

        assert configSType.assignable(scriptSType,tracer);
        out.flush();
    }

    @Test
    public void anonSimple_04(){
        var tracer = AssignableTracer$.MODULE$.apply(out);

        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: \"1\", abc: 123 }";

        var tast = ci.compile(script);

        var scriptSType = tast.supplierType();
        assert scriptSType instanceof TAnon;

        var jvmTypeOpt = JvmType.of(SampleConfig1.class);
        assert jvmTypeOpt.isPresent();
        assert jvmTypeOpt.get() instanceof ToStslType;

        var configSType = ((ToStslType) jvmTypeOpt.get()).toStslType();
        assert configSType instanceof TAnon;

        //out.println(configSType.assignable(scriptSType,tracer));
        //out.flush();

        assert !configSType.assignable(scriptSType,tracer);
        out.flush();
    }
}
