package ui.events;

/**
 * Event to handle the creation of a new line
 *
 * @author Kareem Horstink
 * @version 1.00
 */
public class GuiEventsMove extends GuiEvents {

    private int pointID;
    private double[] info;
    private int curveID;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param info The info about the line [x-location,y-location]
     * @param pointID The point ID
     * @param curveID The curve ID
     */
    public GuiEventsMove(Object source, double[] info, int pointID, int curveID) {
        super(source);
        this.info = info;
        this.curveID = curveID;
        this.pointID = pointID;
    }

    /**
     * Returns the pointID
     *
     * @return The pointID
     */
    public int getPointID() {
        return pointID;
    }

    /**
     * Returns the info
     *
     * @return The info about the line [x-location,y-location]
     */
    public double[] getInfo() {
        return info;
    }

    /**
     * Returns the curveID
     * @return The curveID
     */
    public int getCurveID() {
        return curveID;
    }

}
