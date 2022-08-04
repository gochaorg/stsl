package xyz.cofe.stsl.eval;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.stsl.conf.CommonForTest;
import xyz.cofe.stsl.eval.sample.ScriptCompute1;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl0;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl1;
import xyz.cofe.stsl.types.Obj;
import xyz.cofe.stsl.types.WriteableField;
import xyz.cofe.stst.eval.EvalClassloader;
import xyz.cofe.stst.eval.ImplBuilder;
import xyz.cofe.stst.eval.TastCompiler;
import xyz.cofe.stst.eval.TastInterop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

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
    public void imp1_bc(){
        var cbegin = CBegin.parseByteCode(ScriptComputeImpl1.class);
        dump(cbegin);

        var evalCl = new EvalClassloader(this.getClass().getClassLoader());
        evalCl.put(ScriptComputeImpl1.class.getName(), cbegin.toByteCode());

        try{
            Class<?> cls = Class.forName(
                ScriptComputeImpl1.class.getName()
                ,true,evalCl);
            TastInterop interop = new TastInterop() {
                @Override
                public Object computeField( String name ){
                    System.out.println("TastInterop computeField "+name);
                    return 135;
                }
            };
            var inst = cls.getConstructor(TastInterop.class).newInstance(interop);

            out.println("====================");
            var sumMeth = cls.getMethod("sum");
            var res = sumMeth.invoke(inst);

            out.println("sum() "+res);
            out.flush();
        } catch( ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e ){
            e.printStackTrace();
        }
    }

    @Test
    public void interop_compile(){
        var genClassName = "some.myClass";

        var impBld = new ImplBuilder();
        var cb = impBld.build(ScriptCompute1.class, genClassName);
        dump(cb);
        out.flush();

        var evalCl = new EvalClassloader(this.getClass().getClassLoader());
        evalCl.put(genClassName, cb.toByteCode());

        try{
            Class<?> cls = Class.forName(
                genClassName
                ,true,evalCl);
            TastInterop interop = new TastInterop() {
                @Override
                public Object computeField( String name ){
                    System.out.println("TastInterop computeField "+name);
                    return 135;
                }
            };
            var inst = cls.getConstructor(TastInterop.class).newInstance(interop);

            out.println("====================");
            var sumMeth = cls.getMethod("sum");
            var res = sumMeth.invoke(inst);

            out.println("sum() "+res);
            out.flush();
        } catch( ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e ){
            e.printStackTrace();
        }
    }

    @Test
    public void interop_eval(){
        String script = "{ sum: 1+2 }";
        var tast = new TastCompiler().compile(script);

        var genClassName = "some.myClass";

        var impBld = new ImplBuilder();
        var cb = impBld.build(ScriptCompute1.class, genClassName);
        dump(cb);
        out.flush();

        var evalCl = new EvalClassloader(this.getClass().getClassLoader());
        evalCl.put(genClassName, cb.toByteCode());

        try{
            Class<?> cls = Class.forName(
                genClassName
                ,true,evalCl);
            TastInterop interop = new TastInterop() {
                @Override
                public Object computeField( String name ){
                    System.out.println("TastInterop computeField "+name);
                    //////////////
                    if( tast.supplierType() instanceof Obj){
                        var sciptInst = tast.supplier().get();
                        var obj = ((Obj)tast.supplierType());
                        var fldOpt = obj.publicFields().find( fld -> fld.name().equals(name) );
                        if( fldOpt.isDefined() ){
                            var fld = fldOpt.get();
                            if( fld instanceof WriteableField ){
                                var fldValue = ((WriteableField) fld).reading().apply(sciptInst);
                                return fldValue;
                            }
                        }
                    }
                    //////////////
                    return 135;
                }

                @Override
                public void setVariable( String name, Object value, Type valueJvmType ){
                    System.out.println("setVariable "+name+" = "+value+" : "+valueJvmType);
                }
            };
            var inst = (ScriptCompute1)cls.getConstructor(TastInterop.class).newInstance(interop);

            out.println("====================");
            //var sumMeth = cls.getMethod("sum");
            //var res = sumMeth.invoke(inst);
            inst.a = 123;
            inst.b = 234;
            var res = inst.sum();

            out.println("sum() "+res);
            out.flush();
        } catch( ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e ){
            e.printStackTrace();
        }
    }
}
