package xyz.cofe.stsl.conf.reflect;

import java.lang.reflect.Type;
import java.util.Optional;

public interface JvmType {
    public static Optional<? extends JvmType> of( Type jvmType ){
        if( jvmType == null ) throw new IllegalArgumentException("jvmType==null");
        Optional<JvmType> a = JvmObjContainer.of(jvmType).map(x -> (JvmType) x);
        Optional<JvmType> b = JvmPrimitive.of(jvmType).map(x -> (JvmType) x);
        Optional<? extends JvmType> z = b.or(() -> a);
        return z;
    }

    public static final JvmType THIS = new JvmType() {
    };
}
