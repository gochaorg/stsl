package xyz.cofe.stsl.types.j;

import java.util.List;

public class SomeTest {
    public <A extends Long> void test01(){
        Long l = 10L;
        //A a = l;
        List<? extends Long> l1 = null;
        List<? super Long> l2 = null;
        //l1.add(l2.get(0));
        l2.add(l1.get(0));
        l = l1.get(0);

    }
}
