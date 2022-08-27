package xyz.cofe.stsl.eval;

import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.stsl.conf.TastCompiler;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.VarScope;
import xyz.cofe.stsl.types.Obj;
import xyz.cofe.stsl.types.WriteableField;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class EvalCompiler {
    private Map<Type, xyz.cofe.stsl.types.Type> predefStslTypes;
    {
        Map<Type, xyz.cofe.stsl.types.Type> m = new HashMap<>();

        m.put(byte.class, xyz.cofe.stsl.tast.JvmType.BYTE());
        m.put(Byte.class, xyz.cofe.stsl.tast.JvmType.BYTE());

        m.put(short.class, xyz.cofe.stsl.tast.JvmType.SHORT());
        m.put(Short.class, xyz.cofe.stsl.tast.JvmType.SHORT());

        m.put(int.class, xyz.cofe.stsl.tast.JvmType.INT());
        m.put(Integer.class, xyz.cofe.stsl.tast.JvmType.INT());

        m.put(long.class, xyz.cofe.stsl.tast.JvmType.LONG());
        m.put(Long.class, xyz.cofe.stsl.tast.JvmType.LONG());

        m.put(float.class, xyz.cofe.stsl.tast.JvmType.FLOAT());
        m.put(Float.class, xyz.cofe.stsl.tast.JvmType.FLOAT());

        m.put(double.class, xyz.cofe.stsl.tast.JvmType.DOUBLE());
        m.put(Double.class, xyz.cofe.stsl.tast.JvmType.DOUBLE());

        m.put(boolean.class, xyz.cofe.stsl.tast.JvmType.BOOLEAN());
        m.put(Boolean.class, xyz.cofe.stsl.tast.JvmType.BOOLEAN());

        m.put(char.class, xyz.cofe.stsl.tast.JvmType.CHAR());
        m.put(Character.class, xyz.cofe.stsl.tast.JvmType.CHAR());

        m.put(String.class, xyz.cofe.stsl.tast.JvmType.STRING());

        m.put(Number.class, xyz.cofe.stsl.tast.JvmType.NUMBER());

        predefStslTypes = m;
    }

    private Function<Type, Optional<xyz.cofe.stsl.types.Type>> resolveType =
        jvmType -> {
            var stslType = predefStslTypes.get(jvmType);
            return stslType!=null ? Optional.of(stslType) : Optional.empty();
        };

    private Function<ExportField.DefaultValueForField,Object> defaultValue =
        defaultValueForField -> {
            if( defaultValueForField.jvmType==int.class )return 0;
            return null;
        };

    private Function<Class<?>,String> targetName = srcClass -> "_evalCompiler_."+srcClass.getName();

    private Supplier<TastCompiler> tastCompiler = TastCompiler::new;

    private Supplier<ImplBuilder> implBuilder = ImplBuilder::new;

    private Function<Class<?>,EvalClassloader> evalClassloader =
        cls -> new EvalClassloader(cls.getClassLoader());

    private Function<CBegin<?,?,?>, CBegin<?,?,?>> traceByteCode = a -> a;

    public EvalCompiler traceByteCode( Consumer<CBegin<?,?,?>> tracer ){
        if( tracer==null )throw new IllegalArgumentException( "tracer==null" );
        traceByteCode = a -> {
            tracer.accept(a);
            return a;
        };
        return this;
    }

    public <T> T compile( Class<T> cls, String script ){
        if( cls==null )throw new IllegalArgumentException( "cls==null" );
        if( script==null )throw new IllegalArgumentException( "script==null" );

        var exportFields = ExportField.of(cls, resolveType, defaultValue);
        var tastCompiler0 = tastCompiler.get();

        exportFields.forEach( exportField -> {
            tastCompiler0.varScope().put(exportField.name, exportField.stslType, exportField.defauleValue);
        });

        var exportedFields = new HashMap<String,ExportField>();
        exportFields.forEach( exportField -> {
            exportedFields.put(exportField.name, exportField);
        });

        var tast = tastCompiler0.compile(script);

        var implBuilder0 = implBuilder.get();
        var genClassName = targetName.apply(cls);

        var cb =
            traceByteCode.apply(implBuilder0.build(cls,genClassName));

        var evalCl = evalClassloader.apply(cls);
        evalCl.put(genClassName, cb.toByteCode());

        try{
            Class<?> implCls = Class.forName(genClassName,true,evalCl);
            TastInterop interop = createInterop(tastCompiler0.varScope(), exportedFields, tast);

            //noinspection unchecked,UnnecessaryLocalVariable
            var inst = (T)implCls.getConstructor(TastInterop.class).newInstance(interop);
            return inst;
        } catch(
            ClassNotFoundException |
            InvocationTargetException |
            InstantiationException |
            IllegalAccessException |
            NoSuchMethodException
                e
        ){
            throw new RuntimeException(e);
        }
    }

    private TastInterop createInterop( VarScope varScope, HashMap<String, ExportField> exportedFields, TAST tast ){
        return new TastInterop() {
            @Override
            public Object computeField( String name ){
                if( tast.supplierType() instanceof Obj ){
                    var sciptInst = tast.supplier().get();
                    var obj = ((Obj) tast.supplierType());
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
                // todo here exception
                return 135;
            }

            @Override
            public void setVariable( String name, Object value, Type valueJvmType ){
                var ef = exportedFields.get(name);
                if( ef!=null ){
                    varScope.put(name, ef.stslType, value);
                }
            }
        };
    }
}