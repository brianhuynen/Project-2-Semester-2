package ui.events;

/**
 * Event to handle the closing of a line
 *
 * @author Kareem Horstink
 */
public class GuiEventsClose extends GuiEvents {

    private int curveID;

    /**
     * Creation of the event
     *
     * @param source The source of the event
     * @param curveID The curve for the point to be added
     */
    public GuiEventsClose(Object source, int curveID) {
        super(source);
        this.curveID = curveID;
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
