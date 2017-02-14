package ui;

import curves.Controller;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import ui.events.GuiEventListner;
import ui.events.GuiEvents;
import ui.events.GuiEventsAdd;
import ui.events.GuiEventsClose;
import ui.events.GuiEventsConvert;
import ui.events.GuiEventsCreate;
import ui.events.GuiEventsDeleteP;
import ui.events.GuiEventsMove;
import ui.events.GuiEventsOpen;
import ui.events.GuiEventsRefresh;

/**
 * The canvas in which to draw various elements of the UI, mostly focusing on the graphical side of
 * things as well intuitive controls
 *
 * @author Kareem Horstink
 * @version 1.00
 */
public class Canvas extends JPanel implements ActionListener {

    /**
     * List of the gui listeners attached to the object
     */
    private final List<GuiEventListner> LISTENERS = new ArrayList<>();

    /**
     * The offset of the view port to the origin
     */
    private double offSetX = 0;
    /**
     * The offset of the view port to the origin
     */
    private double offSetY = 0;

    /**
     * The zoom level
     */
    private double zoom = 1;

    /**
     * The units to be shown the grid spacing
     */
    private double units = 1;
    /* *
     *The spacing between grid line in pixels
     */
    private double gridspacing = 100;

    /**
     * A list of the curve
     */
    private ArrayList<List<Point2D>> curves = new ArrayList<>();

    /**
     * List of the control point (only for current line)
     */
    private List<Point2D> controls = new ArrayList<>();

    /**
     * List of the colors for the line
     */
    private final ArrayList<Color> COLORS = new ArrayList<>();

    /**
     * The visiblity of the line
     */
    private boolean Visiblity = true;

    /**
     * The current selected line
     */
    private int curveID = -1;

    /**
     * Used to see if the points are being edit or not
     */
    private boolean first = false;

    /**
     * The pop up menus
     */
    private JPopupMenu popup1;
    /**
     * The pop up menus
     */
    private JPopupMenu popup2;

    /**
     * Point of where the user open the pop
     */
    private Point2D.Double point = new Point2D.Double();

    /**
     * The list of clickable area
     */
    private ArrayList<Ellipse2D> controlPoints;

    /**
     * Which current line is being selected
     */
    private int selectPoint;

    /**
     * If the point is to be moved
     */
    private boolean moveSelected;

    private final boolean DEBUG = false;

    /**
     * Creates a new canvas and sets the default zoom level as well the units to be used by the grid
     *
     * @param zoom The current zoom level
     * @param units The units to be shown on the grid
     */
    protected Canvas(double zoom, double units) {
        setBackground(Color.DARK_GRAY);
        init();
        this.zoom = zoom;
        this.units = units;
        this.setSize(400, 500);
    }

