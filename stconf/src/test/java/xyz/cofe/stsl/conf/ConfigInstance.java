package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.shade.scala.Option;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.TASTDump;
import xyz.cofe.stsl.tast.Toaster;
import xyz.cofe.stsl.tast.TypeScope;
import xyz.cofe.stsl.tast.VarScope;
import xyz.cofe.stsl.types.AnyVariant;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class ConfigInstance<T> {
    private final T proxy;
    private final Class<T> confItf;
    private final ConfInstHandler handler;

    protected ConfigInstance( T proxy, Class<T> itf, ConfInstHandler handler ){
        this.proxy = proxy;
        this.confItf = itf;
        this.handler = handler;
    }

    public static <T> ConfigInstance<T> create(Class<T> configType) {
        if( configType==null )throw new IllegalArgumentException( "configType==null" );
        if( !configType.isInterface() )throw new IllegalArgumentException( "!configType.isInterface()" );

        var h = new ConfInstHandler(configType);

        //noinspection unchecked
        T inst = (T)Proxy.newProxyInstance(
            configType.getClassLoader(),
            new Class<?>[]{ configType },
            h
        );

        //noinspection InstantiationOfUtilityClass
        return new ConfigInstance<T>(inst, configType, h);
    }

    public T get(){
        return proxy;
    }

    private volatile Type anyType;
    public Type anyType(){
        if( anyType!=null )return anyType;
        synchronized( this ){
            if( anyType!=null )return anyType;
            anyType = Type.ANY();
            return anyType;
        }
    }

    private volatile TObject optType;
    public TObject optType(){
        if( optType!=null )return optType;
        synchronized( this ){
            if( optType!=null )return optType;
            var t = TObject.create("Opt").extend(anyType()).build();
            t.generics().append(new AnyVariant("A"));
            optType = t;
            return optType;
        }
    }

//    private volatile xyz.cofe.stsl.tast.isect.package. optionalField;
//    public OptionalField

    //region typeScope() : TypeScope
    private volatile TypeScope _ts;
    public TypeScope typeScope(){
        if( _ts!=null )return _ts;
        synchronized( this ){
            if( _ts!=null )return _ts;
            TypeScope ts = new TypeScope();
            ts.setImplicits(JvmType.implicitConversion());
            ts.imports(JvmType.types());
            ts.imports(optType());
            _ts = ts;
            return _ts;
        }
    }
    //endregion
    //region varScope() : VarScope
    private volatile VarScope _vs;
    public VarScope varScope(){
        if( _vs!=null )return _vs;
        synchronized( this ){
            if( _vs!=null )return _vs;
            _vs = new VarScope();
            return _vs;
        }
    }
    //endregion
    //region toaster() : Toaster
    private volatile Toaster _toaster;
    public Toaster toaster(){
        if( _toaster!=null )return _toaster;
        synchronized( this ){
            if( _toaster!=null )return _toaster;
            _toaster = Toaster.defaultToaster(typeScope(),varScope());
            return _toaster;
        }
    }
    //endregion
    //region parser() : Parser
    private volatile Parser _parser;
    public Parser parser(){
        if( _parser!=null )return _parser;
        synchronized( this ){
            if( _parser!=null )return _parser;
            _parser = Parser.defaultParser().withArraySupport(true);
            return _parser;
        }
    }
    //endregion

    public T read(String source) {
        if( source==null )throw new IllegalArgumentException( "source==null" );
        var tast =compile(source);
        handler.setTAST(tast);
        return get();
    }

    private boolean dumpAst = false;
    private boolean dumpTast = false;

    public TAST compile(String source){
        Option<AST> astOpt = parser().parse(source);
        if( astOpt.isEmpty() ){
            throw new RuntimeException("ast not parsed");
        }
        if( dumpAst )ASTDump.dump(astOpt.get());

        TAST tast = toaster().compile(astOpt.get());
        if( dumpTast )TASTDump.dump(tast);

        return tast;
    }
}
