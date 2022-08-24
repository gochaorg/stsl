package xyz.cofe.stsl.eval.swing;

import xyz.cofe.stst.eval.swing.EditorFrame;
import javax.swing.SwingUtilities;
import static xyz.cofe.stst.eval.swing.SwingEvents.*;

public class EditorTest {
    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            var frame = new EditorFrame();

            frame.editorPanel.textPane.setText("(1 + 2) * 3");
            on(frame).delay(200).opened(frame.editorPanel::highlight);

            frame.setSize(800,600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
