package ui;

import curves.NumericalApproximation;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import ui.events.GuiEventsAreaChange;
import ui.events.GuiEventsLengthChange;
import ui.events.GuiEventListner;
import ui.events.GuiEvents;
import ui.events.GuiEventsCurrent;
import ui.events.GuiEventsMove;
import ui.events.GuiEventsRefresh;
import ui.events.GuiEventsVisibility;

/**
 * The sidebar which shows the data input grid and other controls
 *
 * @author Kareem Horstink
 * @version 1.00
 */
public class SideBar extends JTabbedPane implements TableModelListener {

    /**
     * Holds the name of the curve
     */
    private String[] name;

    /**
     * Set the value of name
     *
     * @param name new value of name
     */
    public void setName(String[] name) {
        this.name = name;
    }

    /**
     * JTable to represent the table of info about points
     */
    private JTable table;

    /**
     * The table to hold info
     */
    private TableModel mod;

    /**
     * Current selected line
     */
    private int curveID = -1;

    /**
     * Check box to set the line to be visible or not
     */
    private JCheckBox visibleBox;

    /**
     * Boolean to represent if the line is visible or not
     */
    private boolean visiblity;

    /**
     * List of the control points
     */
    private ArrayList<List<Point2D>> controlPoints;

    /**
     * Checks if the table is currently updating
     */
    private boolean updating1 = false;

    /**
     * Checks if the table is currently updating
     */
    private boolean updating2 = true;

    /**
     * List of the Gui Event Listener
     */
    private final List<GuiEventListner> LISTENER = new ArrayList<GuiEventListner>();

    /**
     * The panel to hold info about the line
     */
    private JPanel info;

    /**
     * The panel to hold the control element of the ui
     */
    private JPanel controls;

    /**
     * An array of string to hold the curve info: Name; Area; Length; Number Of control points; Zoom
     * Level
     *
     */
    private String[] curveInfo;

    /**
     * Array of JLabels containing curveInfo[]
     */
    private JLabel[] infoText;

    /**
     * The amount of curve
     */
    private int numberOfCurve;

    /**
     * Combo box to select the current line
     */
    private JComboBox<String> currentLineComboBox;

    /**
     * Combo box to select the algorithm
     */
    private JComboBox<String> comboLength;

    /**
     * Combo box to select the algorithm
     */
    private JComboBox<String> comboArea;

    private final boolean DEBUG = false;

    /**
     * The constructor of the side bar
     */
    public SideBar() {
        controlPoints = new ArrayList<>();
        initGeneral();
    }

    /**
     * Sets the curve ID
     *
     * @param curveID The index of the current curve
     * @param area The allowed algorithms to find the area of the arc
     * @param length The allowed algorithms to find the length of the arc
     */
    public void setCurveID(int curveID, ArrayList area, ArrayList length) {
        if (this.curveID != curveID) {
            this.curveID = curveID;
            updating1 = true;
            updating2 = true;
            updateTableFull();
            updateComboBox(area, length);
            updating1 = false;
            updating2 = false;

        }
    }

    /**
     * Sets the number of lines in the system
     *
     * @param numberOfCurve The number of curves
     */
    public void setNumberOfCurve(int numberOfCurve) {
        this.numberOfCurve = numberOfCurve;
    }

    /**
     * Sets the control points
     *
     * @param controlPoints The control points to be passed
     */
    protected void setCurves(ArrayList<List<Point2D>> controlPoints) {
        if (curveID == -1) {
        } else {
            this.controlPoints = controlPoints;
            while (this.controlPoints.get(curveID).size() >= mod.getRowCount()) {
                mod.addRow(new Object[]{0d, 0d, 0d});
            }
            updating1 = true;
            updateTableFull();
            updating1 = false;
        }
    }

    /**
     * Updates the table with the current info
     */
    protected void updateTableFull() {
        if (curveID < controlPoints.size()) {

            List<Point2D> curve = controlPoints.get(curveID);
            int counter = 0;
            int rows = mod.getRowCount();
            for (int i = 0; i < rows; i++) {
                if (i < curve.size()) {
                    Point2D point = curve.get(i);
                    mod.setValueAt(point.getX(), counter, 1);
                    mod.setValueAt(point.getY(), counter, 2);
                    mod.setValueAt(counter, counter, 0);
                } else {
                    mod.removeLast();
                }
                counter++;
            }
        }
    }

