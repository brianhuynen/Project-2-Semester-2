package ui.events;

/**
 * Event to handle the changing of a line
 *
 * @author Kareem Horstink
 */
public class GuiEventsCurrent extends GuiEvents {

    private int curveID;

    /**
     * Constructor
     *
     * @param source The source of the event
     * @param curveID The Curve ID of the new selected ID
     */
    public GuiEventsCurrent(Object source, int curveID) {
        super(source);
        this.curveID = curveID;
    }

    /**
     * Returns the curve ID
     * @return Curve ID
     */
    public int getCurveID() {
        return curveID;
    }

}
