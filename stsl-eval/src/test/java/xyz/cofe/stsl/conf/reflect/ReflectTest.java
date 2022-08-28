package xyz.cofe.stsl.conf.reflect;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.conf.CommonForTest;
import xyz.cofe.stsl.conf.sample.SampleConfig1;
import xyz.cofe.stsl.conf.sample.SampleRecur;

public class ReflectTest extends CommonForTest {
    @Test
    public void primitive_int0(){
        var jt = JvmType.of(int.class);
        assert jt.isPresent();
        assert jt.get() instanceof JvmPrimitive;
        assert jt.get() == JvmPrimitive.intPrimitive;
    }

    @Test
    public void primitive_int1(){
        var jt = JvmType.of(Integer.class);
        assert jt.isPresent();
        assert jt.get() instanceof JvmPrimitive;
        assert jt.get() == JvmPrimitive.intWrapper;

        var t = ((JvmPrimitive) jt.get()).toStslType();
        assert t == xyz.cofe.stsl.tast.JvmType.INT();
    }

    @Test
    public void compaund_01(){
        var jt = JvmObjContainer.of(SampleConfig1.class);
        assert jt.isPresent();
        assert jt.get().members.stream().anyMatch(m -> m.name().equals("name"));
        assert jt.get().members.stream().anyMatch(m -> m.name().equals("value"));
        System.out.println(jt.get());
    }

    @Test
    public void compaund_02(){
        var jt = JvmType.of(SampleConfig1.class);
        assert jt.isPresent();
        assert jt.get() instanceof JvmObjContainer;

        var c = (JvmObjContainer) jt.get();
        assert c.members.stream().anyMatch(m -> m.name().equals("name"));
        assert c.members.stream().anyMatch(m -> m.name().equals("value"));
        System.out.println(c);
    }

    @Test
    public void recursive_01(){
        var jt = JvmType.of(SampleRecur.class);
        assert jt.isPresent();
        System.out.println(jt.get());
    }
}
