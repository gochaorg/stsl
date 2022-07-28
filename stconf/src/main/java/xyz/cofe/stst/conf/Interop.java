package xyz.cofe.stst.conf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Interop {
    private final Map<Class<?>,Class<?>> impls = new HashMap<>();
    private final AtomicInteger idSeq = new AtomicInteger(0);
    private volatile ClassLoader classLoader;
    private ClassLoader getClassLoader(){
        if( classLoader!=null )return classLoader;
        synchronized( this ){
            if( classLoader!=null )return classLoader;
            classLoader = createClassLoader();
            return classLoader;
        }
    }

    private ClassLoader createClassLoader(){
        return new ClassLoader(this.getClassLoader()) {
            @Override
            protected Class<?> findClass( String className ) throws ClassNotFoundException{
                return super.findClass(className);
            }
        };
    }

    public <T> T compile(Class<T> clazz){
        if( clazz==null )throw new IllegalArgumentException( "clazz==null" );
        return null;
    }
}
