package xyz.cofe.stst.eval.swing;

import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;

import static xyz.cofe.stst.eval.swing.SwingEvents.*;

public class EditorFrame extends JFrame {
    public final EditorPanel editorPanel;
    public final OutputPanel outputPanel;

    public EditorFrame(){
        editorPanel = new EditorPanel();
        outputPanel = new OutputPanel();

        getContentPane().setLayout(new BorderLayout());

        var splitEditorOutput = new JSplitPane();
        splitEditorOutput.setLeftComponent(editorPanel);
        splitEditorOutput.setRightComponent(outputPanel);
        splitEditorOutput.setOrientation(JSplitPane.HORIZONTAL_SPLIT);

        on(this)
            .opened(()->{
                splitEditorOutput.setDividerLocation(0.5);
                editorPanel.requestFocus();
            });

        getContentPane().add(splitEditorOutput, BorderLayout.CENTER);

        setTitle("stsl eval");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(()->{
            var frame = new EditorFrame();
            frame.setSize(800,600);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
