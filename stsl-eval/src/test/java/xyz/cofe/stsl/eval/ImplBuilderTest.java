package xyz.cofe.stsl.eval;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.conf.TastCompiler;
import xyz.cofe.stsl.eval.sample.ScriptCompute1;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl0;
import xyz.cofe.stsl.eval.sample.ScriptComputeImpl1;
import xyz.cofe.stsl.eval.sample.ScriptCompute_str_impl;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.ToasterError;
import xyz.cofe.stsl.tast.ToasterTracer;
import xyz.cofe.stsl.types.Obj;
import xyz.cofe.stsl.types.WriteableField;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    public void dump_impl_str(){
        var cbegin = CBegin.parseByteCode(ScriptCompute_str_impl.class);
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
    
    public class TastTracer
    implements ToasterTracer    
    {
        private List<Integer> level = new ArrayList<>();

        @Override
        public void begin( AST ast ){
            level.add( indent.level() );
            indent.level( indent.level()+1 );

            out.println( "tast> "+ast );
        }

        @Override
        public void endSucc( AST ast, TAST result ){
            out.println( "tast< ast="+ast+" result.type="+result.supplierType()+" result.type.class="+result.supplierType().getClass() );
            restoreLevel();
        }

        @Override
        public void endFail( AST ast, ToasterError err ){
            out.println( "tast< ast="+ast+" error="+err );
            restoreLevel();
        }

        private void restoreLevel(){
            if( !level.isEmpty() ){
                var lvl = level.remove(level.size() - 1);
                indent.level(lvl);
            }
        }
    }

    @Test
    public void interop_eval(){
        var intStslType = xyz.cofe.stsl.tast.JvmType.INT();

        var predefVal = Map.of(
            int.class,
            intStslType
        );

        var scriptClass = ScriptCompute1.class;
        var exportFields = ExportField.of(scriptClass,
            jvmType -> {
                var stslType = predefVal.get(jvmType);
                return stslType!=null ? Optional.of(stslType) : Optional.empty();
            },
            defaultValueForField -> {
                if( defaultValueForField.jvmType==int.class )return 0;
                return null;
            }
        );

        String script = "{ sum: a + b }";

        var tastCompiler = new TastCompiler();

        var impBld = new ImplBuilder();

        exportFields.forEach( exportField -> {
            System.out.println("export into var scope" +
                " name="+exportField.name+
                " stslType="+exportField.stslType+
                " value="+exportField.defauleValue);
            tastCompiler.varScope().put(exportField.name, exportField.stslType, exportField.defauleValue);
        });

        var exportedFields = new HashMap<String,ExportField>();
        exportFields.forEach( exportField -> {
            exportedFields.put(exportField.name, exportField);
        });

        tastCompiler.withToasterTracer(new TastTracer());
        var tast = tastCompiler.compile(script);
        var genClassName = "some.myClass";

        var cb = impBld.build(scriptClass, genClassName);
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
                    var ef = exportedFields.get(name);
                    if( ef!=null ){
                        System.out.println("export name="+name+" value="+value+" stslType="+ef.stslType+" jvmType="+ef.jvmType);
                        tastCompiler.varScope().put(name, ef.stslType, value);
                    }
                }
            };
            var inst = (ScriptCompute1)cls.getConstructor(TastInterop.class).newInstance(interop);

            out.println("====================");

            inst.a = 123;
            inst.b = 234;
            var res = inst.sum();

            out.println("sum() for a=123 b=234 is "+res);

            inst.a = 12;
            inst.b = 23;
            out.println("sum() for a=12 b=23 is "+inst.sum());

            out.flush();
        } catch( ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InstantiationException | InvocationTargetException e ){
            e.printStackTrace();
        }
    }
}
