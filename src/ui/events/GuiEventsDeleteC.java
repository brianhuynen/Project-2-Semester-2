package ui.events;

/**
 * Event to handle the delation of a new line
 *
 * @author Kareem Horstink
 */
public class GuiEventsDeleteC extends GuiEvents {

    private int curveID;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param curveID The curveID
     */
    public GuiEventsDeleteC(Object source, int curveID) {
        super(source);
        this.curveID = curveID;
    }

    /**
     * Gets the curveID to be affected
     * @return The curve ID
     */
    public int getCurveID() {
        return curveID;
    }

}
