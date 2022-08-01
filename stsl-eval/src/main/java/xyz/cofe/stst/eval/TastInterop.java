package xyz.cofe.stst.eval;

public interface TastInterop {
    Object computeField(String name);

    TastInterop dummy = new TastInterop() {
        @Override
        public Object computeField( String name ){
            return null;
        }
    };
}
