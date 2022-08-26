package xyz.cofe.stsl.eval.swing;

import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.BinaryAST;
import xyz.cofe.stsl.ast.DelegateAST;
import xyz.cofe.stsl.ast.LiteralAST;
import xyz.cofe.stsl.eval.TastCompiler;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.function.Consumer;

public class EditorPanel extends JPanel {
    public final JTextPane textPane;
    public final JScrollPane scrollPane;

    public EditorPanel(){
        textPane = new JTextPane();
        scrollPane = new JScrollPane(textPane);
        setLayout(new BorderLayout());
        add(scrollPane,BorderLayout.CENTER);

        setFocusable(true);
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained( FocusEvent e ){
                textPane.requestFocus();
            }
        });

        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
    }

    private TastCompiler tastCompiler;
    public TastCompiler getTastCompiler(){
        if( tastCompiler!=null )return tastCompiler;
        tastCompiler = new TastCompiler();
        return tastCompiler;
    }
    public void setTastCompiler(TastCompiler tastCompiler){
        this.tastCompiler = tastCompiler;
    }

    private static final SimpleAttributeSet emptyHighlight = new SimpleAttributeSet();
    private static final SimpleAttributeSet binaryOperatorHighlight = new SimpleAttributeSet();
    static {
        StyleConstants.setForeground(binaryOperatorHighlight, Color.black);
    }

    private static final SimpleAttributeSet literalHighlight = new SimpleAttributeSet();
    static {
        StyleConstants.setForeground(literalHighlight, Color.blue);
    }

    public void highlight(){
        clearHighlight();

        var sd = textPane.getStyledDocument();
        try{

            var source = sd.getText(0, sd.getLength());
            var tastCompiler = getTastCompiler();

            var astOpt = tastCompiler.parser().parse(source);
            if( astOpt.isDefined() ){
                var ast = astOpt.get();
                highlight(sd, ast);
            }
        } catch( BadLocationException e ){
            e.printStackTrace();
        }
    }

    private void clearHighlight(){
        var sd = textPane.getStyledDocument();
        sd.setCharacterAttributes(0, sd.getLength(), emptyHighlight,true);
    }

    private void visit( AST ast, Consumer<AST> cons ){
        cons.accept(ast);
        for( var i=0; i<ast.children().length(); i++ ){
            visit( ast.children().apply(i), cons );
        }
    }

    private void highlight( StyledDocument doc, AST ast ){
        visit( ast, aNode -> {
            System.out.println("node "+aNode.getClass());
            if( aNode instanceof BinaryAST ){
                var binAst = (BinaryAST)aNode;
                //binAst.operator().tok().begin()
            }else if( aNode instanceof DelegateAST ){
            }else if( aNode instanceof LiteralAST ){
                var litAst = (LiteralAST)aNode;

                var from = (Integer)litAst.begin().lookup(0).get().begin().pointer();
                var to = (Integer)litAst.begin().lookup(0).get().end().pointer();
                var len = to - from;

                doc.setCharacterAttributes(from,to-from, literalHighlight, true);
                System.out.println("from="+from+" to="+to+" len="+len);
            }
        });
    }
}
