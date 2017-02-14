package ui;

import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;

/**
 * A two dimensional data structure used to store double instances, usually for display in a JTable
 * component.
 *
 * @author Kareem Horstink
 * @version 1.0
 */
public class TableModel extends AbstractTableModel implements Serializable {

    static final long serialVersionUID = 6680042567037222321L;
    private ArrayList<ArrayList<Double>> data;
    private ArrayList<String> header;

    /**
     * Default Constructor which sets the header
     *
     * @param header The header to be added
     */
    protected TableModel(ArrayList<String> header) {
        data = new ArrayList<>();
        this.header = header;
    }

    /**
     * Default Constructor which sets the header
     *
     * @param header The header to be added
     */
    protected TableModel(String[] header) {
        ArrayList<String> tmp = new ArrayList();
        for (String tmp1 : header) {
            tmp.add(tmp1);
        }
        this.header = tmp;
        data = new ArrayList<>();
        System.out.println(data.size());
        ArrayList tmp1 = new ArrayList();
        tmp1.add(0d);
        tmp1.add(0d);
        tmp1.add(0d);
        data.add(tmp1);

        System.out.println(data);

    }

    /**
     * Adds a new row to the table
     *
     * @param rowData The row to be added
     */
    protected void addRow(Object[] rowData) {
        ArrayList tmp = new ArrayList();
        for (Object tmp1 : rowData) {
            tmp.add(tmp1);
        }
        data.add(tmp);
    }

    /**
     * Gets the data from the table
     *
     * @return The data in the table
     */
    protected ArrayList getData() {
        return data;
    }

    /**
     * Removes the last row
     */
    protected void removeLast() {
        data.remove(data.size() - 1);
        fireTableRowsDeleted(data.size() - 1, data.size() - 1);

    }

    /**
     * Removes the row based on the index
     *
     * @param row Index of the row to be removed
     */
    protected void removeRow(int row) {
        data.remove(row);
        fireTableRowsDeleted(row, row);
    }

    @Override
    public int getRowCount() {
        return data.size() - 1;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        if (column == 0) {
            return false;
        }
        return true;
    }

    @Override
    public int getColumnCount() {
        if (data.isEmpty()) {
            return 0;
        } else {
            return data.get(0).size();
        }
    }

    @Override
    public String getColumnName(int column) {
        return header.get(column);
    }

    @Override
    public Double getValueAt(int rowIndex, int columnIndex) {
        return data.get(rowIndex).get(columnIndex);
    }

    protected void setValueAt(double value, int rowIndex, int columnIndex) {
        data.get(rowIndex).set(columnIndex, value);
        fireTableCellUpdated(rowIndex, columnIndex);
    }

    @Override
    public void setValueAt(Object value, int row, int column) {
        if (value != null) {
            if (isNumeric((String) (value))) {
                data.get(row).set(column, Double.valueOf((String) value));
                fireTableCellUpdated(row, column);
            } else {
                System.out.println("Error /n Please enter a valid number");
            }
        }
    }

    /**
     * Checks if the String is Numeric input
     *
     * @param str The string to be checked
     * @return Returns if its numeric
     */
    protected static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
