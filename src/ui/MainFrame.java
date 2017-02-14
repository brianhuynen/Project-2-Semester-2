package ui;

import curves.Controller;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import ui.events.GuiEventsAreaChange;
import ui.events.GuiEventsConvert;
import ui.events.GuiEventsLengthChange;
import ui.events.GuiEventListner;
import ui.events.GuiEvents;
import ui.events.GuiEventsAdd;
import ui.events.GuiEventsClose;
import ui.events.GuiEventsCreate;
import ui.events.GuiEventsCurrent;
import ui.events.GuiEventsDeleteC;
import ui.events.GuiEventsDeleteP;
import ui.events.GuiEventsMove;
import ui.events.GuiEventsOpen;
import ui.events.GuiEventsRefresh;
import ui.events.GuiEventsVisibility;

/**
 * The main frame to put everything on
 *
 * @author Kareem Horstink
 * @version 1.00
 */
public class MainFrame extends JFrame implements GuiEventListner {

    /**
     * To test the rest of the program
     *
     * @param args This will be ignored
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ex) {
            System.out.println(ex);
        }
        new MainFrame();
    }

    /**
     * The canvas
     */
    private final Canvas CANVAS;

    /**
     * The side bar
     */
    private final SideBar SIDE_BAR;

    /**
     * The controller that changes the line
     */
    private final Controller CONTROLLER;

    /**
     * The current selected line
     */
    private int curveID = -1;

    private final boolean DEBUG = false;

