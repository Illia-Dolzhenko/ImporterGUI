package util;

import javax.swing.*;
import javax.swing.text.*;

public class Printer {

    JTextPane textPane;

    public Printer(JTextPane textPane){
        this.textPane = textPane;
    }

    public void print(String text){
        try {
            Document doc = textPane.getDocument();
            doc.insertString(doc.getLength(), text + System.lineSeparator(), null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
