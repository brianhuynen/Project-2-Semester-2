package ui.events;

/**
 * Event to handle the creation of a new line
 *
 * @author Kareem Horstink
 */
public class GuiEventsDeleteP extends GuiEvents {

    private int pointID;
    private int curveID;

    /**
     * Constructor
     * @param source The source of the event
     * @param pointID The pointID to be deleted
     * @param curveID The curveID to be affected
     */
    public GuiEventsDeleteP(Object source, int pointID, int curveID) {
        super(source);
        this.curveID = curveID;
        this.pointID = pointID;
    }

    /**
     * Returns the pointID
     * @return The pointID to be deleted
     */
    public int getPointID() {
        return pointID;
    }

    /**
     * Return of curveID
     * @return The curveID to be affected
     */
    public int getCurveID() {
        return curveID;
    }

}
