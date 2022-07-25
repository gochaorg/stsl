package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.shade.scala.Function0;
import xyz.cofe.stsl.shade.scala.Function1;
import xyz.cofe.stsl.shade.scala.Function3;
import xyz.cofe.stsl.shade.scala.None$;
import xyz.cofe.stsl.shade.scala.Option;
import xyz.cofe.stsl.shade.scala.Some$;
import xyz.cofe.stsl.tast.AnonymousObject;
import xyz.cofe.stsl.tast.ArrayCompiler;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.PojoCompiler;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.TASTDump;
import xyz.cofe.stsl.tast.Toaster;
import xyz.cofe.stsl.tast.TypeScope;
import xyz.cofe.stsl.tast.VarScope;
import xyz.cofe.stsl.tast.isect.OptBaker;
import xyz.cofe.stsl.tast.isect.OptionalBuilder;
import xyz.cofe.stsl.tast.isect.OptionalField;
import xyz.cofe.stsl.tast.isect.TAnonReductor;
import xyz.cofe.stsl.types.AnyVariant;
import xyz.cofe.stsl.types.GenericInstance;
import xyz.cofe.stsl.types.Named;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;
import xyz.cofe.stsl.types.TypeVarReplace;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigInstance<T> extends Compile {
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

    public T read(String source) {
        if( source==null )throw new IllegalArgumentException( "source==null" );
        var tast =compile(source);
        handler.setTAST(tast);
        return get();
    }
}
