package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.types.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JvmAssignable {
    private volatile Map<Class<?>, Type> _jvm2type;
    public Map<Class<?>, xyz.cofe.stsl.types.Type> jvm2type(){
        if( _jvm2type!=null )return _jvm2type;
        synchronized( this ){
            if( _jvm2type!=null )return _jvm2type;
            Map<Class<?>, xyz.cofe.stsl.types.Type> m = new HashMap<>();

            m.put(byte.class, JvmType.BYTE());
            m.put(Byte.class, JvmType.BYTE());
            m.put(short.class, JvmType.SHORT());
            m.put(Short.class, JvmType.SHORT());
            m.put(int.class, JvmType.INT());
            m.put(Integer.class, JvmType.INT());
            m.put(long.class, JvmType.LONG());
            m.put(Long.class, JvmType.LONG());
            m.put(float.class, JvmType.FLOAT());
            m.put(Float.class, JvmType.FLOAT());
            m.put(double.class, JvmType.DOUBLE());
            m.put(Double.class, JvmType.DOUBLE());

            m.put(boolean.class, JvmType.BOOLEAN());
            m.put(Boolean.class, JvmType.BOOLEAN());

            m.put(char.class, JvmType.CHAR());
            m.put(Character.class, JvmType.CHAR());

            m.put(String.class, JvmType.STRING());
            m.put(java.math.BigInteger.class, JvmType.BIGINT());
            m.put(java.math.BigDecimal.class, JvmType.DECIMAL());

            _jvm2type = m;
            return _jvm2type;
        }
    }

//    public Type typeOf( Class<?> cls ){
//        if( cls==null )throw new IllegalArgumentException( "cls==null" );
//    }
}
