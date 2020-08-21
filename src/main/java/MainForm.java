import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import config.SettingsLoader;
import entity.Product;
import exception.AppException;
import util.Importer;
import util.PrinterGUI;
import util.Uploader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class MainForm extends JFrame {
    private JPanel panel1;
    private JButton saveButton;
    private JCheckBox checkBoxCSV;
    private JCheckBox checkBoxImages;
    private JTextField textFieldURL;
    private JTextPane textPane;
    private JButton buttonUpload;
    private JScrollPane scrollPane;
    private JButton stopUloadButton;
    private static final String DEFAULT_URL_PREFIX = "https://ih1947831.my-ihor.ru/wp-content/uploads/products/";
    private PrinterGUI printer;
    private Importer importer;
    private SettingsLoader settingsLoader;
    private Uploader uploader;

    private MainForm() {
        setTitle("Importer");
        setContentPane(panel1);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        textPane.setEditable(false);
        printer = new PrinterGUI(textPane, scrollPane);
        importer = new Importer(printer);
        settingsLoader = new SettingsLoader();

        setUpButtons();

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(setUpFileMenu());
        menuBar.add(setUpInfoMenu());
        setJMenuBar(menuBar);

        textFieldURL.setText(DEFAULT_URL_PREFIX);

        setVisible(true);
    }

    public static void main(String[] args) {
        new MainForm();
    }

    private JMenu setUpInfoMenu() {
        JMenu infoMenu = new JMenu("Info");
        JMenuItem showProducts = new JMenuItem("Print products");
        JMenuItem showTable = new JMenuItem("Show table");
        showProducts.addActionListener(e -> importer.showProducts());
        showTable.addActionListener(e -> {
            List<Product> products = importer.getProducts();
            if (products != null) {
                TableForm tableForm = new TableForm(products);
                tableForm.setVisible(true);
            }
        });
        infoMenu.add(showProducts);
        infoMenu.add(showTable);
        return infoMenu;
    }

    private JMenu setUpFileMenu() {
        JMenu fileMenu = new JMenu("File");
        JMenuItem select = new JMenuItem("Select catalog folder");

        select.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File("."));
            chooser.setDialogTitle("Select catalog folder");
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setAcceptAllFileFilterUsed(false);

            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                importer.setCatalogLocation(chooser.getSelectedFile().toPath());
                printer.print("[INFO] Catalog folder is set to: " + importer.getCatalogLocation());
                try {
                    settingsLoader.load(importer.getCatalogLocation());
                } catch (AppException ex) {
                    showMessageDialog(ex.getMessage());
                }
                try {
                    importer.loadProducts();
                } catch (AppException ex) {
                    printer.print(ex.getMessage());
                    showMessageDialog(ex.getMessage());
                }
            } else {
                printer.print("[INFO] Catalog folder is not set.");
            }
        });

        fileMenu.add(select);

        return fileMenu;
    }

    private void setUpButtons() {
        saveButton.addActionListener(e -> {
            String urlPrefix = textFieldURL.getText();

            if (!urlPrefix.contains("http") || urlPrefix.isBlank()) {
                showMessageDialog("[WARN] Url prefix could be incorrect!");
            }

            importer.setUrlPrefix(urlPrefix);

            if (checkBoxCSV.isSelected()) {
                printer.print("[INFO] Saving CSV");
                try {
                    importer.saveProductsToCSV();
                } catch (AppException ex) {
                    showMessageDialog(ex.getMessage());
                    printer.print(ex.getMessage());
                }
            }

            if (checkBoxImages.isSelected()) {
                printer.print("[INFO] Saving images");
                try {
                    importer.saveImages();
                } catch (AppException ex) {
                    showMessageDialog(ex.getMessage());
                    printer.print(ex.getMessage());
                }
            }

            if (!checkBoxImages.isSelected() && !checkBoxCSV.isSelected()) {
                showMessageDialog("You should select one of the options to save.");
            }
        });

        buttonUpload.addActionListener(e -> {
            if (importer.getCatalogLocation() != null) {

                String URL = settingsLoader.getProperties().getProperty("URL");
                String user = settingsLoader.getProperties().getProperty("user");
                String password = settingsLoader.getProperties().getProperty("password");

                if (URL == null || user == null || password == null) {
                    showMessageDialog("[WARN] Some of the ftp settings are not set.");
                    return;
                }

                printer.print("[INFO] Uploading");
                buttonUpload.setVisible(false);
                stopUloadButton.setVisible(true);
                uploader = new Uploader(
                        printer,
                        importer.getCatalogLocation().getParent().toString() + "\\images\\",
                        URL,
                        user,
                        password);
                uploader.addDoneListener(() -> {
                    buttonUpload.setVisible(true);
                    stopUloadButton.setVisible(false);
                });
                uploader.execute();
            } else {
                showMessageDialog("You should set catalog folder first");
            }
        });

        stopUloadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                printer.print("[INFO] Stopping upload");
                uploader.cancel(true);
                buttonUpload.setVisible(true);
                stopUloadButton.setVisible(false);
            }
        });
    }

    private void showMessageDialog(String message) {
        JOptionPane.showMessageDialog(this, message);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));
        scrollPane = new JScrollPane();
        panel1.add(scrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        textPane = new JTextPane();
        scrollPane.setViewportView(textPane);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save:");
        panel2.add(saveButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxCSV = new JCheckBox();
        checkBoxCSV.setText("CSV file");
        panel2.add(checkBoxCSV, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        checkBoxImages = new JCheckBox();
        checkBoxImages.setText("Images");
        panel2.add(checkBoxImages, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        buttonUpload = new JButton();
        buttonUpload.setText("Upload images");
        panel2.add(buttonUpload, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        stopUloadButton = new JButton();
        stopUloadButton.setText("Stop");
        stopUloadButton.setVisible(false);
        panel2.add(stopUloadButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("Image url prefix:");
        label1.setToolTipText("Folder on the server where images will be stored.");
        panel3.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        textFieldURL = new JTextField();
        panel3.add(textFieldURL, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