    /**
     * Creates the overall interface
     */
    private void init() {
        /*
         * Creates the first popup menu
         * New line;Close curve; Open curve
         */
        popup1 = new JPopupMenu();

        JMenuItem menuItem = new JMenuItem("New Line");
        menuItem.addActionListener(this);
        popup1.add(menuItem);
        menuItem = new JMenuItem("Close Curve");
        menuItem.addActionListener(this);
        popup1.add(menuItem);
        menuItem = new JMenuItem("Open Curve");
        menuItem.addActionListener(this);
        popup1.add(menuItem);
        menuItem = new JMenuItem("Change Curve");
        menuItem.addActionListener(this);
        popup1.add(menuItem);

        /*
         * Creates the second popup menu
         * Move point; delete point
         */
        popup2 = new JPopupMenu();
        menuItem = new JMenuItem("Move point");
        menuItem.addActionListener(this);
        popup2.add(menuItem);
        menuItem = new JMenuItem("Delete point");
        menuItem.addActionListener(this);
        popup2.add(menuItem);

        /*
         * Add a mouse listener to the canvas
         * Add new point; Open popup 1; Open popup 2;
         * Some logic for dragging points; Enter coordiantes
         */
        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.isControlDown()) {
                    if (curveID != -1) {
                        fireEvent(new GuiEventsAdd(this, new double[]{xm(e.getX()), ym(e.getY())}, curveID));
                    }
                } else if (SwingUtilities.isRightMouseButton(e) && e.isControlDown()) {
                    point.setLocation(xm(e.getX()), ym(e.getY()));
                    openNewPopUpCoordiante();
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    point.setLocation(xm(e.getX()), ym(e.getY()));
                    popup1.show(e.getComponent(), e.getX(), e.getY());
                    repaint();
                } else if (SwingUtilities.isRightMouseButton(e) && e.isShiftDown()) {
                    fireEvent(new GuiEventsRefresh(this));
                }
                if (controlPoints != null) {
                    for (int i = 0; i < controlPoints.size(); i++) {
                        if (controlPoints.get(i).contains(e.getX(), e.getY())) {
                            selectPoint = i;
                            moveSelected = true;
                            if (SwingUtilities.isRightMouseButton(e)) {
                                popup2.show(e.getComponent(), e.getX(), e.getY());
                            }
                        }
                    }
                }
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                first = false;
                moveSelected = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        /*
         * Add the motion mouse listener
         * Move point
         */
        addMouseMotionListener(new MouseMotionListener() {
            int preX = 0;
            int preY = 0;

            @Override
            public void mouseDragged(MouseEvent e) {
                if (!first) {
                    first = true;
                    preX = e.getX();
                    preY = e.getY();
                } else if (e.isShiftDown()) {
                    offSetX -= preX - e.getX();
                    offSetY += preY - e.getY();
                    updateControls();
                    repaint();
                    preX = e.getX();
                    preY = e.getY();
                } else if (moveSelected) {
                    fireEvent(new GuiEventsMove(this, new double[]{xm(e.getX()), ym(e.getY())}, selectPoint, curveID));
                }

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                repaint();
            }
        });

