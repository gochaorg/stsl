package xyz.cofe.stst.eval;

import xyz.cofe.jvmbc.cls.CBegin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EvalClassloader extends ClassLoader {
    public EvalClassloader(){
    }

    public EvalClassloader( ClassLoader parentLoader ){
        super(parentLoader);
    }

    public EvalClassloader( String classLoaderName, ClassLoader parentLoader ){
        super(classLoaderName, parentLoader);
    }

    private final Map<String, byte[]> classes = new ConcurrentHashMap<>();

    public void put( String name, byte[] byteCode ){
        if( name==null )throw new IllegalArgumentException( "name==null" );
        if( byteCode==null )throw new IllegalArgumentException( "byteCode==null" );
        classes.put(name, byteCode);
    }

    private final Map<String,Class<?>> definedClasses = new ConcurrentHashMap<>();

    @Override
    protected Class<?> findClass( String className ) throws ClassNotFoundException {
        var defCls = definedClasses.get(className);
        if( defCls!=null )return defCls;

        var bc = classes.get(className);
        if( bc!=null ){
            var cls = defineClass(className, bc, 0, bc.length);
            definedClasses.put(className,cls);
            return cls;
        }

        return super.findClass(className);
    }
}
