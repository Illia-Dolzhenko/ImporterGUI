import entity.Product;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableForm extends JFrame {

    JTable table;


    public TableForm(List<Product> products) {
        setTitle("Product table");
        String[] header = new String[]{"Sku", "Weight", "Name", "Description", "Categories", "File"};
        String[][] values = new String[products.size()][header.length];
        int i = 0;
        for (Product product : products) {
            values[i][0] = product.getSKU();
            values[i][1] = product.getWeight();
            values[i][2] = product.getName();
            values[i][3] = product.getDescription();
            values[i][4] = product.getCategories();
            values[i][5] = product.getFile().toString();
            i++;
        }
        table = new JTable(values, header);
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.ASCENDING),
                new RowSorter.SortKey(1, SortOrder.ASCENDING),
                new RowSorter.SortKey(2, SortOrder.ASCENDING),
                new RowSorter.SortKey(3, SortOrder.ASCENDING),
                new RowSorter.SortKey(4, SortOrder.ASCENDING),
                new RowSorter.SortKey(5, SortOrder.ASCENDING)));
        setContentPane(new JScrollPane(table));
        setSize(640, 480);
    }

}
