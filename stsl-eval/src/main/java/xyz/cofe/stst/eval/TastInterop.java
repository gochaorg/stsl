package xyz.cofe.stst.eval;

import java.lang.reflect.Type;

/**
 * Взаимодействие с TAST в RunTime
 */
public interface TastInterop {
    default void setVariable( String name, Object value, Type valueJvmType ){
        return;
    }

    Object computeField(String name);

    TastInterop dummy = new TastInterop() {
        @Override
        public Object computeField( String name ){
            return null;
        }
    };
}
