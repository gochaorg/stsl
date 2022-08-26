package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.conf.sample.SampleArr;
import xyz.cofe.stsl.conf.sample.SampleCompaund1;
import xyz.cofe.stsl.conf.sample.SampleConfig1;
import xyz.cofe.stsl.eval.CommonForTest;

import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ConfigInstanceTest extends CommonForTest {
    @Test
    public void anonSimple(){
        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: 1 }";
        System.out.println(ci.read(script).name());
    }

    @Test
    public void anonCompute(){
        var confInst = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\" + \" world\", value: 1 }";

        System.out.println(script);

        var conf = confInst.read(script);
        System.out.println(conf.name());
        System.out.println(conf.value());

        assert( conf.name().equals("hello world") );
        assert( conf.value() == 1 );
    }

    @Test
    public void compaund01() {
        var script = "{\n" +
            " first : {\n" +
            "  name: \"hello\", value: 1\n" +
            " },\n" +
            " second : {\n" +
            "  name: \"world\", value: 2\n" +
            " }\n" +
            "}";

        System.out.println("script:");
        System.out.println(script);

        var confInst = ConfigInstance.create(SampleCompaund1.class);
        var conf = confInst.read(script);

        List.of( conf.first(), conf.second() ).forEach( c -> {
            System.out.println("name="+c.name()+" value="+c.value());
        });
    }

    @Test
    public void externalVars01(){
        var confInst = ConfigInstance.create(SampleConfig1.class);
        confInst.varScope().put("a", xyz.cofe.stsl.tast.JvmType.INT(), 10 );
        confInst.varScope().put("b", xyz.cofe.stsl.tast.JvmType.INT(), 25 );
        confInst.varScope().put("c", xyz.cofe.stsl.tast.JvmType.STRING(), "Hello " );
        confInst.varScope().put("d", xyz.cofe.stsl.tast.JvmType.STRING(), "World!" );

        var script =
            "{\n" +
            "  name: c+d,\n" +
            "  value: a+b\n" +
            "}";

        out.println("script:");
        indent(()->{
            out.println(script);
        });

        var conf = confInst.read(script);
        out.println("inst:");
        indent(()->{
            out.println("name = "+conf.name());
            out.println("value = "+conf.value());
        });
        out.flush();
    }

    @Test
    public void array01(){
        var cls = SampleArr.class;
        try{
            var listMeth = cls.getMethod("list");
            var listMethRet = listMeth.getGenericReturnType();
            System.out.println(listMethRet);

            var listMethRetPType = (ParameterizedType)listMethRet;
            assert listMethRetPType.getRawType() == List.class;
            assert listMethRetPType.getActualTypeArguments().length == 1;
            assert listMethRetPType.getActualTypeArguments()[0] == SampleConfig1.class;

            var collMeth = cls.getMethod("coll");
            var collMethRetPType = (ParameterizedType)collMeth.getGenericReturnType();
            assert collMethRetPType.getRawType() == Collection.class;
            assert collMethRetPType.getActualTypeArguments().length == 1;
            assert collMethRetPType.getActualTypeArguments()[0] == SampleConfig1.class;

            var iterMeth = cls.getMethod("iter");
            var iterMethRetPType = (ParameterizedType)iterMeth.getGenericReturnType();
            assert iterMethRetPType.getRawType() == Iterable.class;
            assert iterMethRetPType.getActualTypeArguments().length == 1;
            assert iterMethRetPType.getActualTypeArguments()[0] == SampleConfig1.class;

            var optMeth = cls.getMethod("opt");
            var optMethRetPType = (ParameterizedType)optMeth.getGenericReturnType();
            System.out.println(optMethRetPType);
            assert optMethRetPType.getRawType() == Optional.class;
            assert optMethRetPType.getActualTypeArguments().length == 1;
            assert optMethRetPType.getActualTypeArguments()[0] == SampleConfig1.class;

            var listOptMeth = cls.getMethod("listOpt");
            var listOptMethRetPType = (ParameterizedType)listOptMeth.getGenericReturnType();
            System.out.println(listOptMethRetPType);
            assert listOptMethRetPType.getRawType() == List.class;
            assert listOptMethRetPType.getActualTypeArguments().length == 1;
            var tparam = (ParameterizedType)listOptMethRetPType.getActualTypeArguments()[0];
            assert tparam.getRawType() == Optional.class;
            assert tparam.getActualTypeArguments().length == 1;
            assert tparam.getActualTypeArguments()[0] == SampleConfig1.class;
        } catch( NoSuchMethodException e ){
            e.printStackTrace();
        }
    }
}
