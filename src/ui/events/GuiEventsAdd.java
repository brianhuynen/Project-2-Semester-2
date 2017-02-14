package ui.events;

/**
 * Event to handle the creation of a new point
 *
 * @author Kareem Horstink
 * @version 1.0
 */
public class GuiEventsAdd extends GuiEvents {

    private double[] info;
    private int curveID;

    /**
     * Creation of the event
     *
     * @param source The source of the event
     * @param info The information need for the creation of a new curve: x-coordinate; y-coordinate
     * @param curveID The curve for the point to be added
     */
    public GuiEventsAdd(Object source, double[] info, int curveID) {
        super(source);
        this.info = info;
        this.curveID = curveID;
    }

    /**
     * Returns the needed info
     *
     * @return Returns the info of x-coordinate; y-coordinate
     */
    public double[] getInfo() {
        return info;
    }

    /**
     * Gets the curve ID
     *
     * @return Returns an int that represent the curve ID
     */
    public int getCurveID() {
        return curveID;
    }

}
