package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import xyz.cofe.stsl.types.TypeDescriber;
import xyz.cofe.text.EndLineReWriter;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.function.Supplier;

public class CommonForTest {
    public final EndLineReWriter reWriter;

    {
        reWriter = new EndLineReWriter(new OutputStreamWriter(System.out));
    }

    public final PrintWriter out;

    {
        out = new PrintWriter(reWriter);
    }

    public class Indent {
        public final String defaultIndent = "  ";

        private int level = 0;

        public void level( int newLevel ){
            level = Math.max(0, newLevel);
            reWriter.setLineConvertor(line -> defaultIndent.repeat(level) + line);
        }

        public int level(){
            return level;
        }

        public <R> R indent( Supplier<R> code ){
            if( code == null ) throw new IllegalArgumentException("code==null");
            var lvl = level;
            try{
                level(lvl + 1);
                return code.get();
            } finally {
                level(lvl);
            }
        }

        public void indent( Runnable code ){
            if( code == null ) throw new IllegalArgumentException("code==null");
            var lvl = level;
            try{
                level(lvl + 1);
                code.run();
            } finally {
                level(lvl);
            }
        }
    }

    public final Indent indent = new Indent();

    public void indent( Runnable code ){
        if( code == null ) throw new IllegalArgumentException("code==null");
        indent.indent(code);
    }

    public <R> R indent( Supplier<R> code ){
        if( code == null ) throw new IllegalArgumentException("code==null");
        return indent.indent(code);
    }

    @BeforeEach
    public void __resetIndent(){
        indent.level = 0;
    }

    @AfterEach
    public void __flushIndent(){
        out.flush();
    }

    public void describe( xyz.cofe.stsl.types.Type type ){
        out.println(TypeDescriber.describe(type));
    }
}
