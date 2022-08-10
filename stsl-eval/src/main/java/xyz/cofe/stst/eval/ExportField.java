package xyz.cofe.stst.eval;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ExportField {
    public final String name;
    public final xyz.cofe.stsl.types.Type stslType;
    public final Type jvmType;
    public final Object defauleValue;

    public ExportField( String name, xyz.cofe.stsl.types.Type stslType, Type jvmType, Object defaultValue ){
        this.name = name;
        this.stslType = stslType;
        this.jvmType = jvmType;
        this.defauleValue = defaultValue;
    }

    public static class DefaultValueForField {
        public final String name;
        public final xyz.cofe.stsl.types.Type stslType;
        public final Type jvmType;

        public DefaultValueForField( String name, xyz.cofe.stsl.types.Type stslType, Type jvmType ){
            this.name = name;
            this.stslType = stslType;
            this.jvmType = jvmType;
        }
    }

    public static List<ExportField> exportFields(
        Class<?> clz,
        Function<Type, Optional<xyz.cofe.stsl.types.Type>> resolveType,
        Function<DefaultValueForField,Object> defaultValue
    ){
        var ls = new ArrayList<ExportField>();
        for( var jvmFld : Arrays.asList(clz.getFields()) ){
            var exportAnn = jvmFld.getAnnotation(export.class);
            if( exportAnn!=null ){
                resolveType.apply(jvmFld.getGenericType()).ifPresent( stslType -> {
                    var defVal = defaultValue.apply(new DefaultValueForField(
                        jvmFld.getName(), stslType, jvmFld.getGenericType()
                    ));
                    ls.add(new ExportField(
                        jvmFld.getName(),
                        stslType,
                        jvmFld.getGenericType(),
                        defVal
                    ));
                });
            }
        }
        return ls;
    }
}
