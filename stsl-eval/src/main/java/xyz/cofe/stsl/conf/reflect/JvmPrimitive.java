package xyz.cofe.stsl.conf.reflect;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JvmPrimitive implements JvmType, ToStslType {
    public final String name;
    public final java.lang.reflect.Type jvmType;
    public final xyz.cofe.stsl.types.Type stslType;

    public JvmPrimitive( String name, Type jvmType, xyz.cofe.stsl.types.Type stslType ){
        this.name = name;
        this.jvmType = jvmType;
        this.stslType = stslType;
    }

    private final static Map<Type, JvmPrimitive> jvmPrimitiveMap;

    public static final JvmPrimitive byteWrapper = new JvmPrimitive("Byte", Byte.class, xyz.cofe.stsl.tast.JvmType.BYTE());
    public static final JvmPrimitive bytePrimitive = new JvmPrimitive("byte", byte.class, xyz.cofe.stsl.tast.JvmType.BYTE());

    public static final JvmPrimitive shortPrimitive = new JvmPrimitive("short", short.class, xyz.cofe.stsl.tast.JvmType.SHORT());
    public static final JvmPrimitive shortWrapper = new JvmPrimitive("Short", Short.class, xyz.cofe.stsl.tast.JvmType.SHORT());

    public static final JvmPrimitive intPrimitive = new JvmPrimitive("int", int.class, xyz.cofe.stsl.tast.JvmType.INT());
    public static final JvmPrimitive intWrapper = new JvmPrimitive("Int", Integer.class, xyz.cofe.stsl.tast.JvmType.INT());

    public static final JvmPrimitive longPrimitive = new JvmPrimitive("long", long.class, xyz.cofe.stsl.tast.JvmType.LONG());
    public static final JvmPrimitive longWrapper = new JvmPrimitive("Long", Long.class, xyz.cofe.stsl.tast.JvmType.LONG());

    public static final JvmPrimitive floatPrimitive = new JvmPrimitive("float", float.class, xyz.cofe.stsl.tast.JvmType.FLOAT());
    public static final JvmPrimitive floatWrapper = new JvmPrimitive("Float", Float.class, xyz.cofe.stsl.tast.JvmType.FLOAT());

    public static final JvmPrimitive doublePrimitive = new JvmPrimitive("double", double.class, xyz.cofe.stsl.tast.JvmType.DOUBLE());
    public static final JvmPrimitive doubleWrapper = new JvmPrimitive("Double", Double.class, xyz.cofe.stsl.tast.JvmType.DOUBLE());

    public static final JvmPrimitive charPrimitive = new JvmPrimitive("char", char.class, xyz.cofe.stsl.tast.JvmType.CHAR());
    public static final JvmPrimitive charWrapper = new JvmPrimitive("Char", Character.class, xyz.cofe.stsl.tast.JvmType.CHAR());

    public static final JvmPrimitive boolPrimitive = new JvmPrimitive("bool", boolean.class, xyz.cofe.stsl.tast.JvmType.BOOLEAN());
    public static final JvmPrimitive boolWrapper = new JvmPrimitive("Bool", Boolean.class, xyz.cofe.stsl.tast.JvmType.BOOLEAN());

    public static final JvmPrimitive strWrapper = new JvmPrimitive("str", String.class, xyz.cofe.stsl.tast.JvmType.STRING());

    public static final JvmPrimitive decimalWrapper = new JvmPrimitive("Decimal", java.math.BigDecimal.class, xyz.cofe.stsl.tast.JvmType.DECIMAL());
    public static final JvmPrimitive bigIntWrapper = new JvmPrimitive("BigInt", java.math.BigInteger.class, xyz.cofe.stsl.tast.JvmType.BIGINT());

    public static final JvmPrimitive numWrapper = new JvmPrimitive("Num", java.lang.Number.class, xyz.cofe.stsl.tast.JvmType.NUMBER());

    static{
        var m = new HashMap<Type, JvmPrimitive>();
        var parr = new JvmPrimitive[]{
            bytePrimitive, byteWrapper, shortPrimitive, shortWrapper,
            intPrimitive, intWrapper, longPrimitive, longWrapper,
            floatPrimitive, floatWrapper, doublePrimitive, doubleWrapper,
            charPrimitive, charWrapper,
            boolPrimitive, boolWrapper,
            strWrapper,
            decimalWrapper,
            bigIntWrapper,
            numWrapper
        };
        for( var p : parr ){
            m.put(p.jvmType, p);
        }

        jvmPrimitiveMap = Collections.unmodifiableMap(m);
    }

    public static Optional<JvmPrimitive> of( java.lang.reflect.Type jvmType ){
        if( jvmType == null ) throw new IllegalArgumentException("jvmType==null");
        var t = jvmPrimitiveMap.get(jvmType);
        return t != null ? Optional.of(t) : Optional.empty();
    }

    @Override
    public String toString(){
        return name;
    }

    @Override
    public xyz.cofe.stsl.types.Type toStslType(){
        return stslType;
    }
}
