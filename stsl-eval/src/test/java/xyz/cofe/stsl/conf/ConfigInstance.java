package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.eval.TastCompiler;

import java.lang.reflect.Proxy;

public class ConfigInstance<T> extends TastCompiler {
    private final T proxy;
    private final Class<T> confItf;
    private final ConfigInstanceHandler handler;

    protected ConfigInstance( T proxy, Class<T> itf, ConfigInstanceHandler handler ){
        this.proxy = proxy;
        this.confItf = itf;
        this.handler = handler;
    }

    public static <T> ConfigInstance<T> create(Class<T> configType) {
        if( configType==null )throw new IllegalArgumentException( "configType==null" );
        if( !configType.isInterface() )throw new IllegalArgumentException( "!configType.isInterface()" );

        var h = new ConfigInstanceHandler(configType);

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
