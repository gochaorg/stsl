package xyz.cofe.stst.eval.swing;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import java.awt.BorderLayout;
import java.awt.Font;

public class OutputPanel extends JPanel {
    public final JScrollPane scrollPane;
    public final JTextPane textPane;
    public OutputPanel(){
        textPane = new JTextPane();
        scrollPane = new JScrollPane(textPane);
        setLayout(new BorderLayout());
        add(textPane, BorderLayout.CENTER);

        textPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
    }
}