        /*
         *  Mouse wheel listener to control zoom level
         */
        addMouseWheelListener((MouseWheelEvent e) -> {
            if (Math.signum(e.getPreciseWheelRotation()) == 1) {
                if (getZoom() > 0.4356029233981616) {
                    setZoom(Math.abs(getZoom() / (e.getPreciseWheelRotation() * 1.05)));
                }
            } else {
                setZoom(Math.abs(e.getPreciseWheelRotation() * 1.05 * getZoom()));
            }
            fireEvent(new GuiEventsRefresh(this));
        });

    }

    /**
     * Open a menu to enter coordiante of a new point
     */
    private void openNewPopUpCoordiante() {
        String tmp = JOptionPane.showInputDialog(this, "Enter Coordiante", "0.0, 0.0");
        try {
            tmp = tmp.replace(" ", "");
            String split[] = tmp.split(",");
            double x = Double.valueOf(split[0]);
            double y = Double.valueOf(split[1]);
            fireEvent(new GuiEventsAdd(this, new double[]{x, y}, curveID));
        } catch (Exception e) {
            System.out.println("Please enter a proper location " + e);
        }
    }

    /**
     * Open a menu to enter coordiante to move a point
     */
    private void openNewPopUpCoordianteMove() {
        String tmp = JOptionPane.showInputDialog(this, "Enter Coordiante", "0.0, 0.0");
        try {
            tmp = tmp.replace(" ", "");
            String split[] = tmp.split(",");
            double x = Double.valueOf(split[0]);
            double y = Double.valueOf(split[1]);
            if (DEBUG) {
                System.out.println("x " + x + ", y " + y);
                System.out.println("point " + selectPoint);
                System.out.println("curve " + curveID);
            }
            fireEvent(new GuiEventsMove(this, new double[]{x, y}, selectPoint, curveID));
        } catch (Exception e) {
            System.out.println("Please enter a proper location " + e);
        }
    }

    /**
     * Paints the Canvas
     *
     * @param g The graphics
     */
    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paint(g);
        drawGrid(g2);
        drawLines(g2);
        drawControls(g2);
    }

    /**
     * Draws the control points
     *
     * @param g
     */
    private void drawControls(Graphics2D g) {
        if (curveID != -1) {
            g.setColor(COLORS.get(curveID));
            for (Ellipse2D control : controlPoints) {
                g.draw(control);
            }
        }
    }

    /**
     * Draws the grid line
     *
     * @param g
     */
    private void drawGrid(Graphics2D g) {
        for (int i = (int) (-1000); i < (int) (1000); i++) {
            g.setColor(Color.lightGray);
            g.drawLine(Integer.MAX_VALUE * -1, (int) y((int) (i * gridspacing)), Integer.MAX_VALUE, (int) y((int) (i * gridspacing)));
            g.drawLine((int) x((int) (i * gridspacing)), Integer.MAX_VALUE * -1, (int) x((int) (i * gridspacing)), Integer.MAX_VALUE);
            g.drawString(Double.toString(i * units), (int) x((int) (i * gridspacing + 5)), (int) y(0) - 2);
            g.drawString(Double.toString(i * units), (int) x((int) (5)), (int) y((int) (i * gridspacing)) - 2);
        }
    }

    /**
     * Draws the curve/spline
     *
     * @param g
     */
    private void drawLines(Graphics2D g) {
        int counter = 0;
        for (List<Point2D> curve : curves) {
            if (Visiblity || counter == curveID) {
                boolean fir = true;
                Path2D.Double tmp = new Path2D.Double(Path2D.WIND_NON_ZERO, 1);
                g.setStroke(new BasicStroke(3f));
                colorPicker();
                g.setColor(COLORS.get(counter));
                for (Point2D points : curve) {
                    if (fir) {
                        fir = false;
                        tmp.moveTo(x(points.getX()), y(points.getY()));
                    } else {
                        tmp.lineTo(x(points.getX()), y(points.getY()));
                    }
                }
                g.draw(tmp);
            }
            counter++;
        }
    }

    /**
     * Creates random colors
     */
    private void colorPicker() {
        Random r = new Random();
        while (COLORS.size() != curves.size()) {
            COLORS.add(new Color(100 + r.nextInt(155), 100 + r.nextInt(155), 100 + r.nextInt(155)));
        }
    }

    /**
     * Sets the current selected curve/spline
     *
     * @param currentLine The index of the current selected curve/spline
     */
    protected void setCurrentLine(int currentLine) {
        this.curveID = currentLine;
    }

    /**
     * Set the value of curves
     *
     * @param curves new value of curves
     */
    protected void setCurves(ArrayList<List<Point2D>> curves) {
        this.curves = curves;
        repaint();
    }

    /**
     * Get the value of zoom
     *
     * @return the value of zoom
     */
    protected double getZoom() {
        return zoom;
    }

    /**
     * Set the value of zoom
     *
     * @param zoom new value of zoom
     */
    protected void setZoom(double zoom) {
        this.zoom = zoom;
        updateControls();
        repaint();
    }

    /**
     * Give every event listener attached to this object the event
     *
     * @param event The event to be passed
     */
    private void fireEvent(GuiEvents event) {
        Iterator<GuiEventListner> i = LISTENERS.iterator();
        while (i.hasNext()) {
            i.next().actionPerformed(event);
        }
    }

    /**
     * Handles the logic of the pop up
     *
     * @param e The event
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        /*
         *Checks if the source of the event comes from the popup menu
         */
        if (e.getSource().getClass().isInstance(new JMenuItem())) {
            //Converts it to a JMenuItem
            JMenuItem tmp = (JMenuItem) e.getSource();

            /*
             *Checks event comes from the first popup or the 2nd
             */
            if (tmp.getParent().equals(popup1)) {

                /*
                 *Does the action if the user clicks on "New Line" 
                 * or "Close curve" or "Open curve"
                 */
                if (e.getActionCommand().equals("New Line")) {
                    String optionSelected = (String) JOptionPane.showInputDialog(this, "Please Select a Line Type", "Line Type", JOptionPane.QUESTION_MESSAGE, null, new String[]{
                        "PolyLine",
                        "Cubic Line Natural",
                        "B-Spline",
                        "Bezier Curve",
                        "Bezier Spline",
                        "Bezier Spline Colinear",
                        "Ellipse",
                        "Bowl - GA",
                        "Bowl - SemiCircle"
                    }, null);
                    /*
                     *Fires the currect event based on what was selected
                     */
                    if (optionSelected != null) {
                        String name = (String) JOptionPane.showInputDialog(this, "Please Give the Line a name", "Line Name", JOptionPane.QUESTION_MESSAGE);
                        if (name != null) {
                            curveID++;
                            switch (optionSelected) {
                                case "PolyLine":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.POLYLINE}, name));
                                    break;
                                case "Cubic Line Natural":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.CUBIC_N}, name));
                                    break;
                                case "Bezier Curve":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.BEZIERCURVE}, name));
                                    break;
                                case "Bezier Spline":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.BEZIERSPLINE}, name));
                                    break;
                                case "Bezier Spline Colinear":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.BEZIERSPLINECOLINEAR}, name));
                                    break;
                                case "B-Spline":
                                    fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.BSPLINE}, name));
                                    break;
                                case "Bowl - SemiCircle":
                                    String length1 = JOptionPane.showInputDialog(this, "Enter the target length", "0.0");
                                    try {
                                        length1 = length1.replace(" ", "");
                                        double len = Double.valueOf(length1);
                                        fireEvent(new GuiEventsCreate(this, new double[]{len, point.y, Controller.BOWLSC}, name));
                                    } catch (Exception error) {
                                        System.out.println("Please enter a proper location " + error);
                                        curveID--;
                                    }
                                    break;
                                case "Ellipse":
                                    String numbers = JOptionPane.showInputDialog(this, "Enter Numbers based on \n x(t) = a cos(t) and y(t) = c sin(t)", "3.0, 3.0");
                                    try {
                                        numbers = numbers.replace(" ", "");
                                        String split[] = numbers.split(",");
                                        if (split.length == 2) {
                                            double x = Double.valueOf(split[0]);
                                            double z = Double.valueOf(split[1]);
                                            fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.ELLIPSE, x, z}, name));
                                        } else {
                                            JOptionPane.showMessageDialog(this, "Please input 2 numbers");
                                            curveID--;
                                        }
                                    } catch (Exception error) {
                                        System.out.println("Please enter a proper location " + error);
                                    }
                                    break;
                                case "Bowl - GA":
                                    String length = JOptionPane.showInputDialog(this, "Enter the target length", "0.0");
                                    try {
                                        length = length.replace(" ", "");
                                        double len = Double.valueOf(length);
                                        fireEvent(new GuiEventsCreate(this, new double[]{point.x, point.y, Controller.BOWLGA, len}, name));
                                    } catch (Exception error) {
                                        System.out.println("Please enter a proper location " + error);
                                        curveID--;
                                    }
                                    break;
                                default:
                                    System.out.println("Option Panel is incorrect");
                            }
                        }
                    }
                } else if (e.getActionCommand().equals("Close Curve")) {
                    if (curveID != -1) {
                        fireEvent(new GuiEventsClose(this, curveID));
                    }
                } else if (e.getActionCommand().equals("Open Curve")) {
                    fireEvent(new GuiEventsOpen(this, curveID));
                } else if (e.getActionCommand().equals("Change Curve")) {
                    String optionSelected = (String) JOptionPane.showInputDialog(this, "Change Curve Type to:", "Change Curve", JOptionPane.QUESTION_MESSAGE, null, new String[]{
                        "PolyLine",
                        "Cubic Line Natural",
                        "Bezier Curve",
                        "Bezier Spline",
                        "Bezier Spline Colinear"
                    }, null);
                    if (optionSelected != null) {
                        switch (optionSelected) {
                            case "PolyLine":
                                fireEvent(new GuiEventsConvert(this, curveID, Controller.POLYLINE));
                                break;
                            case "Cubic Line Natural":
                                fireEvent(new GuiEventsConvert(this, curveID, Controller.CUBIC_N));
                                break;
                            case "Bezier Curve":
                                fireEvent(new GuiEventsConvert(this, curveID, Controller.BEZIERCURVE));
                                break;
                            case "Bezier Spline":
                                fireEvent(new GuiEventsConvert(this, curveID, Controller.BEZIERSPLINE));
                                break;
                            case "Bezier Spline Colinear":
                                fireEvent(new GuiEventsConvert(this, curveID, Controller.BEZIERSPLINECOLINEAR));
                                break;
                        }
                    }
                }
            } else {
                if (e.getActionCommand().equals("Move point")) {
                    openNewPopUpCoordianteMove();
                } else if (e.getActionCommand().equals("Delete point")) {
                    fireEvent(new GuiEventsDeleteP(curves, selectPoint, curveID));
                }
            }
        }
        repaint();
    }

    /**
     * Attaches a eventListner to the object
     *
     * @param list The GuiEventListener
     */
    protected synchronized void addEventListener(GuiEventListner list) {
        LISTENERS.add(list);
    }

    /**
     * Calculates the correct location of a point based on the mouse x location
     *
     * @param x The mouse x location
     * @return The corrected x location
     */
    private double xm(double x) {
        return ((x - getVisibleRect().width / 2 - offSetX) / zoom) / gridspacing;
    }

    /**
     * Calculates the correct location of a point based on the mouse y location
     *
     * @param y The mouse y location
     * @return The corrected y location
     */
    private double ym(double y) {
        return ((getVisibleRect().height / 2 - y - offSetY) / zoom) / gridspacing;
    }

    /**
     * Corrects the location so that the origin is in the center of the screen
     *
     * @param x The x location the object
     * @return The corrected x location
     */
    private double x(double x) {
        return (zoom * (x * gridspacing) + offSetX + getVisibleRect().width / 2);
    }

    /**
     * Corrects the location so that the origin is in the center of the screen
     *
     * @param x The x location the object
     * @return The corrected x location
     */
    private double x(int x) {
        return (zoom * (x) + offSetX + getVisibleRect().width / 2);
    }

    /**
     * Corrects the location so that the origin is in the center of the screen
     *
     * @param y The y location the object
     * @return The corrected y location
     */
    private double y(double y) {
        return ((zoom * getVisibleRect().height / 2 - zoom * y * gridspacing) - ((zoom - 1) * getVisibleRect().height / 2) - offSetY);
    }

    /**
     * Corrects the location so that the origin is in the center of the screen
     *
     * @param y The y location the object
     * @return The corrected y location
     */
    private double y(int y) {
        return ((zoom * getVisibleRect().height / 2 - zoom * y) - ((zoom - 1) * getVisibleRect().height / 2) - offSetY);
    }

    /**
     * Sets the controls points
     *
     * @param controls The controls to be changed
     */
    protected void setControls(ArrayList<List<Point2D>> controls) {
        if (curveID != -1) {
            this.controls = controls.get(curveID);
            updateControls();
        }

    }

    /**
     * Updates the control points draggable area
     */
    private void updateControls() {
        controlPoints = new ArrayList<>();
        double size = 15;
        for (Point2D control : controls) {
            controlPoints.add(new Ellipse2D.Double(x(control.getX()) - size / 2, y(control.getY()) - size / 2, size, size));
        }
    }

    /**
     * Sets the units to be used for the grid spacing
     *
     * @param units The units to be used
     */
    protected void setUnits(double units) {
        this.units = units;
    }

    /**
     * Sets the spacing between the grid line
     *
     * @param gridspacing The spacing between grid line in pixels
     */
    protected void setGridspacing(double gridspacing) {
        this.gridspacing = gridspacing;
    }

    /**
     * Set if other lines are visible
     *
     * @param visiblity Boolean to set if its true or false
     */
    protected void setVisiblity(boolean visiblity) {
        this.Visiblity = visiblity;
        repaint();
    }
}