    /**
     * Default constructor
     */
    public MainFrame() {
        setTitle("Curves");
        CONTROLLER = new Controller();
        CANVAS = new Canvas(1, 1);
        CANVAS.setGridspacing(100);
        CANVAS.addEventListener(this);
        SIDE_BAR = new SideBar();
        add(CANVAS);
        add(SIDE_BAR, BorderLayout.EAST);
        setSize(1500, 800);
        SIDE_BAR.addEventListener(this);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width / 2 - this.getWidth() / 2, dim.height / 2 - this.getHeight() / 2);
        this.setVisible(true);
    }

    /**
     * Fully update the canvas and side bar
     */
    private void update() {
        int amount = CONTROLLER.amountOfCurves();

        if (amount > 0) {
            updateG();
            ArrayList tmpList = new ArrayList();
            for (int i = 0; i < amount; i++) {
                tmpList.add(CONTROLLER.getCurveName(i));
            }
            SIDE_BAR.setName((String[]) tmpList.toArray(new String[amount]));
            SIDE_BAR.setNumberOfCurve(amount);
            tmpList.clear();
            for (int i = 0; i < amount; i++) {
                tmpList.add(CONTROLLER.getControlsPoints(curveID));
            }
            SIDE_BAR.setCurves(tmpList);
            SIDE_BAR.updateInfo(curveID,
                    new String[]{
                        CONTROLLER.getCurveName(curveID),
                        Double.toString(CONTROLLER.curveArea(curveID)),
                        Double.toString(CONTROLLER.curveLength(curveID)),
                        Integer.toString(CONTROLLER.getControlsPoints(curveID).size()),
                        Double.toString(CANVAS.getZoom())},
                    CONTROLLER.getAllowedAlgorithmsArea(curveID),
                    CONTROLLER.getAllowedAlgorithmsLength(curveID)
            );

            if (DEBUG) {
                System.out.println("Updating Data");
            }
        } else {
            System.out.println("Insufficient amount of curves");
        }
    }

    /**
     * Only updates the graphical plot in the canvas
     */
    private void updateG() {
        int amount = CONTROLLER.amountOfCurves();
        ArrayList tmpList = new ArrayList();
        for (int i = 0; i < amount; i++) {
            tmpList.add(CONTROLLER.getCurvePlot(i, (int) (30 * CANVAS.getZoom())));
        }

        if (amount > 0) {
            CANVAS.setCurves(tmpList);
            tmpList = new ArrayList();
            for (int i = 0; i < amount; i++) {
                tmpList.add(CONTROLLER.getControlsPoints(i));
            }

            CANVAS.setControls(tmpList);
            if (DEBUG) {
                System.out.println("Updating Graphics");
            }
        } else {
            System.out.println("Insufficient amount of curves");
        }
    }

    @Override
    public void handleCreate(GuiEventsCreate e) {
        if (DEBUG) {
            System.out.println("Creating");
        }
        curveID++;
        if (e.getInfo().length == 3) {
            CONTROLLER.createCurve((int) e.getInfo()[2], e.getInfo()[0], e.getInfo()[1], e.getName());
            CONTROLLER.setDefault(curveID);

            update();
        } else if (e.getInfo().length == 4) {
            CONTROLLER.createBowlGA(e.getInfo()[3], e.getName());
            CONTROLLER.setDefault(curveID);
            update();
        } else if (e.getInfo().length == 5) {
            CONTROLLER.createEllipse(e.getInfo()[3], e.getInfo()[4], e.getName());
            CONTROLLER.setDefault(curveID);
            update();
        } else {
            System.out.println("Create line - Error");
        }
    }

    /**
     * Handles the request to converts the line to another line
     * @param e The event to be handled
     */
    public void handleConvert(GuiEventsConvert e) {
        if (DEBUG) {
            System.out.println("Converting");
        }
        CONTROLLER.convertCurveToType(e.getCurveID(), e.getType());
        updateG();
    }

    @Override
    public void handleAdd(GuiEventsAdd e) {
        if (DEBUG) {
            System.out.println("Adding a new point");
        }
        CONTROLLER.addLastPoint(e.getInfo()[0], e.getInfo()[1], e.getCurveID());
        update();
    }

    @Override
    public void handleMove(GuiEventsMove e) {
        if (DEBUG) {
            System.out.println("Moving a point");
        }
        if (e.getSource().equals(CANVAS)) {
            CONTROLLER.setPointLocation(e.getInfo()[0], e.getInfo()[1], e.getCurveID(), e.getPointID());
        } else {
            CONTROLLER.setPointLocation(e.getInfo()[0], e.getInfo()[1], e.getCurveID(), e.getPointID());
        }
        update();
    }

    @Override
    public void handleDeleteP(GuiEventsDeleteP e) {
        if (DEBUG) {
            System.out.println("Deleting a point");
        }
        CONTROLLER.removePoint(e.getCurveID(), e.getPointID());
        update();
    }

    @Override
    public void handleDeleteC(GuiEventsDeleteC e) {
        if (DEBUG) {
            System.out.println("Deleting a curve");
        }
        System.out.println("Not supported yet.");
        update();
    }

    @Override
    public void handleClose(GuiEventsClose e) {
        if (DEBUG) {
            System.out.println("Closing Curve");
        }
        CONTROLLER.closeCurve(e.getCurveID());
        update();
    }

    @Override
    public void handleOpen(GuiEventsOpen e) {
        if (DEBUG) {
            System.out.println("Closing Curve");
        }
        CONTROLLER.openCurve(e.getCurveID());
        updateG();
    }

    @Override
    public void handleVisibility(GuiEventsVisibility e) {
        if (DEBUG) {
            System.out.println("Change Visiblity");
        }
        CANVAS.setVisiblity(e.getVisiablity());
    }

    @Override
    public void handleCurrent(GuiEventsCurrent e) {
        if (DEBUG) {
            System.out.println("Changing Current Line");
        }
        curveID = e.getCurveID();
        if (!e.getSource().getClass().equals(CANVAS.getClass())) {
            CANVAS.setCurrentLine(e.getCurveID());
        } else {
            SIDE_BAR.setCurveID(e.getCurveID(), CONTROLLER.getAllowedAlgorithmsArea(e.getCurveID()), CONTROLLER.getAllowedAlgorithmsLength(e.getCurveID()));
        }
        CONTROLLER.setDefault(e.getCurveID());
        update();
    }

    @Override
    public void handleRefresh(GuiEventsRefresh e) {
        if (DEBUG) {
            System.out.println("Refreshing");
        }
        update();
    }

    @Override
    public void actionPerformed(GuiEvents e) {
        if (GuiEventsAdd.class.equals(e.getClass())) {
            handleAdd((GuiEventsAdd) e);
        } else if (GuiEventsClose.class.equals(e.getClass())) {
            handleClose((GuiEventsClose) e);
        } else if (GuiEventsMove.class.equals(e.getClass())) {
            handleMove((GuiEventsMove) e);
        } else if (GuiEventsCreate.class.equals(e.getClass())) {
            handleCreate((GuiEventsCreate) e);
        } else if (GuiEventsConvert.class.equals(e.getClass())) {
            handleConvert((GuiEventsConvert) e);
        } else if (GuiEventsCurrent.class.equals(e.getClass())) {
            handleCurrent((GuiEventsCurrent) e);
        } else if (GuiEventsDeleteC.class.equals(e.getClass())) {
            handleDeleteC((GuiEventsDeleteC) e);
        } else if (GuiEventsDeleteP.class.equals(e.getClass())) {
            handleDeleteP((GuiEventsDeleteP) e);
        } else if (GuiEventsOpen.class.equals(e.getClass())) {
            handleOpen((GuiEventsOpen) e);
        } else if (GuiEventsVisibility.class.equals(e.getClass())) {
            handleVisibility((GuiEventsVisibility) e);
        } else if (GuiEventsRefresh.class.equals(e.getClass())) {
            handleRefresh((GuiEventsRefresh) e);
        } else if (GuiEventsAreaChange.class.equals(e.getClass())) {
            handleAreaChange((GuiEventsAreaChange) e);
        } else if (GuiEventsLengthChange.class.equals(e.getClass())) {
            handleLengthChange((GuiEventsLengthChange) e);
        } else {
            System.out.println("Not handled");
        }
    }

    @Override
    public void handleAreaChange(GuiEventsAreaChange e) {
        if (DEBUG) {
            System.out.println("Changing Area");
        }
        CONTROLLER.setArea(e.getAlgorithm());
        update();
    }

    @Override
    public void handleLengthChange(GuiEventsLengthChange e) {
        if (DEBUG) {
            System.out.println("Changing Length");
        }
        CONTROLLER.setLength(e.getAlgorithm());
        update();
    }

}