    /**
     * Initialize the buttons and info boxes
     */
    private void initGeneral() {
        JPanel container = new JPanel(new GridLayout(2, 0));
        controls = new JPanel(new GridLayout(0, 2));
        container.add(controls);
        this.addTab("Controls", container);
        JLabel tmp = new JLabel("Set other lines to invisible");
        tmp.setHorizontalAlignment(SwingConstants.CENTER);
        controls.add(tmp);
        visibleBox = new JCheckBox("");
        visibleBox.setHorizontalAlignment(SwingConstants.CENTER);
        visibleBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEvent(new GuiEventsVisibility(this, visiblity));
                visiblity = !visiblity;
            }
        });
        visibleBox.setToolTipText("Sets all other line to not be visible");
        controls.add(visibleBox);

        initCombo();

        curveInfo = new String[5];

        JButton button = new JButton("Refresh");
        button.setToolTipText("Refresh all the data");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fireEvent(new GuiEventsRefresh(this));
            }
        });

        controls.add(button);
        button = new JButton("Help");
        button.setToolTipText("Shows a help diaglog");
        button.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(controls, "Crtl Click to Add New Points"
                    + "\n" + "Panning with shift click");
        });
        controls.add(button);

        infoText = new JLabel[]{
            new JLabel("Name"),
            new JLabel("Area"),
            new JLabel("Length"),
            new JLabel("Number Of control points"),
            new JLabel("Zoom Level")};
        info = new JPanel(new GridLayout(6, 0));
        info.setToolTipText("Information about the line");
        for (JLabel infoText1 : infoText) {
            infoText1.setHorizontalAlignment(SwingConstants.CENTER);
            info.add(infoText1);
        }
        container.add(info);
        initTable();

    }

    /**
     * Initialize the combo boxes
     */
    private void initCombo() {
        currentLineComboBox = new JComboBox<>(new String[]{""});
        currentLineComboBox.setEnabled(false);
        currentLineComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DEBUG) {
                    System.out.println(e.getActionCommand());
                }
                if (e.getSource() == currentLineComboBox && currentLineComboBox.getItemCount() != 0 && !updating2) {
                    fireEvent(new GuiEventsCurrent(this, currentLineComboBox.getSelectedIndex()));
                    curveID = currentLineComboBox.getSelectedIndex();
                }
            }
        });
        JLabel tmp = new JLabel("Select current Line");
        tmp.setHorizontalAlignment(SwingConstants.CENTER);
        controls.add(tmp);
        controls.add(currentLineComboBox);
        //------------------------------------------------------------------------------------------

        comboArea = new JComboBox<>(new String[]{""});
        comboArea.setEnabled(false);
        comboArea.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DEBUG) {
                    System.out.println(e.getActionCommand());
                }
                if (!updating2) {
                    if (e.getSource() == comboArea) {
                        if (null != (String) comboArea.getSelectedItem()) {
                            switch ((String) comboArea.getSelectedItem()) {
                                case "Shoe Lace Area":
                                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.SHOELACE_AREA));
                                    comboArea.setSelectedItem("Shoe Lace Area");
                                    break;
                                case "Exact Area Cubic":
                                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.EXACT_AREA_CUBIC));
                                    comboArea.setSelectedItem("Exact Area Cubic");
                                    break;
                                case "Exact Ellipse Area":
                                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.EXACT_ELLIPSE_AREA));
                                    comboArea.setSelectedItem("Exact Ellipse Area");
                                    break;
                                case "Richardson Extrapolation Area":
                                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.RICHARDSON_EXTRAPOLATION_AREA));
                                    comboArea.setSelectedItem("Richardson Extrapolation Area");
                                    break;
                            }
                        }
                    }
                }
            }
        });
        JLabel tmp1 = new JLabel("Select Area Calculation Mode");
        tmp1.setHorizontalAlignment(SwingConstants.CENTER);
        controls.add(tmp1);
        controls.add(comboArea);

        //------------------------------------------------------------------------------------------
        comboLength = new JComboBox<>(new String[]{""});
        comboLength.setEnabled(false);
        comboLength.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (DEBUG) {
                    System.out.println(e.getActionCommand());
                }
                if (!updating2) {
                    if (e.getSource() == comboLength) {
                        if (null != (String) comboLength.getSelectedItem()) {
                            switch ((String) comboLength.getSelectedItem()) {
                                case "Romberg Arclength":
                                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.ROMBERG_ARCLENGTH));
                                    comboLength.setSelectedItem("Romberg Arclength");
                                    break;
                                case "Simpons Arclength":
                                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.SIMPSON_ARCLENGTH));
                                    comboLength.setSelectedItem("Simpons Arclength");
                                    break;
                                case "Pythagorean Arclength":
                                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.PYTHAGOREAN_ARCLENGTH));
                                    comboLength.setSelectedItem("Pythagorean Arclength");
                                    break;
                                case "Richson Extrapolation Arclength":
                                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH));
                                    comboLength.setSelectedItem("Richson Extrapolation Arclength");
                                    break;
                                case "Ellipse Arclength Exact":
                                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.ELLIPSE_ARCLENGTH_EXACT));
                                    comboLength.setSelectedItem("Ellipse Arclength Exact");
                                    break;
                                default:
                                    System.out.println("Error in selection");
                                    break;
                            }
                        }
                    }
                }
            }
        });
        JLabel tmp2 = new JLabel("Select Length Calculation Mode");
        tmp2.setHorizontalAlignment(SwingConstants.CENTER);
        controls.add(tmp2);
        controls.add(comboLength);
    }

    /**
     * Initialize the table
     */
    private void initTable() {
        JPanel dataViewer = new JPanel();
        createTable();
        JScrollPane scrollPane = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        dataViewer.add(scrollPane);
        dataViewer.setName("Data Inputter");
        this.addTab(dataViewer.getName(), dataViewer);
    }

    /**
     * Updates the content of the combo boxes
     *
     * @param area
     * @param length
     */
    private void updateComboBox(ArrayList area, ArrayList length) {
        if (curveID != -1) {
            currentLineComboBox.setEnabled(true);
            comboArea.removeAllItems();
            if (area != null && !area.isEmpty()) {
                comboArea.setEnabled(true);
                for (Iterator iterator = area.iterator(); iterator.hasNext();) {
                    int next = (int) iterator.next();
                    switch (next) {
                        case NumericalApproximation.EXACT_AREA_CUBIC:
                            comboArea.addItem("Exact Area Cubic");
                            break;
                        case NumericalApproximation.SHOELACE_AREA:
                            comboArea.addItem("Shoe Lace Area");
                            break;
                        case NumericalApproximation.EXACT_ELLIPSE_AREA:
                            comboArea.addItem("Exact Ellipse Area");
                            break;
                        case NumericalApproximation.RICHARDSON_EXTRAPOLATION_AREA:
                            comboArea.addItem("Richardson Extrapolation Area");
                            break;
                        default:
                            System.out.println("Error - Update Combo Box");
                            break;
                    }
                }
            }
            comboLength.removeAllItems();
            if (length != null && !length.isEmpty()) {
                comboLength.setEnabled(true);
                for (Iterator iterator = length.iterator(); iterator.hasNext();) {
                    int next = (int) iterator.next();
                    switch (next) {
                        case NumericalApproximation.PYTHAGOREAN_ARCLENGTH:
                            comboLength.addItem("Pythagorean Arclength");
                            break;
                        case NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH:
                            comboLength.addItem("Richson Extrapolation Arclength");
                            break;
                        case NumericalApproximation.ROMBERG_ARCLENGTH:
                            comboLength.addItem("Romberg Arclength");
                            break;
                        case NumericalApproximation.SIMPSON_ARCLENGTH:
                            comboLength.addItem("Simpons Arclength");
                            break;
                        case NumericalApproximation.ELLIPSE_ARCLENGTH_EXACT:
                            comboLength.addItem("Ellipse Arclength Exact");
                            break;
                        default:
                            break;
                    }
                }
            }
        }

        if (null != (String) comboArea.getSelectedItem()) {
            switch ((String) comboArea.getSelectedItem()) {
                case "Shoe Lace Area":
                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.SHOELACE_AREA));
                    comboArea.setSelectedItem("Shoe Lace Area");
                    break;
                case "Exact Area Cubic":
                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.EXACT_AREA_CUBIC));
                    comboArea.setSelectedItem("Exact Area Cubic");
                    break;
                case "Exact Ellipse Area":
                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.EXACT_ELLIPSE_AREA));
                    break;
                case "Richardson Extrapolation Area":
                    fireEvent(new GuiEventsAreaChange(this, NumericalApproximation.RICHARDSON_EXTRAPOLATION_AREA));
                    break;
            }
        }
        if (null != (String) comboLength.getSelectedItem()) {

            switch ((String) comboLength.getSelectedItem()) {
                case "Romberg Arclength":
                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.ROMBERG_ARCLENGTH));
                    comboLength.setSelectedItem("Romberg Arclength");
                    break;
                case "Simpons Arclength":
                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.SIMPSON_ARCLENGTH));
                    comboLength.setSelectedItem("Simpons Arclength");
                    break;
                case "Pythagorean Arclength":
                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.PYTHAGOREAN_ARCLENGTH));
                    comboLength.setSelectedItem("Pythagorean Arclength");
                    break;
                case "Richson Extrapolation Arclength":
                    fireEvent(new GuiEventsLengthChange(this, NumericalApproximation.RICHARDSON_EXTRAPOLATION_ARCLENGTH));
                    comboLength.setSelectedItem("Richson Extrapolation Arclength");
                    break;
                default:
                    System.out.println("Error in selection");
                    break;
            }
        }
    }

    /**
     * Update the info for the curve
     *
     * @param curveID The index of the current curve
     * @param info The info from the curve: Name, Area, Length, Number Of Control points, Zoom level
     * @param area The allowed algorithms to find the area of the arc
     * @param length The allowed algorithms to find the length of the arc
     */
    protected void updateInfo(int curveID, String[] info, ArrayList area, ArrayList length) {
        updating2 = true;

        if (this.curveID != curveID) {
            this.curveID = curveID;
            updateComboBox(area, length);
        }

        updating1 = true;
        updateTableFull();
        updating1 = false;

        if (info.length == 5) {
            curveInfo = info;
        }
        updateInfoPanel();
        currentLineComboBox.removeAllItems();
        for (int i = 0; i < numberOfCurve; i++) {
            currentLineComboBox.addItem(name[i]);
        }
        if (DEBUG) {
            System.out.println(curveID);
        }
        currentLineComboBox.setSelectedIndex(curveID);
        updating2 = false;
    }

    /**
     * Updates the jLabel
     */
    private void updateInfoPanel() {
        infoText[0].setText("Name " + curveInfo[0]);
        if (!curveInfo[1].equals("Infinity")) {
            infoText[1].setText("Area " + curveInfo[1]);
        } else {
            infoText[1].setText("Area 0");
        }
        infoText[2].setText("Length " + curveInfo[2]);
        infoText[3].setText("Number Of control points " + curveInfo[3]);
        infoText[4].setText("Zoom Level " + curveInfo[4]);

    }

    /**
     * Creates the JTable and the table model
     */
    private void createTable() {
        String[] header = {"Point ID", "X-coordinates", "Y-coordinates"};
        mod = new TableModel(header);
        table = new JTable(mod);
        mod.addTableModelListener(this);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (!updating1) {
            int row = e.getFirstRow();
            if (DEBUG) {
                System.out.println(row);
            }
            double fir = mod.getValueAt(row, 1);
            double sec = mod.getValueAt(row, 2);
            if (DEBUG) {
                System.out.println("Table has been changed");
            }
            fireEvent(new GuiEventsMove(this, new double[]{fir, sec}, row, curveID));
        }
    }

    /**
     * Handles the logic of the pop up
     *
     * @param e The event
     */
    private void fireEvent(GuiEvents event) {
        Iterator<GuiEventListner> i = LISTENER.iterator();
        while (i.hasNext()) {
            i.next().actionPerformed(event);
        }
    }

    /**
     * Attaches a eventListner to the object
     *
     * @param list The GuiEventListener
     */
    protected synchronized void addEventListener(GuiEventListner list) {
        LISTENER.add(list);
    }

}
