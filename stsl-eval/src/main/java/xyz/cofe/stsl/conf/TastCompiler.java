package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.shade.scala.Function0;
import xyz.cofe.stsl.shade.scala.Function1;
import xyz.cofe.stsl.shade.scala.Function2;
import xyz.cofe.stsl.shade.scala.Function3;
import xyz.cofe.stsl.shade.scala.None$;
import xyz.cofe.stsl.shade.scala.Option;
import xyz.cofe.stsl.shade.scala.Some$;
import xyz.cofe.stsl.tast.AnonymousObject;
import xyz.cofe.stsl.tast.ArrayCompiler;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.PojoCompiler;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.Toaster;
import xyz.cofe.stsl.tast.ToasterTracer;
import xyz.cofe.stsl.tast.TypeScope;
import xyz.cofe.stsl.tast.VarScope;
import xyz.cofe.stsl.tast.isect.OptBaker;
import xyz.cofe.stsl.tast.isect.OptionalBuilder;
import xyz.cofe.stsl.tast.isect.OptionalField;
import xyz.cofe.stsl.tast.isect.TAnonReductor;
import xyz.cofe.stsl.types.AnyVariant;
import xyz.cofe.stsl.types.GenericInstance;
import xyz.cofe.stsl.types.Named;
import xyz.cofe.stsl.types.TAnon;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TastCompiler {
    private final Type anyType = Type.ANY();

    private final TObject optType;

    {
        var t = TObject.create("Opt").extend(anyType).build();
        t.generics().append(new AnyVariant("A"));
        optType = t;
    }

    private final OptionalField optionalField = new OptionalField(optType, "A");

    private final OptionalBuilder optionalBuilder = new OptBaker();

    private final AnonymousObject.MethodBuilder anonMethodBuilder = AnonymousObject.asIsMethodBuilder();

    private final TAnonReductor tAnonReductor = new TAnonReductor(optionalField, optionalBuilder, anonMethodBuilder);

    private final TObject arrayType;

    {
        var t = TObject.create("List").extend(anyType).build();
        t.generics().append(new AnyVariant("A"));
        arrayType = t;
    }

    @SuppressWarnings("rawtypes")
    private final Function1 arrayTypeConstruct_bake = v1 -> {
        var elementType = (Type) v1;
        var t = arrayType.typeVarBaker().thiz(Map.of("A", elementType));
        if( elementType instanceof Named ){
            return t.withName("List_" + ((Named) elementType).name());
        }
        return t;
    };

    @SuppressWarnings("rawtypes")
    private final Function1 arrayTypeConstruct_genericInstance = v1 -> {
        var elementType = (Type) v1;
        var t = GenericInstance.set("A", elementType).build(arrayType);
        return t;
    };

    @SuppressWarnings("rawtypes")
    private final Function1 arrayTypeConstruct = arrayTypeConstruct_genericInstance;

    @SuppressWarnings({"rawtypes", "Convert2MethodRef"})
    private final Function0 emptyArray = () -> new ArrayList<Object>();

    @SuppressWarnings("rawtypes")
    private final Function3 appendItem3 = ( listInst, item, itemType ) -> {
        @SuppressWarnings("unchecked") var lst = (List<Object>) listInst;
        lst.add(item);
        return lst;
    };

    @SuppressWarnings("rawtypes")
    private final Function2 appendItem2 = ( listInst, item ) -> {
        @SuppressWarnings("unchecked") var lst = (List<Object>) listInst;
        lst.add(item);
        return lst;
    };

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final ArrayCompiler.MergeAnon mergeAnon_ArrayCompiler = new ArrayCompiler.MergeAnon(
        tAnonReductor.anonCollector(),
        tAnonReductor.anonReductor(),
        arrayTypeConstruct,
        emptyArray,
        appendItem3,
        None$.empty(),
        anyType
    );

    @SuppressWarnings({"rawtypes", "unchecked"})
    private final ArrayCompiler.FirstAssignType firstAssign_ArrayCompiler = new ArrayCompiler.FirstAssignType(
        arrayTypeConstruct,
        emptyArray,
        appendItem3,
        None$.empty(),
        anyType
    );

    private final ArrayCompiler.AnyTypedArray anyTypedArray_ArrayCompiler = new ArrayCompiler.AnyTypedArray(
        arrayTypeConstruct,
        emptyArray,
        appendItem2,
        None$.empty(),
        anyType
    );

    private final ArrayCompiler.LookupMerge lookupMerge_ArrayCompiler = new ArrayCompiler.LookupMerge(
        ( tast ) -> {
            if( tast.supplierType() instanceof TAnon ){
                return Some$.MODULE$.apply(mergeAnon_ArrayCompiler);
            }
            return None$.empty();
        },
        firstAssign_ArrayCompiler
    );

    private final ArrayCompiler.FallbackMerge fallbackMerge_ArrayCompiler = new ArrayCompiler.FallbackMerge(
        lookupMerge_ArrayCompiler,
        anyTypedArray_ArrayCompiler,
        None$.empty()
    );

    //region typeScope() : TypeScope
    private volatile TypeScope _ts;

    public TypeScope typeScope(){
        if( _ts != null ) return _ts;
        synchronized( this ){
            if( _ts != null ) return _ts;
            TypeScope ts = new TypeScope();
            ts.setImplicits(JvmType.implicitConversion());
            ts.imports(JvmType.types());
            ts.imports(optType);
            _ts = ts;
            return _ts;
        }
    }

    public void withToasterTracer( ToasterTracer tracer ){
        if( tracer == null ) throw new IllegalArgumentException("tracer==null");
        synchronized( this ){
            _toaster = toaster().withTracer(tracer);
        }
    }

    //endregion
    //region varScope() : VarScope
    private volatile VarScope _vs;

    public VarScope varScope(){
        if( _vs != null ) return _vs;
        synchronized( this ){
            if( _vs != null ) return _vs;
            var vs = new VarScope();
            vs.put("true", JvmType.BOOLEAN(), true);
            vs.put("false", JvmType.BOOLEAN(), false);
            _vs = vs;
            return _vs;
        }
    }

    //endregion
    //region toaster() : Toaster
    private volatile Toaster _toaster;

    public Toaster toaster(){
        if( _toaster != null ) return _toaster;
        synchronized( this ){
            if( _toaster != null ) return _toaster;
            _toaster = Toaster
                .defaultToaster(typeScope(), varScope())
                .withPojoCompile(new PojoCompiler.TAnonPojo())
                .withArrayCompile(fallbackMerge_ArrayCompiler)
            ;
            return _toaster;
        }
    }

    //endregion
    //region parser() : Parser
    private volatile Parser _parser;

    public Parser parser(){
        if( _parser != null ) return _parser;
        synchronized( this ){
            if( _parser != null ) return _parser;
            _parser = Parser.defaultParser().withArraySupport(true);
            return _parser;
        }
    }
    //endregion

    public TAST compile( String source ){
        if( source == null ) throw new IllegalArgumentException("source==null");

        Option<AST> astOpt = parser().parse(source);
        if( astOpt.isEmpty() ){
            throw new RuntimeException("ast not parsed for " + source);
        }

        return toaster().compile(astOpt.get());
    }
}
