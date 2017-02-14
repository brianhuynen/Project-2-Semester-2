package ui.events;
/**
 * An event to signal the want to convert the line into another line
 * @author Brian Huyen
 */
public class GuiEventsConvert extends GuiEvents {

    private int curveID, type;

    /**
     * Constructor
     * @param source The source of the event
     * @param curveID The curve to be changed
     * @param type The type that we want to changed into
     */
    public GuiEventsConvert(Object source, int curveID, int type) {
        super(source);
        this.curveID = curveID;
        this.type = type;
    }

    /**
     * Returns the of line it should be converted to
     * @return The type
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the curve ID that we want to be affect
     * @return The curveID
     */
    public int getCurveID() {
        return curveID;
    }
}
