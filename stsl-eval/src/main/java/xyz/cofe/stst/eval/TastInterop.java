package xyz.cofe.stst.eval;

import xyz.cofe.stsl.types.Type;

import java.util.Optional;

public interface TastInterop {
//    public Optional<Type> hasVariable(String name);
//    public void declareVariable(String name, Type type);
//    public void setVariable(String name,Object value);
    public Object computeField(String name);

    public static TastInterop dummy = new TastInterop() {
//        @Override
//        public Optional<Type> hasVariable( String name ){
//            return Optional.empty();
//        }
//
//        @Override
//        public void declareVariable( String name, Type type ){
//        }
//
//        @Override
//        public void setVariable( String name, Object value ){
//        }

        @Override
        public Object computeField( String name ){
            return null;
        }
    };
}
