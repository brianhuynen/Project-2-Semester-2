package ui.events;

/**
 * Event to handle the creation of a new line
 *
 * @author Kareem Horstink
 */
public class GuiEventsOpen extends GuiEvents {

    private int curveID;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param curveID The curveID
     */
    public GuiEventsOpen(Object source, int curveID) {
        super(source);
        this.curveID = curveID;
    }

    /**
     * Returns the curveID
     *
     * @return The curveID
     */
    public int getCurveID() {
        return curveID;
    }

}
