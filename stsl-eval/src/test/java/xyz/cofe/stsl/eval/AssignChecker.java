package xyz.cofe.stsl.eval;

import java.util.List;

/**
 * Проверка совместимости JVM и TAST
 */
public class AssignChecker {
    public interface Result {
        boolean ok();
        List<String> errors();
    }
    private Result ok(){
        return new Result() {
            @Override
            public boolean ok(){
                return true;
            }

            @Override
            public List<String> errors(){
                return List.of();
            }
        };
    }
    private Result fail( List<String> errors ){
        return new Result() {
            @Override
            public boolean ok(){
                return false;
            }

            @Override
            public List<String> errors(){
                return errors;
            }
        };
    }
    private Result fail( String error ){ return fail(List.of(error)); }

    public Result assignable( java.lang.reflect.Type jvmType, xyz.cofe.stsl.types.Type stslType ){
        return null;
    }
}
