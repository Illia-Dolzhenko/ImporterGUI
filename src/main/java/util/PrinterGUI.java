package util;

import javax.swing.*;
import javax.swing.text.*;

public class PrinterGUI implements Printer {

    JTextPane textPane;
    JScrollPane scrollPane;

    public PrinterGUI(JTextPane textPane, JScrollPane scrollPane){
        this.textPane = textPane;
        this.scrollPane = scrollPane;
//        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
//            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
//        });
    }

    public void print(String text){
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), text + System.lineSeparator(), null);
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
