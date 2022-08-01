package xyz.cofe.stst.eval;

import xyz.cofe.jvmbc.JavaClassName;
import xyz.cofe.jvmbc.MDesc;
import xyz.cofe.jvmbc.TDesc;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.jvmbc.cls.CField;
import xyz.cofe.jvmbc.cls.CMethod;
import xyz.cofe.jvmbc.cls.ClassFactory;
import xyz.cofe.jvmbc.mth.MCode;
import xyz.cofe.jvmbc.mth.MEnd;
import xyz.cofe.jvmbc.mth.MFieldInsn;
import xyz.cofe.jvmbc.mth.MInsn;
import xyz.cofe.jvmbc.mth.MLabel;
import xyz.cofe.jvmbc.mth.MLdc;
import xyz.cofe.jvmbc.mth.MLocalVariable;
import xyz.cofe.jvmbc.mth.MMaxs;
import xyz.cofe.jvmbc.mth.MMethod;
import xyz.cofe.jvmbc.mth.MType;
import xyz.cofe.jvmbc.mth.MVar;
import xyz.cofe.jvmbc.mth.MethodByteCode;
import xyz.cofe.jvmbc.mth.OpCode;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ImplBuilder {
    private final ClassFactory<
        CBegin<CField, CMethod<List<MethodByteCode>>,List<MethodByteCode>>,
        CField, CMethod<List<MethodByteCode>>,List<MethodByteCode>
        > cf = new ClassFactory.Default();

    private int classVersion = 55;
    private int access = 33;

    public <T> CBegin<CField, CMethod<List<MethodByteCode>>,List<MethodByteCode>> build( Class<T> clazz, String className ){
        if( className==null )throw new IllegalArgumentException( "className==null" );
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );
        if( clazz.isPrimitive() )throw new IllegalArgumentException("is primitive");
        if( clazz.isInterface() )throw new IllegalArgumentException("is interface");
        if( clazz.isAnonymousClass() )throw new IllegalArgumentException("is anonymous");

        return build( new BClass(clazz, new JavaClassName(className.replace('.','/'))) );
    }

    private class BClass {
        public final Class<?> source;
        public final JavaClassName targetName;
        public final JavaClassName sourceName;
        public final TDesc targetTDesc;
        public final String interopFieldName;
        public final TDesc interopFieldType;
        public final JavaClassName interopFieldJavaTypeName;
        public BClass( Class<?> source, JavaClassName targetName ){
            this.source = source;
            this.targetName = targetName;
            this.sourceName = new JavaClassName(source.getName().replace('.','/'));

            targetTDesc = new TDesc("L"+targetName.rawName()+";");
            interopFieldName = "interop";
            interopFieldType = new TDesc("L"+TastInterop.class.getName().replace(".","/")+";");
            interopFieldJavaTypeName = new JavaClassName(TastInterop.class.getName().replace(".","/"));
        }
    }

    private CBegin<CField, CMethod<List<MethodByteCode>>,List<MethodByteCode>> build( BClass clz ){
        var cb =
            cf.cbegin(
                classVersion,
                access,
                clz.targetName.rawName(), // target name
                null, // generic type
                clz.sourceName.rawName(), // parent name
                new String[]{} // interfaces
            );

        // add fields
        interopField(cb, clz);

        // add constructor
        ctor_default(cb, clz);
        ctor_interop(cb, clz);

        // add methods
        var methods = Arrays.asList(clz.source.getMethods());
        for( var method : methods ){
            var e = method.getAnnotation(eval.class);
            if( e!=null ){
                if( method.getParameterCount()==0 ){
                    implField_interop(cb, method, e, clz);
                    //implField_def(cb, method, e, clz);
                }
            }
        }

        return cb;
    }

    private void interopField( CBegin<? super CField,?,?> cb, BClass clz ){
        var fld = cf.cfield(2,clz.interopFieldName,clz.interopFieldType.getRaw(), null, null);
        cb.getFields().add(fld);
    }

    private static final String DEFAULT_CTOR_DESC = "()V";
    private static final String CTOR_METHOD_NAME = "<init>";
    private static final String BEGIN_LABEL = "begin";
    private static final String END_LABEL = "end";

    private void ctor_default( CBegin<?,? super CMethod<List<MethodByteCode>>,?> cb, BClass clz ) {
        var cm = cf.cmethod();
        cm.setName(CTOR_METHOD_NAME);
        cm.setAccess(1);
        cm.desc().setRaw(DEFAULT_CTOR_DESC);
        cm.setSignature(Optional.empty());
        cm.setExceptions(null);

        cm.getMethodByteCodes().add(new MCode());
        cm.getMethodByteCodes().add(new MLabel(BEGIN_LABEL));

        cm.getMethodByteCodes().add(new MVar(OpCode.ALOAD.code, 0));
        cm.getMethodByteCodes().add(new MMethod(OpCode.INVOKESPECIAL.code, clz.sourceName.rawName(), CTOR_METHOD_NAME, DEFAULT_CTOR_DESC, false));

        cm.getMethodByteCodes().add(new MInsn(OpCode.RETURN.code));

        cm.getMethodByteCodes().add(new MLabel(END_LABEL));
        cm.getMethodByteCodes().add(new MLocalVariable(
            "this", //name
            clz.targetTDesc.getRaw(), //desc
            null,  //signature
            BEGIN_LABEL,
            END_LABEL,
            0
        ));
        cm.getMethodByteCodes().add(new MMaxs());
        cm.getMethodByteCodes().add(new MEnd());

        cb.getMethods().add(cm);
    }

    private void ctor_interop( CBegin<?,? super CMethod<List<MethodByteCode>>,?> cb, BClass clz ) {
        var cm = cf.cmethod();
        cm.setName(CTOR_METHOD_NAME);
        cm.setAccess(1);
        cm.desc().setRaw("("+clz.interopFieldType.getRaw()+")V");
        cm.setSignature(Optional.empty());
        cm.setExceptions(null);

        cm.getMethodByteCodes().add(new MCode());
        cm.getMethodByteCodes().add(new MLabel(BEGIN_LABEL));

        cm.getMethodByteCodes().add(new MVar(OpCode.ALOAD.code, 0));
        cm.getMethodByteCodes().add(new MMethod(OpCode.INVOKESPECIAL.code, clz.sourceName.rawName(), CTOR_METHOD_NAME, DEFAULT_CTOR_DESC, false));

        cm.getMethodByteCodes().add(new MVar(OpCode.ALOAD.code, 0));
        cm.getMethodByteCodes().add(new MVar(OpCode.ALOAD.code, 1));
        cm.getMethodByteCodes().add(new MFieldInsn(OpCode.PUTFIELD.code, clz.targetName.rawName(), clz.interopFieldName, clz.interopFieldType.getRaw()));

        cm.getMethodByteCodes().add(new MInsn(OpCode.RETURN.code));

        cm.getMethodByteCodes().add(new MLabel(END_LABEL));
        cm.getMethodByteCodes().add(new MLocalVariable(
            "this", //name
            clz.targetTDesc.getRaw(), //desc
            null,  //signature
            BEGIN_LABEL,
            END_LABEL,
            0
        ));
        cm.getMethodByteCodes().add(new MLocalVariable(
            clz.interopFieldName, //name
            clz.interopFieldType.getRaw(), //desc
            null,  //signature
            BEGIN_LABEL,
            END_LABEL,
            1
        ));
        cm.getMethodByteCodes().add(new MMaxs());
        cm.getMethodByteCodes().add(new MEnd());

        cb.getMethods().add(cm);
    }

    private static class Return {
        public final List<MethodByteCode> pushDefault;
        public final List<MethodByteCode> retyrn;
        public final List<MethodByteCode> castFromObject;
        public final TDesc typeDesc;
        public Return( TDesc typeDesc,
                       List<MethodByteCode> pushDefault,
                       List<MethodByteCode> retyrn,
                       List<MethodByteCode> castFromObject
        ){
            this.typeDesc = typeDesc;
            this.pushDefault = pushDefault;
            this.retyrn = retyrn;
            this.castFromObject = castFromObject;
        }
    }
    private static final Map<Type, Return> primitives =
        Map.of(
            int.class,
            new Return(
                new TDesc("I"),
                List.of(new MInsn(OpCode.ICONST_0.code)),
                List.of(new MInsn(OpCode.IRETURN.code)),
                List.of(
                    new MType(OpCode.CHECKCAST.code, "java/lang/Integer"),
                    new MMethod(OpCode.INVOKEVIRTUAL.code, "java/lang/Integer", "intValue", "()I", false)
                )
            )
        );

    private Optional<Return> defaultReturn( Type returnType ){
        var r = primitives.get(returnType);
        return r!=null ? Optional.of(r) : Optional.empty();
    }

    private void implField_def( CBegin<?,? super CMethod<List<MethodByteCode>>,?> cb, Method method, eval ev, BClass clz ){
        var retTypeJVM = method.getGenericReturnType();
        var defaultOpt = defaultReturn(retTypeJVM);
        if( defaultOpt.isEmpty() )throw new RuntimeException("not implement for "+retTypeJVM);

        var defs = defaultOpt.get();
        var mdesc = new MDesc("()"+defs.typeDesc.getRaw() );

        var cm = cf.cmethod();
        cm.setName(method.getName());
        cm.setAccess(1);
        cm.desc().setRaw(mdesc.getRaw());
        cm.setSignature(Optional.empty());
        cm.setExceptions(null);

        cm.getMethodByteCodes().add(new MCode());
        cm.getMethodByteCodes().add(new MLabel(BEGIN_LABEL));

        cm.getMethodByteCodes().addAll(defs.pushDefault);
        cm.getMethodByteCodes().addAll(defs.retyrn);

        cm.getMethodByteCodes().add(new MLabel(END_LABEL));
        cm.getMethodByteCodes().add(new MLocalVariable(
            "this", //name
            clz.targetTDesc.getRaw(), //desc
            null,  //signature
            BEGIN_LABEL,
            END_LABEL,
            0
        ));
        cm.getMethodByteCodes().add(new MMaxs());
        cm.getMethodByteCodes().add(new MEnd());

        cb.getMethods().add(cm);
    }

    private List<MethodByteCode> callInterop_computeField( String nameArg, BClass clz ){
        return List.of(
            new MVar(OpCode.ALOAD.code, 0),
            new MFieldInsn(OpCode.GETFIELD.code, clz.targetName.rawName(), clz.interopFieldName, clz.interopFieldType.getRaw() ),
            new MLdc( nameArg ),
            new MMethod(OpCode.INVOKEINTERFACE.code, clz.interopFieldJavaTypeName.rawName(), "computeField", "(Ljava/lang/String;)Ljava/lang/Object;", true)
        );
    }

    private void implField_interop( CBegin<?,? super CMethod<List<MethodByteCode>>,?> cb, Method method, eval ev, BClass clz ){
        var retTypeJVM = method.getGenericReturnType();
        var defaultOpt = defaultReturn(retTypeJVM);
        if( defaultOpt.isEmpty() )throw new RuntimeException("not implement for "+retTypeJVM);

        var defs = defaultOpt.get();
        var mdesc = new MDesc("()"+defs.typeDesc.getRaw() );

        var cm = cf.cmethod();
        cm.setName(method.getName());
        cm.setAccess(1);
        cm.desc().setRaw(mdesc.getRaw());
        cm.setSignature(Optional.empty());
        cm.setExceptions(null);

        cm.getMethodByteCodes().add(new MCode());
        cm.getMethodByteCodes().add(new MLabel(BEGIN_LABEL));

        cm.getMethodByteCodes().addAll(callInterop_computeField(method.getName(), clz));
        cm.getMethodByteCodes().addAll(defs.castFromObject);

        //cm.getMethodByteCodes().addAll(defs.pushDefault);
        cm.getMethodByteCodes().addAll(defs.retyrn);

        cm.getMethodByteCodes().add(new MLabel(END_LABEL));
        cm.getMethodByteCodes().add(new MLocalVariable(
            "this", //name
            clz.targetTDesc.getRaw(), //desc
            null,  //signature
            BEGIN_LABEL,
            END_LABEL,
            0
        ));
        cm.getMethodByteCodes().add(new MMaxs());
        cm.getMethodByteCodes().add(new MEnd());

        cb.getMethods().add(cm);
    }
}
