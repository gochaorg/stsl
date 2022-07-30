package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;

import java.util.List;

public class ConfigInstanceTest {
    @Test
    public void test01(){
        var ci = ConfigInstance.create(SampleConfig1.class);
        var script =
            "{ name: \"hello\", value: 1 }";
        System.out.println(ci.read(script).name());
    }

    @Test
    public void test02(){
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
}
