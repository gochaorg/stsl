package xyz.cofe.stsl.types.j;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

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

//    public void test02(){
//        Consumer<? extends Number> c1 = null;
//        Consumer<? extends Number> c2 = c1;
//        Consumer<? extends Integer> c3 = c1;
//        Consumer<? extends Object> c4 = c1;
//    }

    public void test03(){
//        Consumer<? super Number> c1 = null;
//        Consumer<? super Number> c2 = c1;
//        Consumer<? super Integer> c3 = c1;
//        Consumer<? super Object> c4 = c1;
    }

    public void test04(){
        Consumer<? extends Object> covObj = null;
        Consumer<? extends Number> covNum = null;
        Consumer<? extends Integer> covInt = null;

        Consumer<? super Object> ctrObj = null;
        Consumer<? super Number> ctrNum = null;
        Consumer<? super Integer> ctrInt = null;

        ctrInt = ctrInt;
        ctrInt = ctrNum;
        ctrInt = ctrObj;

        ctrNum = ctrObj;
        // ctrNum = covNum; // err
        ctrNum = ctrNum;
        // ctrNum = ctrInt; // err

        ctrObj = ctrObj;
        // ctrObj = ctrNum; // err
        // ctrObj = ctrInt; // err

//        ctrNum = covInt; // err
//        ctrInt = covInt; // err
//        ctrObj = covInt; // err
//
//        ctrNum = covNum; // err
//        ctrInt = covNum; // err
//        ctrObj = covNum; // err

//        covNum = ctrInt; // err
//        covInt = ctrInt; // err
//        covObj = ctrInt; // err
//
//        covNum = ctrNum; // err
//        covInt = ctrNum; // err
//        covObj = ctrNum; // err
//
//        covNum = ctrObj; // err
//        covInt = ctrObj; // err
//        covObj = ctrObj; // err

        Supplier<? extends Object> s_coObj = null;
        Supplier<? extends Number> s_coNum = null;
        Supplier<? extends Integer> s_coInt = null;
    }
}